package com.itemis.maven.plugins.unleash;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.RepositoryUtils;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Scm;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.PluginParameterExpressionEvaluator;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.w3c.dom.Document;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.itemis.maven.aether.ArtifactCoordinates;
import com.itemis.maven.plugins.cdi.logging.Logger;
import com.itemis.maven.plugins.unleash.util.PomPropertyResolver;
import com.itemis.maven.plugins.unleash.util.PomUtil;
import com.itemis.maven.plugins.unleash.util.ReleaseUtil;
import com.itemis.maven.plugins.unleash.util.Repository;
import com.itemis.maven.plugins.unleash.util.functions.ProjectToCoordinates;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

/**
 * Provides global metadata used during the release process. These metadata evolve during the release process.
 *
 * @author <a href="mailto:stanley.hillner@itemis.de">Stanley Hillner</a>
 * @since 1.0.0
 */
@Singleton
public class ReleaseMetadata {
  private static final String PROPERTIES_KEY_REL_ARTIFACT = "release.artifact.";
  private static final String PROPERTIES_KEY_REL_REPO_URL = "release.deploymentRepository.url";
  private static final String PROPERTIES_KEY_REL_REPO_ID = "release.deploymentRepository.id";
  private static final String PROPERTIES_KEY_SCM_REV_AFTER_DEV = "scm.rev.afterNextDev";
  private static final String PROPERTIES_KEY_SCM_REV_BEFORE_DEV = "scm.rev.beforeNextDev";
  private static final String PROPERTIES_KEY_SCM_REV_AFTER_TAG = "scm.rev.afterTag";
  private static final String PROPERTIES_KEY_SCM_REV_BEFORE_TAG = "scm.rev.beforeTag";
  private static final String PROPERTIES_KEY_SCM_REV_INITIAL = "scm.rev.initial";
  private static final String PROPERTIES_KEY_TAG_PATTERN = "scm.tag.namePattern";
  private static final String PROPERTIES_KEY_TAG_NAME = "scm.tag.name";
  private static final String PROPERTIES_KEY_VERSION_REACTOR_PRE_RELEASE = "version.reactor.pre.release";
  private static final String PROPERTIES_KEY_VERSION_REACTOR_RELEASE = "version.reactor.release";
  private static final String PROPERTIES_KEY_VERSION_REACTOR_POST_RELEASE = "version.reactor.post.release";
  private static final Pattern ALT_LEGACY_REPO_SYNTAX_PATTERN = Pattern.compile("(.+?)::(.+?)::(.+)");
  private static final Pattern ALT_REPO_SYNTAX_PATTERN = Pattern.compile("(.+?)::(.+)");

  @Inject
  private Logger log;
  @Inject
  private MavenProject project;
  @Inject
  private Settings settings;
  @Inject
  private MavenSession session;
  @Inject
  private PluginParameterExpressionEvaluator expressionEvaluator;
  @Inject
  @Named("tagNamePattern")
  private String tagNamePattern;
  @Inject
  @Named("reactorProjects")
  private List<MavenProject> reactorProjects;
  @Inject
  @Named("profiles")
  private List<String> profiles;
  @Inject
  @Named("releaseArgs")
  private Properties releaseArgs;
  @Inject
  @Named("altDeploymentRepository")
  private String altDeploymentRepository;
  @Inject
  @Named("altSnapshotDeploymentRepository")
  private String altSnapshotDeploymentRepository;
  @Inject
  @Named("altReleaseDeploymentRepository")
  private String altReleaseDeploymentRepository;

  private String initialScmRevision;
  private String scmRevisionBeforeNextDevVersion;
  private String scmRevisionAfterNextDevVersion;
  private String scmRevisionBeforeTag;
  private String scmRevisionAfterTag;
  private Map<ReleasePhase, Set<ArtifactCoordinates>> artifactCoordinates;
  private String scmTagName;
  private RemoteRepository deploymentRepository;
  private Set<Artifact> releaseArtifacts;
  private Map<ArtifactCoordinates, Scm> cachedScmSettings;
  private Map<ArtifactCoordinates, Document> originalPOMs;

