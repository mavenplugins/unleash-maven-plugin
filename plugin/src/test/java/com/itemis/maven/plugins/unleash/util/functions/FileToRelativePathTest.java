package com.itemis.maven.plugins.unleash.util.functions;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import com.itemis.maven.plugins.unleash.scm.utils.FileToRelativePath;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;

@RunWith(DataProviderRunner.class)
public class FileToRelativePathTest {
  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  public static final String WINDOWS_BASE_PATH_UPPER_DRIVE = "C:\\Temp";

  public static final String WINDOWS_BASE_PATH_LOWER_DRIVE = "c:\\Temp";

  @Test
  @DataProvider({ "test", "Test", "1/2/3/xyz.txt" })
  public void testApply(String path) throws IOException {
    File base = this.tempFolder.newFolder();
    Assert.assertEquals(path, new FileToRelativePath(base).apply(new File(base, path)));
  }

  @Test
  @DataProvider({ "test", "Test", "1/2/3/xyz.txt" })
  public void testWindowsApply(String path) throws IOException {
    if (SystemUtils.IS_OS_WINDOWS) {
      File base = new File(WINDOWS_BASE_PATH_UPPER_DRIVE);
      Assert.assertEquals(path, new FileToRelativePath(base).apply(new File(WINDOWS_BASE_PATH_LOWER_DRIVE, path)));
    }
  }

}
