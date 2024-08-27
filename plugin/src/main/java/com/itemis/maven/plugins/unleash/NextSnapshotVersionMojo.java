package com.itemis.maven.plugins.unleash;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.itemis.maven.plugins.unleash.util.MavenVersionUtil;
import com.itemis.maven.plugins.unleash.util.VersionUpgradeStrategy;

/**
 * Print the release version calculated for this project by {@link MavenVersionUtil#calculateReleaseVersion(String)}.
 *
 * @author <a href="mailto:mhoffrogge@gmail.com">Markus Hoffrogge</a>
 * @since 3.1.0
 */
@Mojo(name = "nextSnapshotVersion", requiresProject = true)
public class NextSnapshotVersionMojo extends AbstractVersionMojo {

  /**
   * Optional parameter to use a specific {@link VersionUpgradeStrategy}.
   */
  @Parameter(defaultValue = "DEFAULT", property = "unleash.versionUpgradeStrategy", required = true)
  protected VersionUpgradeStrategy versionUpgradeStrategy;

  @Override
  protected String calculateVersion(final String currentVersion) {
    return MavenVersionUtil.calculateNextSnapshotVersion(currentVersion, this.versionUpgradeStrategy);
  }

}