  private ReleaseMetadata() {
    int numPhases = ReleasePhase.values().length;
    this.artifactCoordinates = Maps.newHashMapWithExpectedSize(numPhases);
    for (ReleasePhase phase : ReleasePhase.values()) {
      this.artifactCoordinates.put(phase, Sets.<ArtifactCoordinates> newHashSet());
    }
    this.cachedScmSettings = Maps.newHashMap();
    this.originalPOMs = new HashMap<>();
  }

  /**
   * CDI managed initialization.
   *
   * @throws RuntimeException in case of a {@link MojoExecutionException} thrown inside to meet the CDI
   *                            {@link PostConstruct} convention.
   */
  @PostConstruct
  public void init() throws RuntimeException {
    // setting the artifact version to a release version temporarily since the dist repository checks for a snapshot
    // version of the artifact. Maybe this can be implemented in a different manner but then we would have to setup the
    // repository manually
    org.apache.maven.artifact.Artifact projectArtifact = this.project.getArtifact();
    String oldVersion = projectArtifact.getVersion();
    projectArtifact.setVersion("1");

    try {
      this.deploymentRepository = getEffectiveDeploymentRepository();
    } catch (MojoExecutionException e) {
      throw new RuntimeException(e.getMessage(), e);
    }

    // resetting the artifact version
    projectArtifact.setVersion(oldVersion);

    for (MavenProject p : this.reactorProjects) {
      // puts the initial module artifact coordinates into the cache
      addArtifactCoordinates(ProjectToCoordinates.POM.apply(p), ReleasePhase.PRE_RELEASE);

      // caching of SCM settings of every POM in order to go back to it before setting next dev version
      this.cachedScmSettings.put(ProjectToCoordinates.EMPTY_VERSION.apply(p), p.getModel().getScm());

      Optional<Document> parsedPOM = PomUtil.parsePOM(p);
      if (parsedPOM.isPresent()) {
        this.originalPOMs.put(ProjectToCoordinates.EMPTY_VERSION.apply(p), parsedPOM.get());
      }
    }
  }

  public void setInitialScmRevision(String scmRevision) {
    this.initialScmRevision = scmRevision;
  }

  public String getInitialScmRevision() {
    return this.initialScmRevision;
  }

  public void setScmRevisionBeforeNextDevVersion(String scmRevisionBeforeNextDevVersion) {
    this.scmRevisionBeforeNextDevVersion = scmRevisionBeforeNextDevVersion;
  }

  public String getScmRevisionBeforeNextDevVersion() {
    return this.scmRevisionBeforeNextDevVersion;
  }

  public void setScmRevisionAfterNextDevVersion(String scmRevisionAfterNextDevVersion) {
    this.scmRevisionAfterNextDevVersion = scmRevisionAfterNextDevVersion;
  }

  public String getScmRevisionAfterNextDevVersion() {
    return this.scmRevisionAfterNextDevVersion;
  }

  public void setScmRevisionBeforeTag(String scmRevisionBeforeTag) {
    this.scmRevisionBeforeTag = scmRevisionBeforeTag;
  }

  public String getScmRevisionBeforeTag() {
    return this.scmRevisionBeforeTag;
  }

  public void setScmRevisionAfterTag(String scmRevisionAfterTag) {
    this.scmRevisionAfterTag = scmRevisionAfterTag;
  }

  public String getScmRevisionAfterTag() {
    return this.scmRevisionAfterTag;
  }

  public void addArtifactCoordinates(ArtifactCoordinates coordinates, ReleasePhase phase) {
    this.artifactCoordinates.get(phase).add(coordinates);
  }

  public Map<ReleasePhase, ArtifactCoordinates> getArtifactCoordinatesByPhase(String groupId, String artifactId) {
    Map<ReleasePhase, ArtifactCoordinates> result = Maps.newHashMapWithExpectedSize(this.artifactCoordinates.size());
    for (ReleasePhase phase : this.artifactCoordinates.keySet()) {
      for (ArtifactCoordinates coordinates : this.artifactCoordinates.get(phase)) {
        if (Objects.equal(coordinates.getArtifactId(), artifactId)
            && Objects.equal(coordinates.getGroupId(), groupId)) {
          result.put(phase, coordinates);
          break;
        }
      }
    }
    return result;
  }

