package com.itemis.maven.plugins.unleash.util.functions;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import com.google.common.base.Function;

/**
 * A function to convert a file's absolute path into the relative path starting from a reference file.
 *
 * @author <a href="mailto:stanley.hillner@itemis.de">Stanley Hillner</a>
 * @since 1.0.0
 */
public class FileToRelativePath implements Function<File, String> {
  private File workingDir;

  public FileToRelativePath(File workingDir) {
    this.workingDir = workingDir;
  }

  @Override
  public String apply(File f) {
    URI workingDirURI = this.workingDir.toURI();
    URI fileURI = f.toURI();
    if (SystemUtils.IS_OS_WINDOWS) {
      // On Windows OS deviations in character case of the drive letter may occur
      // => we have to normalize by lower casing the URI!
      String lcURIString = StringUtils.EMPTY;
      try {
        lcURIString = workingDirURI.toString().toLowerCase();
        workingDirURI = new URI(lcURIString);
        lcURIString = fileURI.toString().toLowerCase();
        fileURI = new URI(lcURIString);
      } catch (URISyntaxException e) {
        throw new RuntimeException("Failed to create URI for path: " + lcURIString, e);
      }
    }

    return workingDirURI.relativize(fileURI).toString();
  }
}
