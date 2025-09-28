package com.itemis.maven.plugins.unleash;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.PluginParameterExpressionEvaluator;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.impl.Deployer;
import org.eclipse.aether.impl.Installer;
import org.eclipse.aether.impl.RemoteRepositoryManager;
import org.eclipse.aether.repository.RemoteRepository;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.itemis.maven.aether.ArtifactCoordinates;
import com.itemis.maven.aether.ArtifactDeployer;
import com.itemis.maven.plugins.cdi.AbstractCDIMojo;
import com.itemis.maven.plugins.cdi.annotations.MojoInject;
import com.itemis.maven.plugins.cdi.annotations.MojoProduces;
import com.itemis.maven.plugins.unleash.scm.utils.FileToRelativePath;
import com.itemis.maven.plugins.unleash.steps.actions.ExitWithRollbackNoError;
import com.itemis.maven.plugins.unleash.util.PomPropertyResolver;
import com.itemis.maven.plugins.unleash.util.ReleaseUtil;
import com.itemis.maven.plugins.unleash.util.Repository;
import com.itemis.maven.plugins.unleash.util.ScmMessagePrefixUtil;
import com.itemis.maven.plugins.unleash.util.VersionUpgradeStrategy;

import jakarta.inject.Named;

public class AbstractUnleashMojo extends AbstractCDIMojo {
  public static final String PROPERTY_REPO_BASE = "multiDeploy.repo";

  @Component
  @MojoProduces
  private PlexusContainer plexus;

  @Component
  @MojoProduces
  private RepositorySystem repoSystem;

  @Component
  @MojoProduces
  private RemoteRepositoryManager remoteRepositoryManager;

  @Component
  @MojoProduces
  private Deployer deployer;

  @Component
  @MojoProduces
  private Installer installer;

  @Component
  @MojoProduces
  private Prompter prompter;

  @Parameter(property = "session", readonly = true)
  @MojoProduces
  private MavenSession session;

  @Parameter(property = "mojoExecution", readonly = true)
  @MojoProduces
  private MojoExecution mojoExecution;

  @Parameter(defaultValue = "${repositorySystemSession}", readonly = true, required = true)
  @MojoProduces
  private RepositorySystemSession repoSession;

  @Parameter(defaultValue = "${project.remotePluginRepositories}", readonly = true, required = true)
  @MojoProduces
  @Named("pluginRepositories")
  private List<RemoteRepository> remotePluginRepos;

  @Parameter(defaultValue = "${project.remoteProjectRepositories}", readonly = true, required = true)
  @MojoProduces
  @Named("projectRepositories")
  private List<RemoteRepository> remoteProjectRepos;

  @Parameter(defaultValue = "${localRepository}", readonly = true, required = true)
  @MojoProduces
  @Named("local")
  private ArtifactRepository LocalRepository;

  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  @MojoProduces
  private MavenProject project;

  @Parameter(defaultValue = "${reactorProjects}", readonly = true, required = true)
  @MojoProduces
  @Named("reactorProjects")
  private List<MavenProject> reactorProjects;

  @Parameter(defaultValue = "${settings}", readonly = true, required = true)
  @MojoProduces
  private Settings settings;

  ////////////////////////////// configuration parameters //////////////////////////////
  ////////////////////////////// required
  @Parameter(defaultValue = "true", property = "unleash.allowLocalReleaseArtifacts", required = true)
  @MojoProduces
  @Named("allowLocalReleaseArtifacts")
  private boolean allowLocalReleaseArtifacts;

  @Parameter(defaultValue = "false", property = "unleash.commitBeforeTagging", required = true)
  @MojoProduces
  @Named("commitBeforeTagging")
  private boolean commitBeforeTagging;

  @Parameter(defaultValue = "${maven.home}", property = "unleash.mavenHome", required = true)
  @MojoProduces
  @Named("maven.home")
  private String mavenHome;

  @Parameter(defaultValue = "@{project.version}", property = "unleash.tagNamePattern", required = true)
  @MojoProduces
  @Named("tagNamePattern")
  private String tagNamePattern;

  @Parameter(defaultValue = "true", property = "unleash.updateReactorDependencyVersion", required = true)
  @MojoProduces
  @Named("updateReactorDependencyVersion")
  private boolean updateReactorDependencyVersion;

