package com.itemis.maven.plugins.unleash.util;

/**
 * A strategy to be used to indicate the version part to be upgraded.
 *
 * @author <a href="mailto:stanley.hillner@itemis.de">Stanley Hillner</a>
 */
public enum VersionUpgradeStrategy {
  /**
   * The first part of the version (index 0).
   */
  MAJOR((short) 0),
  /**
   * The second part of the version (index 1).
   */
  MINOR((short) 1),
  /**
   * The third part of the version (index 2).
   */
  INCREMENTAL((short) 2),
  /**
   * The fourth part of the version (index 3).
   */
  BUILD((short) 3),
  /**
   * The fifth part of the version (index 4).
   */
  PART_5((short) 4),
  /**
   * The sixth part of the version (index 5).
   */
  PART_6((short) 5),
  /**
   * The seventh part of the version (index 6).
   */
  PART_7((short) 6),
  /**
   * The eighth part of the version (index 7).
   */
  PART_8((short) 7),
  /**
   * The lowest possible (rightmost) part of the version (index -1).
   */
  DEFAULT((short) -1);

  private short versionSegmentIndex;

  private VersionUpgradeStrategy(short index) {
    this.versionSegmentIndex = index;
  }

  /**
   * @return the index of the version part.
   */
  public short getVersionSegmentIndex() {
    return this.versionSegmentIndex;
  }
}
