/**
 *
 */
package com.itemis.maven.plugins.unleash.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

/**
 * @author mhoffrog
 */
public class ScmMessagePrefixUtil {

  /**
   *
   */
  private ScmMessagePrefixUtil() {
    // Not supposed top be instantiated
  }

  /**
   * @param scmMessagePrefixConfigured
   * @return the un-escaped scmMessagePrefixConfigured
   */
  public static String unescapeScmMessagePrefix(final String scmMessagePrefixConfigured) {
    String scmMessagePrefix = StringEscapeUtils.UNESCAPE_JAVA.translate(scmMessagePrefixConfigured);
    if (!StringUtils.contains(scmMessagePrefixConfigured, "\\r\\n")) {
      /*
       * If there is no explicit \r\n in the config string, then we normalize \n to {@link System#lineSeparator()} for
       * backward compatibility!
       */
      scmMessagePrefix = StringUtils.replace(scmMessagePrefix, "\r\n", "\n");
      scmMessagePrefix = StringUtils.replace(scmMessagePrefix, "\n", System.lineSeparator());
    }
    return scmMessagePrefix;
  }

}
