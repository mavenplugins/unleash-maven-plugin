package com.itemis.maven.plugins.unleash.scm.utils;

import java.io.File;
import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import com.google.common.base.Function;

/**
 * A function to convert a file's absolute path into the relative path starting from a reference file
 * (identical to former com.itemis.maven.plugins.unleash.util.FileToRelativePath within module unleash-mabven-plugin).
 *
 * @author <a href="mailto:stanley.hillner@itemis.de">Stanley Hillner</a>
 * @since 3.2.0
 */
public class FileToRelativePath implements Function<File, String> {
  private File workingDir;

  public FileToRelativePath(File workingDir) {
    this.workingDir = workingDir;
  }

  @Override
  public String apply(File f) {
    URI workingDirURI = toURIWithNormalizedDriveLetterOnWindows(this.workingDir);
    URI fileURI = toURIWithNormalizedDriveLetterOnWindows(f);
    return workingDirURI.relativize(fileURI).toString();
  }

  /**
   * Convert a File to a URI, normalizing the Windows drive letter to lowercase when running on Windows.
   *
   * @param file the file whose URI to produce; its drive letter will be lowercased on Windows if present
   * @return the URI for the file, with a lowercased drive letter on Windows when applicable
   */
  private URI toURIWithNormalizedDriveLetterOnWindows(File file) {
    if (SystemUtils.IS_OS_WINDOWS) {
      // On Windows OS deviations in character case of the drive letter may occur
      // => we have to normalize by lower casing the drive letter!
      // We use file.getAbsoluteFile() to comply with File.toURI inner coding.
      String absolutePath = file.getAbsoluteFile().getPath();
      int driveSeparatorIndex = absolutePath.indexOf(':');
      if (driveSeparatorIndex > 0) {
        absolutePath = absolutePath.substring(0, driveSeparatorIndex).toLowerCase()
            + absolutePath.substring(driveSeparatorIndex);
        return new File(absolutePath).toURI();
      }
    }
    return file.toURI();
  }

  /**
   * Determines whether the given file is the same as or a descendant of the working directory.
   *
   * @param f the file to test
   * @return {@code true} if the file is the same as or located inside the working directory, {@code false} otherwise
   */
  public boolean isParentOfOrSame(File f) {
    return !StringUtils.startsWith(apply(f), "file:");
  }

}