  public String getScmTagName() {
    if (this.scmTagName == null) {
      this.scmTagName = ReleaseUtil.getScmPatternResolved(this.tagNamePattern, this.project, this.expressionEvaluator);
    }
    return this.scmTagName;
  }

  public RemoteRepository getDeploymentRepository() {
    return this.deploymentRepository;
  }

  public void addReleaseArtifact(Artifact artifact) {
    if (this.releaseArtifacts == null) {
      this.releaseArtifacts = Sets.newHashSet();
    }
    this.releaseArtifacts.add(artifact);
  }

  public Set<Artifact> getReleaseArtifacts() {
    return this.releaseArtifacts;
  }

  public Scm getCachedScmSettings(MavenProject p) {
    return this.cachedScmSettings.get(ProjectToCoordinates.EMPTY_VERSION.apply(p));
  }

  public Document getCachedOriginalPOM(MavenProject p) {
    return this.originalPOMs.get(ProjectToCoordinates.EMPTY_VERSION.apply(p));
  }

  public Properties toProperties() {
    Properties p = new Properties();
    addVersionInfo(p);
    addScmTagInfo(p);
    addScmRevisions(p);
    addDeploymentRepositoryInfo(p);
    addReleaseArtifacts(p);
    return p;
  }

  private void addVersionInfo(Properties p) {
    Map<ReleasePhase, ArtifactCoordinates> reactorCoordinates = getArtifactCoordinatesByPhase(this.project.getGroupId(),
        this.project.getArtifactId());

    if (reactorCoordinates != null) {
      ArtifactCoordinates preReleaseCoordinates = reactorCoordinates.get(ReleasePhase.PRE_RELEASE);
      ArtifactCoordinates releaseCoordinates = reactorCoordinates.get(ReleasePhase.RELEASE);
      ArtifactCoordinates postReleaseCoordinates = reactorCoordinates.get(ReleasePhase.POST_RELEASE);

      if (preReleaseCoordinates != null) {
        p.setProperty(PROPERTIES_KEY_VERSION_REACTOR_PRE_RELEASE, preReleaseCoordinates.getVersion());
      }
      if (releaseCoordinates != null) {
        p.setProperty(PROPERTIES_KEY_VERSION_REACTOR_RELEASE, releaseCoordinates.getVersion());
      }
      if (postReleaseCoordinates != null) {
        p.setProperty(PROPERTIES_KEY_VERSION_REACTOR_POST_RELEASE, postReleaseCoordinates.getVersion());
      }
    }
  }

  private void addScmTagInfo(Properties p) {
    p.setProperty(PROPERTIES_KEY_TAG_PATTERN, this.tagNamePattern);
    p.setProperty(PROPERTIES_KEY_TAG_NAME, this.scmTagName != null ? this.scmTagName : StringUtils.EMPTY);
  }

  private void addScmRevisions(Properties p) {
    p.setProperty(PROPERTIES_KEY_SCM_REV_INITIAL,
        this.initialScmRevision != null ? this.initialScmRevision : StringUtils.EMPTY);
    p.setProperty(PROPERTIES_KEY_SCM_REV_BEFORE_TAG,
        this.scmRevisionBeforeTag != null ? this.scmRevisionBeforeTag : StringUtils.EMPTY);
    p.setProperty(PROPERTIES_KEY_SCM_REV_AFTER_TAG,
        this.scmRevisionAfterTag != null ? this.scmRevisionAfterTag : StringUtils.EMPTY);
    p.setProperty(PROPERTIES_KEY_SCM_REV_BEFORE_DEV,
        this.scmRevisionBeforeNextDevVersion != null ? this.scmRevisionBeforeNextDevVersion : StringUtils.EMPTY);
    p.setProperty(PROPERTIES_KEY_SCM_REV_AFTER_DEV,
        this.scmRevisionAfterNextDevVersion != null ? this.scmRevisionAfterNextDevVersion : StringUtils.EMPTY);
  }

