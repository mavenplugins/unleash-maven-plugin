package com.itemis.maven.plugins.unleash.util;

import java.util.Iterator;
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

class Version {
  private static final char SEPARATOR_DASH = '-';
  private static final char SEPARATOR_DOT = '.';

  private List<String> segments;
  private List<Character> separators;

  private Version() {
    this.segments = Lists.newArrayList();
    this.separators = Lists.newArrayList();
  }

  protected static Version parse(String versionString) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(versionString), "Please provide a version String!");

    Version version = new Version();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < versionString.length(); i++) {
      char c = versionString.charAt(i);
      if (SEPARATOR_DASH == c || SEPARATOR_DOT == c) {
        version.segments.add(sb.toString());
        version.separators.add(c);
        sb.setLength(0);
      } else {
        sb.append(c);
      }
    }
    version.segments.add(sb.toString());

    return version;
  }

  public void increase(VersionUpgradeStrategy strategy) {
    if (strategy == VersionUpgradeStrategy.DEFAULT) {
      increaseLowestPossibleSegment();
    } else {
      increaseSpecificSegment(strategy.getVersionSegmentIndex());
    }
  }

  private void increaseLowestPossibleSegment() {
    for (int i = this.segments.size() - 1; i >= 0; i--) {
      String segment = this.segments.get(i);
      String increasedSegment = increaseSegment(segment);
      if (Objects.equal(segment, increasedSegment)) {
        continue;
      } else {
        this.segments.set(i, increasedSegment);
        break;
      }
    }
  }

  private void increaseSpecificSegment(short index) {
    for (short increasableIndex = index; increasableIndex < this.segments.size()
        && increasableIndex >= 0; increasableIndex++) {
      String segment = this.segments.get(increasableIndex);
      String increasedSegment = increaseSegment(segment);
      if (!Objects.equal(segment, increasedSegment)) {
        this.segments.set(increasableIndex, increasedSegment);
        return;
      }
    }
    // No increasable part found yet
    increaseLowestPossibleSegment();
  }

  private String increaseSegment(String segment) {
    StringBuilder sb = new StringBuilder(segment);
    int start = -1;
    int end = -1;
    for (int i = sb.length() - 1; i >= 0; i--) {
      try {
        Integer.parseInt(sb.substring(i, i + 1));
        if (end == -1) {
          end = i;
        }
      } catch (NumberFormatException e) {
        if (end > 0 && start == -1) {
          start = i + 1;
          break;
        }
      }
    }

    if (end >= 0) {
      if (start == -1) {
        start = 0;
      }
      int lengthForLeadingZeroes = 0;
      String sNumber = sb.substring(start, end + 1);
      if (sNumber.startsWith("0")) {
        lengthForLeadingZeroes = sNumber.length();
      }
      int increasedNumber = Integer.parseInt(sNumber) + 1;
      sNumber = String.format(lengthForLeadingZeroes > 0 ? "%0" + lengthForLeadingZeroes + "d" : "%d", increasedNumber);
      sb.replace(start, end + 1, sNumber);
    }

    return sb.toString();
  }

  @Override
  public String toString() {
    if (this.segments.isEmpty()) {
      return "";
    }

    Iterator<String> segmentsIterator = this.segments.iterator();
    StringBuilder sb = new StringBuilder(segmentsIterator.next());
    for (Character separator : this.separators) {
      sb.append(separator);
      sb.append(segmentsIterator.next());
    }

    return sb.toString();
  }

  public static void main(String[] args) {
    Version v = Version.parse("1.0.7-12-SNAPSHOT");
    v.increase(VersionUpgradeStrategy.DEFAULT);
    System.out.println(v);
  }
}
