package com.itemis.maven.plugins.unleash.util.functions;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import com.itemis.maven.plugins.unleash.scm.utils.FileToRelativePath;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

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

  @DataProvider
  public static Object[][] windows_IsParentOf() {
    return new Object[][] { { "C:/abc/def", "C:/abc/def/ghi" }, { "C:/abc/def/ghi", "C:/abc/def/ghi" },
        { "c:/abc/def", "C:/abc/def/ghi" }, { "C:/abc/def", "c:\\abc\\def\\ghi" },
        { "C:\\abc\\def", "c:\\abc\\def\\ghi" } };
  };

  @DataProvider
  public static Object[][] unix_IsParentOf() {
    return new Object[][] { { "/c/abc/def", "/c/abc/def/ghi" }, { "/c/abc/def/ghi", "/c/abc/def/ghi" } };
  };

  @Test
  @UseDataProvider("windows_IsParentOf")
  public void testWindowsIsParentOf(String parentPath, String childPath) {
    if (!SystemUtils.IS_OS_WINDOWS) {
      return;
    }
    Assert.assertTrue(new FileToRelativePath(new File(parentPath)).isParentOfOrSame(new File(childPath)));
  }

  @Test
  @UseDataProvider("windows_IsParentOf")
  public void testWindowsIsNotParentOf(String parentPath, String childPath) {
    if (!SystemUtils.IS_OS_WINDOWS) {
      return;
    }
    File fileParentPath = new File(parentPath);
    File fileChildPath = new File(childPath);
    Assert.assertTrue(!new FileToRelativePath(fileChildPath).isParentOfOrSame(fileParentPath)
        || StringUtils.isEmpty(new FileToRelativePath(fileChildPath).apply(fileParentPath)));
  }

  @Test
  @UseDataProvider("unix_IsParentOf")
  public void testUnixIsParentOf(String parentPath, String childPath) {
    if (SystemUtils.IS_OS_WINDOWS) {
      return;
    }
    Assert.assertTrue(new FileToRelativePath(new File(parentPath)).isParentOfOrSame(new File(childPath)));
  }

  @Test
  @UseDataProvider("unix_IsParentOf")
  public void testUnixIsNotParentOf(String parentPath, String childPath) {
    if (SystemUtils.IS_OS_WINDOWS) {
      return;
    }
    File fileParentPath = new File(parentPath);
    File fileChildPath = new File(childPath);
    Assert.assertTrue(!new FileToRelativePath(fileChildPath).isParentOfOrSame(fileParentPath)
        || StringUtils.isEmpty(new FileToRelativePath(fileChildPath).apply(fileParentPath)));
  }
}