  //////////////////////////// optional
  @Parameter(property = "unleash.developmentVersion", required = false)
  @MojoProduces
  @Named("developmentVersion")
  private String developmentVersion;

  @Parameter(defaultValue = "clean,verify", property = "unleash.goals", required = false)
  @MojoProduces
  @Named("releaseGoals")
  private List<String> goals;

  @Parameter(property = "unleash.profiles", required = false)
  @MojoProduces
  @Named("profiles")
  private List<String> profiles;

  @Parameter(defaultValue = "", property = "unleash.releaseArgs", required = false)
  private List<String> releaseArgs;

  @Parameter(property = "unleash.releaseVersion", required = false)
  @MojoProduces
  @Named("releaseVersion")
  private String releaseVersion;

  @Parameter(name = "scmMessagePrefix", defaultValue = "[unleash-maven-plugin]", property = "unleash.scmMessagePrefix", required = false)
  private String scmMessagePrefixConfigured;

  @MojoProduces
  @Named("scmPassword")
  @Parameter(property = "unleash.scmPassword", required = false)
  private String scmPassword;

  @MojoProduces
  @Named("scmUsername")
  @Parameter(property = "unleash.scmUsername", required = false)
  private String scmUsername;

  @MojoProduces
  @Named("scmSshPassphrase")
  @Parameter(property = "unleash.scmSshPassphrase", required = false)
  private String scmSshPassphrase;

  @MojoProduces
  @Named("scmPasswordEnvVar")
  @Parameter(property = "unleash.scmPasswordEnvVar", required = false)
  private String scmPasswordEnvVar;

  @MojoProduces
  @Named("scmUsernameEnvVar")
  @Parameter(property = "unleash.scmUsernameEnvVar", required = false)
  private String scmUsernameEnvVar;

  @MojoProduces
  @Named("scmSshPassphraseEnvVar")
  @Parameter(property = "unleash.scmSshPassphraseEnvVar", required = false)
  private String scmSshPassphraseEnvVar;

  @MojoProduces
  @Named("scmSshPrivateKeyEnvVar")
  @Parameter(property = "unleash.scmSshPrivateKeyEnvVar", required = false)
  private String scmSshPrivateKeyEnvVar;

  @Parameter(property = "unleash.releaseEnvironment", required = false)
  private String releaseEnvironmentVariables;

  @MojoProduces
  @Parameter(property = "unleash.versionUpgradeStrategy", required = true, defaultValue = "DEFAULT")
  private VersionUpgradeStrategy versionUpgradeStrategy;

  @MojoProduces
  @Named("preserveFixedModuleVersions")
  @Parameter(property = "unleash.preserveFixedModuleVersions", required = false, defaultValue = "false")
  private boolean preserveFixedModuleVersions;

  @Parameter
  private Set<Repository> additionalDeploymentRepositories;

  /**
   * If set true, then do not perform actual deployment rather than warn level logging each artifact that would have
   * been deployed as well as the target repo url to deploy to.
   *
   * This is intended to be used for e2e testing purpose.
   *
   * Note: In combination with {@link ExitWithRollbackNoError} it allows e2e testing of workflows (except the
   * actual upload to remote repositories).
   *
   * @see ArtifactDeployer#deployArtifacts(java.util.Collection)
   *
   * @since 3.3.0
   */
  @Parameter(defaultValue = "false", property = "unleash.isDeployDryRun", required = true)
  @MojoProduces
  @Named("isDeployDryRun")
  private boolean isDeployDryRun;

  /**
   * Specifies an alternative repository to which the project artifacts should be deployed (other than those specified
   * in &lt;distributionManagement&gt;). <br/>
   * Format: <code>id::url</code>
   * <dl>
   * <dt>id</dt>
   * <dd>The id can be used to pick up the correct credentials from the settings.xml</dd>
   * <dt>url</dt>
   * <dd>The location of the repository</dd>
   * </dl>
   * <b>Note:</b> In version 2.x, the format was <code>id::<i>layout</i>::url</code> where <code><i>layout</i></code>
   * could be <code>default</code> (ie. Maven 2) or <code>legacy</code> (ie. Maven 1), but since 3.0.0 the layout part
   * has been removed because Maven 3 only supports Maven 2 repository layout.
   *
   * @since 3.3.0
   */
  @MojoProduces
  @Named("altDeploymentRepository")
  @Parameter(property = "altDeploymentRepository", required = false)
  private String altDeploymentRepository;

