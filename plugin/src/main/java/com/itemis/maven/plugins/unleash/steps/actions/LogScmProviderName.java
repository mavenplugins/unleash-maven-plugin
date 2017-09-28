package com.itemis.maven.plugins.unleash.steps.actions;

import javax.inject.Inject;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.google.common.base.Optional;
import com.itemis.maven.plugins.cdi.CDIMojoProcessingStep;
import com.itemis.maven.plugins.cdi.ExecutionContext;
import com.itemis.maven.plugins.cdi.annotations.ProcessingStep;
import com.itemis.maven.plugins.cdi.logging.Logger;
import com.itemis.maven.plugins.unleash.util.scm.MavenScmUtil;

/**
 * A Mojo that just logs SCM tagName and prefix message passed to and resolved by the Mojo.<br>
 *
 * @author mhoffrog
 * @since 2.10.1
 */
@ProcessingStep(id = "logScmProviderName", description = "Log SCM provider name for unleash plugin.", requiresOnline = false)
public class LogScmProviderName implements CDIMojoProcessingStep {
  @Inject
  private Logger log;

  // @Inject
  // private ScmProviderRegistry scmProviderRegistry;

  // @Inject
  // private ReleaseMetadata metadata;

  @Inject
  private MavenProject project;

  // @Inject
  // private PluginParameterExpressionEvaluator expressionEvaluator;

  // @Inject
  // @Named("scmMessagePrefix")
  // private String scmMessagePrefix;

  @Override
  public void execute(ExecutionContext context) throws MojoExecutionException, MojoFailureException {
    Optional<String> providerName = MavenScmUtil.calcProviderName(this.project);
    if (!providerName.isPresent()) {
      this.log.error(
          "Could not determine SCM provider name from your POM configuration! Please check the SCM section of your POM and provide connections in the correct format (see also: https://maven.apache.org/scm/scm-url-format.html).");
    } else {
      this.log.info("Resolved required SCM provider implementation to '" + providerName.get() + "'");
    }
  }
}
