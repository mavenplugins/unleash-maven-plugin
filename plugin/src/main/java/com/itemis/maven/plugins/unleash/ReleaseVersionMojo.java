package com.itemis.maven.plugins.unleash;

import org.apache.maven.plugins.annotations.Mojo;

import com.itemis.maven.plugins.unleash.util.MavenVersionUtil;

/**
 * Print the release version calculated for this project by {@link MavenVersionUtil#calculateReleaseVersion(String)}.
 *
 * @author <a href="mailto:mhoffrogge@gmail.com">Markus Hoffrogge</a>
 * @since 3.1.0
 */
@Mojo(name = "releaseVersion", //
    requiresProject = false // Do NOT recurse through each reactor module of a project!
)
public class ReleaseVersionMojo extends AbstractVersionMojo {

  @Override
  protected String calculateVersion(final String currentVersion) {
    return MavenVersionUtil.calculateReleaseVersion(currentVersion);
  }

}