  /**
   * The alternative repository to use when property <code>altReleaseDeploymentRepository</code> is not blank.
   *
   * <b>Note:</b> In version 2.x, the format was <code>id::<i>layout</i>::url</code> where <code><i>layout</i></code>
   * could be <code>default</code> (ie. Maven 2) or <code>legacy</code> (ie. Maven 1), but since 3.0.0 the layout part
   * has been removed because Maven 3 only supports Maven 2 repository layout.
   *
   * @since 3.3.0
   * @see AbstractUnleashMojo#altDeploymentRepository
   */
  @MojoProduces
  @Named("altReleaseDeploymentRepository")
  @Parameter(property = "altReleaseDeploymentRepository", required = false)
  private String altReleaseDeploymentRepository;

  /**
   * @since 3.3.0
   */
  @MojoProduces
  @Parameter(property = "unleash.artifactSpyPluginGroupId", required = true, defaultValue = "io.github.mavenplugins")
  private String artifactSpyPluginGroupId;

  /**
   * @since 3.3.0
   */
  @MojoProduces
  @Parameter(property = "unleash.artifactSpyPluginArtifactId", required = true, defaultValue = "artifact-spy-plugin")
  private String artifactSpyPluginArtifactId;

  /**
   * @since 3.3.0
   */
  @MojoProduces
  @Parameter(property = "unleash.artifactSpyPluginVersion", required = true, defaultValue = "1.1.0")
  private String artifactSpyPluginVersion;

  @MojoProduces
  @Named("artifactSpyPlugin")
  private ArtifactCoordinates getArtifactSpyPluginCoordinates() {
    return new ArtifactCoordinates(this.artifactSpyPluginGroupId, this.artifactSpyPluginArtifactId,
        this.artifactSpyPluginVersion, "maven-plugin");
  }

  @MojoProduces
  private PluginParameterExpressionEvaluator getExpressionEvaluator() {
    return new PluginParameterExpressionEvaluator(this.session, this.mojoExecution);
  }

  @MojoProduces
  private PluginDescriptor getPluginDescriptor() {
    return (PluginDescriptor) getPluginContext().get("pluginDescriptor");
  }

  /**
   * Setter called by injector for property 'scmMessagePrefix'.
   *
   * @param scmMessagePrefixConfigured
   */
  public void setScmMessagePrefix(String scmMessagePrefixConfigured) {
    this.scmMessagePrefixConfigured = scmMessagePrefixConfigured;
  }

  @MojoProduces
  @Named("scmMessagePrefix")
  private String getScmMessagePrefix() {
    // @shaertel: expand newlines and other control characters in message prefix
    String scmMessagePrefix = ScmMessagePrefixUtil.unescapeScmMessagePrefix(this.scmMessagePrefixConfigured);
    // @shaertel: accept any white space char as separator
    if (!scmMessagePrefix.matches(".*\\s+$")) {
      scmMessagePrefix = scmMessagePrefix + " ";
    }
    // @mhoffrog: return with probable pom property values replaced
    return ReleaseUtil.getScmPatternResolved(Strings.nullToEmpty(scmMessagePrefix), this.project,
        getExpressionEvaluator());
  }

  @MojoProduces
  @Named("unleashOutputFolder")
  private File getUnleashOutputFolder() {
    File folder = new File(this.project.getBuild().getDirectory(), "unleash");
    folder.mkdirs();
    return folder;
  }

  @MojoProduces
  @Named("releaseArgs")
  @MojoInject
  private Properties getReleaseArgs() {
    Properties args = new Properties();
    Splitter splitter = Splitter.on('=');
    if (this.releaseArgs != null) {
      for (String arg : this.releaseArgs) {
        List<String> split = splitter.splitToList(arg);
        if (split.size() == 2) {
          args.put(split.get(0), split.get(1));
        } else {
          args.put(split.get(0), "true");
          getLog().info("Detected release argument without an explicit value. Assuming '" + split.get(0)
              + "' to be a boolean property and setting it to true.");
        }
      }
    }

    // Add default property indicating that the unleash plugin is triggering the build
    args.put("isUnleashBuild", "true");
    return args;
  }

