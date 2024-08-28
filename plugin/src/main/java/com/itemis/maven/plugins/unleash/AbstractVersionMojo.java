package com.itemis.maven.plugins.unleash;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.WriterFactory;

/**
 * Base class with some Version Mojo functionalities.
 *
 * @author <a href="mailto:mhoffrogge@gmail.com">Markus Hoffrogge</a>
 * @since 3.1.0
 */
public abstract class AbstractVersionMojo extends AbstractMojo {
  /**
   * Current Maven project.
   */
  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  protected MavenProject project;

  /**
   * List of Maven reactor projects
   */
  @Parameter(defaultValue = "${reactorProjects}", readonly = true, required = true)
  private List<MavenProject> reactorProjects;

  /**
   * The current build session instance. This is used for
   * plugin manager API calls.
   */
  @Parameter(defaultValue = "${session}", readonly = true, required = true)
  protected MavenSession session;

  /**
   * Optional parameter to write the output of this version in a given file, instead of writing to the console.
   * <br>
   * <b>Note</b>: Could be a relative path.
   */
  @Parameter(property = "output")
  protected File output;

  /**
   * This parameter gives the option to output information in cases where the output has been suppressed by using
   * <code>-q</code> (quiet option) in Maven. This is useful if you like to use
   * <code>unleash:releaseVersion</code> or <code>unleash:nextSnapshotVersion</code> in a script call (for example in
   * bash) like this:
   *
   * <pre>
   * RESULT=$(mvn -q unleash:nextSnapshotVersion -DforceStdout)
   * echo $RESULT
   * </pre>
   *
   * This will only printout the result which has been calculated to <code>stdout</code>.
   */
  @Parameter(property = "forceStdout", defaultValue = "false")
  private boolean forceStdout;

  /**
   * Allow recursive reactor output. If set false, this goal will output the version of the root module only. If set
   * true, the goal will output the version of any reactor module of the project.
   */
  @Parameter(property = "allowRecursiveReactorOutput", defaultValue = "false")
  private boolean allowRecursiveReactorOutput;

  /**
   * Utility method to write a content in a given file.
   *
   * @param output  is the wanted output file.
   * @param content contains the content to be written to the file.
   * @throws IOException if any
   * @see #writeFile(File, String)
   */
  protected static void writeFile(File output, StringBuilder content) throws IOException {
    writeFile(output, content.toString());
  }

  /**
   * Utility method to write a content in a given file.
   *
   * @param output  is the wanted output file.
   * @param content contains the content to be written to the file.
   * @throws IOException if any
   */
  protected static void writeFile(File output, String content) throws IOException {
    if (output == null) {
      return;
    }

    output.getParentFile().mkdirs();
    try (Writer out = WriterFactory.newPlatformWriter(output)) {
      out.write(content);
    }
  }

  /**
   * @param result the result of the version calculated
   * @throws MojoExecutionException if any
   * @throws MojoFailureException   if any reflection exceptions occur or missing components.
   */
  protected void handleResultOutput(String result, int projectIndex)
      throws MojoExecutionException, MojoFailureException {
    if (this.output != null) {
      StringBuilder sb = new StringBuilder();
      if (projectIndex > 0) {
        sb.append(SystemUtils.LINE_SEPARATOR);
      }
      sb.append(result);
      try {
        writeFile(this.output, sb);
      } catch (IOException e) {
        throw new MojoExecutionException("Cannot write result to output: " + this.output, e);
      }
      getLog().info("Result written to: " + this.output);
    } else {
      if (getLog().isInfoEnabled()) {
        getLog().info(SystemUtils.LINE_SEPARATOR + result);
      } else {
        if (this.forceStdout) {
          if (projectIndex > 0) {
            System.out.print(SystemUtils.LINE_SEPARATOR);
          }
          System.out.print(result);
          System.out.flush();
        }
      }
    }
  }

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    final int projectIndex = this.reactorProjects.indexOf(this.project);
    if (projectIndex == 0 || this.allowRecursiveReactorOutput) {
      final String result = calculateVersion(this.project.getVersion());
      handleResultOutput(result, projectIndex);
    }
  }

  /**
   * Calculate the version according to the implementing Mojo use case.
   *
   * @param currentVersion this projects version
   * @return the version calculated
   */
  protected abstract String calculateVersion(String currentVersion);

}