  private void addReleaseArtifacts(Properties p) {
    if (this.releaseArtifacts == null) {
      return;
    }

    int index = 0;
    for (Artifact a : this.releaseArtifacts) {
      p.setProperty(PROPERTIES_KEY_REL_ARTIFACT + index, a.toString());
      index++;
    }
  }

  private void addDeploymentRepositoryInfo(Properties p) {
    p.setProperty(PROPERTIES_KEY_REL_REPO_ID,
        this.deploymentRepository != null ? this.deploymentRepository.getId() : "");
    p.setProperty(PROPERTIES_KEY_REL_REPO_URL,
        this.deploymentRepository != null ? this.deploymentRepository.getUrl() : "");
  }

  /**
   * Taken from original maven-deploy-plugin <code>DeployMojo.getDeploymentRepository()</code>.
   * Plus keeping this plugin's feature regarding property replacement in remote repository URL.
   *
   * @see https://github.com/apache/maven-deploy-plugin/blob/maven-deploy-plugin-3.1.4/src/main/java/org/apache/maven/plugins/deploy/DeployMojo.java
   * @see https://github.com/shillner/unleash-maven-plugin/pull/101
   */
  private RemoteRepository getEffectiveDeploymentRepository() throws MojoExecutionException {
    RemoteRepository repo = null;

    String altDeploymentRepo;
    if (ArtifactUtils.isSnapshot(this.project.getVersion()) && this.altSnapshotDeploymentRepository != null) {
      altDeploymentRepo = this.altSnapshotDeploymentRepository;
    } else if (!ArtifactUtils.isSnapshot(this.project.getVersion()) && this.altReleaseDeploymentRepository != null) {
      altDeploymentRepo = this.altReleaseDeploymentRepository;
    } else {
      altDeploymentRepo = this.altDeploymentRepository;
    }

    if (altDeploymentRepo != null) {
      this.log.info("Using alternate deployment repository " + altDeploymentRepo);

      Matcher matcher = ALT_LEGACY_REPO_SYNTAX_PATTERN.matcher(altDeploymentRepo);

      if (matcher.matches()) {
        String id = matcher.group(1).trim();
        String layout = matcher.group(2).trim();
        String url = matcher.group(3).trim();

        if ("default".equals(layout)) {
          this.log
              .warn("Using legacy syntax for alternative repository. " + "Use \"" + id + "::" + url + "\" instead.");
          repo = Repository.buildRemoteRepository(id, url, this.session,
              new PomPropertyResolver(this.project, this.settings, this.profiles, this.releaseArgs));
        } else {
          throw new MojoExecutionException(
              "Invalid legacy syntax and layout for alternative repository: \"" + altDeploymentRepo + "\". Use \"" + id
                  + "::" + url + "\" instead, since only default layout is supported.");
        }
      } else {
        matcher = ALT_REPO_SYNTAX_PATTERN.matcher(altDeploymentRepo);

        if (!matcher.matches()) {
          throw new MojoExecutionException(
              "Invalid syntax for alternative repository: \"" + altDeploymentRepo + "\". Use \"id::url\".");
        } else {
          String id = matcher.group(1).trim();
          String url = matcher.group(2).trim();

          repo = Repository.buildRemoteRepository(id, url, this.session,
              new PomPropertyResolver(this.project, this.settings, this.profiles, this.releaseArgs));
        }
      }
    }

    if (repo == null) {
      ArtifactRepository artifactRepository = this.project.getDistributionManagementArtifactRepository();
      if (artifactRepository != null) {
        // replace properties in remote repository URL
        artifactRepository.setUrl(new PomPropertyResolver(this.project, this.settings, this.profiles, this.releaseArgs)
            .expandPropertyReferences(artifactRepository.getUrl()));
      }
      repo = RepositoryUtils.toRepo(artifactRepository);
    }

    if (repo == null) {
      String msg = "Deployment failed: repository element was not specified in the POM inside"
          + " distributionManagement element or in -DaltDeploymentRepository=id::url parameter";

      throw new MojoExecutionException(msg);
    }

    return repo;
  }

}
