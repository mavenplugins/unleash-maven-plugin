package com.itemis.maven.plugins.unleash.util;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.aether.repository.RemoteRepository;

import com.google.common.base.Splitter;

public class Repository {
  @Parameter(required = true)
  private String id;
  @Parameter(required = true)
  private String url;

  // only for local usage during property parsing
  private boolean releasesEnabled = true;

  public String getId() {
    return this.id;
  }

  public String getUrl() {
    return this.url;
  }

  public static Optional<Repository> parseFromProperty(String value) {
    Repository repo = new Repository();

    Splitter.on(',').split(value).forEach(s -> {
      int i = s.indexOf('=');
      if (i > 0 && i < s.length() - 1) {
        String k = s.substring(0, i).trim();
        String v = s.substring(i + 1, s.length()).trim();
        switch (k) {
          case "id":
            repo.id = v;
            break;
          case "url":
            repo.url = v;
            break;
          case "releases":
            repo.releasesEnabled = Boolean.parseBoolean(v);
            break;
        }
      }
    });

    if (repo.releasesEnabled && StringUtils.isNotBlank(repo.id) && StringUtils.isNoneBlank(repo.url)) {
      return Optional.of(repo);
    }
    return Optional.empty();
  }

  /**
   * @see #buildRemoteRepository(String, String, MavenSession, PomPropertyResolver)
   *
   * @param session          the Maven session for repo authentication and deploy proxy lookup
   * @param propertyResolver the {@link PomPropertyResolver} to replace property variables in <code>repo.getUrl()</code>
   * @return the {@link RemoteRepository} built
   */
  public RemoteRepository buildRemoteRepository(final MavenSession session,
      final PomPropertyResolver propertyResolver) {
    return buildRemoteRepository(getId(), getUrl(), session, propertyResolver);
  }

  /**
   * Build a {@link RemoteRepository} for a <code>repositoryId</code> and a <code>repositoryUrl</code>.
   *
   * Taken from original maven-deploy-plugin
   * <code>AbstractDeployMojo.getRemoteRepository(final String repositoryId, final String url)</code>.
   * Plus keeping this plugin's feature regarding property replacement in remote repository URL.
   *
   * @see <a href=
   *      "https://github.com/apache/maven-deploy-plugin/blob/maven-deploy-plugin-3.1.4/src/main/java/org/apache/maven/plugins/deploy/AbstractDeployMojo.java">maven-deploy-plugin:
   *      AbstractDeployMojo.java</a>
   * @see <a href="https://github.com/shillner/unleash-maven-plugin/pull/101">unleash-maven-plugin #101</a>
   *
   * @param repositoryId     the repository id
   * @param repositoryUrl    the repository url
   * @param session          the Maven session for repo authentication and deploy proxy lookup
   * @param propertyResolver the {@link PomPropertyResolver} to replace property variables in <code>repositoryUrl</code>
   * @return the {@link RemoteRepository} built
   */
  public static RemoteRepository buildRemoteRepository(final String repositoryId, final String repositoryUrl,
      final MavenSession session, final PomPropertyResolver propertyResolver) {
    // replace properties in remote repository URL and getting the remote repo
    String resolvedUrl = propertyResolver.expandPropertyReferences(repositoryUrl);
    // TODO: RepositorySystem#newDeploymentRepository does this very same thing!
    RemoteRepository result = new RemoteRepository.Builder(repositoryId, "default", resolvedUrl).build();

    if (result.getAuthentication() == null || result.getProxy() == null) {
      RemoteRepository.Builder builder = new RemoteRepository.Builder(result);

      if (result.getAuthentication() == null) {
        builder.setAuthentication(session.getRepositorySession().getAuthenticationSelector().getAuthentication(result));
      }

      if (result.getProxy() == null) {
        builder.setProxy(session.getRepositorySession().getProxySelector().getProxy(result));
      }

      result = builder.build();
    }

    return result;
  }

}
