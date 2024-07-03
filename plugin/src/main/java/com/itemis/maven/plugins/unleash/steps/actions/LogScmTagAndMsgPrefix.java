package com.itemis.maven.plugins.unleash.steps.actions;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.itemis.maven.plugins.cdi.CDIMojoProcessingStep;
import com.itemis.maven.plugins.cdi.ExecutionContext;
import com.itemis.maven.plugins.cdi.annotations.ProcessingStep;
import com.itemis.maven.plugins.cdi.logging.Logger;
import com.itemis.maven.plugins.unleash.ReleaseMetadata;

import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * A Mojo that just logs SCM tagName and prefix message passed to and resolved by the Mojo.<br>
 *
 * @author mhoffrog
 * @since 2.10.1
 */
@ProcessingStep(id = "logScmTagAndMsgPrefix", description = "Log SCM tagName and message prefix for unleash plugin.", requiresOnline = false)
public class LogScmTagAndMsgPrefix implements CDIMojoProcessingStep {
  @Inject
  private Logger log;

  // @Inject
  // private ScmProviderRegistry scmProviderRegistry;

  @Inject
  private ReleaseMetadata metadata;

  // @Inject
  // private MavenProject project;
  //
  // @Inject
  // private PluginParameterExpressionEvaluator expressionEvaluator;

  @Inject
  @Named("scmMessagePrefix")
  private String scmMessagePrefix;

  @Override
  public void execute(ExecutionContext context) throws MojoExecutionException, MojoFailureException {
    this.log.info("Resolved SCM tagname: " + this.metadata.getScmTagName());
    this.log.info("Resolved SCM message prefix: " + this.scmMessagePrefix);
  }
}
