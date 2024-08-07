package com.itemis.maven.plugins.unleash.steps.actions;

import com.itemis.maven.plugins.cdi.CDIMojoProcessingStep;
import com.itemis.maven.plugins.cdi.ExecutionContext;
import com.itemis.maven.plugins.cdi.annotations.ProcessingStep;
import com.itemis.maven.plugins.cdi.exception.EnforceRollbackWithoutErrorException;
import com.itemis.maven.plugins.cdi.logging.Logger;

import jakarta.inject.Inject;

/**
 * A {@link CDIMojoProcessingStep} that enforces abort of the Workflow with rolling back of previous actions but with
 * Maven success.<br>
 *
 * @author mhoffrog
 * @since 2.12.0
 */
@ProcessingStep(id = ExitWithRollbackNoError.ACTION_ID, description = "Enforce a rollback and end workflow execution with Maven success.", requiresOnline = false)
public class ExitWithRollbackNoError implements CDIMojoProcessingStep {

  public static final String ACTION_ID = "exitWithRollbackNoError";

  @Inject
  private Logger log;

  @Override
  public void execute(ExecutionContext context) throws EnforceRollbackWithoutErrorException {
    final String message = ACTION_ID + ": Enforce the normal end of the workflow with rollback of previous actions!";
    this.log.warn(message);
    throw new EnforceRollbackWithoutErrorException(message);
  }
}