  @MojoProduces
  @Named("releaseEnvVariables")
  private Map<String, String> getReleaseEnvironmentVariables() {
    Map<String, String> env = Maps.newHashMap();
    if (!Strings.isNullOrEmpty(this.releaseEnvironmentVariables)) {
      Iterable<String> split = Splitter.on(',').split(this.releaseEnvironmentVariables);
      for (String token : split) {
        String date = Strings.emptyToNull(token.trim());
        if (date != null) {
          List<String> dataSplit = Splitter.on("=>").splitToList(date);
          String key = dataSplit.get(0);
          String value = dataSplit.get(1);
          env.put(key, value);
        }
      }
    }
    return env;
  }

  /**
   * Collects additional deployment repositories configured for the build and returns them as RemoteRepository instances.
   *
   * The method includes repositories declared via the `additionalDeploymentRepositories` plugin configuration and
   * repositories parsed from system properties whose keys start with "multiDeploy.repo".
   *
   * @return a set of RemoteRepository objects representing the additional deployment targets
   */
  @MojoProduces
  @Named("additionalDeployemntRepositories")
  private Set<RemoteRepository> getAdditionalDeploymentRepositories() {
    Set<Repository> repos = new HashSet<>();
    if (this.additionalDeploymentRepositories != null) {
      repos.addAll(this.additionalDeploymentRepositories);
    }

    System.getProperties().forEach((key, value) -> {
      if (key.toString().startsWith(PROPERTY_REPO_BASE)) {
        Repository.parseFromProperty(value.toString()).ifPresent(repo -> repos.add(repo));
      }
    });

    return repos.stream().map(repo -> {
      return repo.buildRemoteRepository(this.session,
          new PomPropertyResolver(this.project, this.settings, this.profiles, getReleaseArgs()));
    }).collect(Collectors.toSet());
  }

  private File allReactorsBasedir;

  /**
   * Determine and return the lowest common base directory that contains all reactor projects, validating
   * that each reactor corresponds to a unique relative path under that base.
   *
   * This method computes the common ancestor directory for the current project and all reactor projects,
   * caches the result, and ensures there is at most one POM per relative path under the computed base.
   *
   * @return the common base directory that contains all reactor projects
   * @throws MojoFailureException if no common ancestor exists or if multiple reactor POMs are found in the same relative directory
   */
  @MojoProduces
  @Named("allReactorsBasedir")
  private File getAllReactorsBasedir() throws MojoFailureException {
    if (this.allReactorsBasedir != null) {
      return this.allReactorsBasedir;
    }
    // Derive the lowest common ancestor of all reactor projects
    File commonBase = this.project.getBasedir();
    List<File> listOfAllReactorsBasedir = Lists.newArrayList();
    for (MavenProject reactorProject : this.reactorProjects) {
      File currentCommonBase = commonBase;
      File reactorBasedir = reactorProject.getBasedir();
      listOfAllReactorsBasedir.add(reactorBasedir);
      // Walk up from the initial base until it contains the reactor
      while (commonBase != null && !new FileToRelativePath(commonBase).isParentOfOrSame(reactorBasedir)) {
        commonBase = commonBase.getParentFile();
      }
      if (commonBase == null) {
        getLog().error("Invalid project structure: Reactor project " + reactorProject.getName()
            + " does not share a common ancestor directory with " + currentCommonBase.getAbsolutePath());
        throw new MojoFailureException("Invalid project structure - see previous error log for details.");
      }
    }
    this.allReactorsBasedir = commonBase;
    getLog().info("All projects basedir is: " + this.allReactorsBasedir.getAbsolutePath());
    // Now validate that there is a single POM per relative path
    List<String> listOfAllRelativPaths = Lists.newArrayList();
    for (File projectBasedir : listOfAllReactorsBasedir) {
      String relativePath = new FileToRelativePath(this.allReactorsBasedir).apply(projectBasedir);
      if (!listOfAllRelativPaths.contains(relativePath)) {
        listOfAllRelativPaths.add(relativePath);
        continue;
      }
      getLog().error("Invalid project structure: Multiple project/reactor POMs found in directory "
          + projectBasedir.getAbsolutePath());
      throw new MojoFailureException("Invalid project structure - see previous error log for details.");
    }
    return this.allReactorsBasedir;
  }

}
