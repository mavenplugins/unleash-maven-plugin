package com.itemis.maven.plugins.unleash.util;

import java.io.File;

/**
 * A function to convert a file's absolute path into the relative path starting from a reference file.
 *
 * @author <a href="mailto:stanley.hillner@itemis.de">Stanley Hillner</a>
 * @since 1.0.0
 * @deprecated Use {@link com.itemis.maven.plugins.unleash.scm.utils.FileToRelativePath} instead.
 */
@Deprecated
public class FileToRelativePath extends com.itemis.maven.plugins.unleash.scm.utils.FileToRelativePath {

  public FileToRelativePath(File workingDir) {
    super(workingDir);
  }
}
