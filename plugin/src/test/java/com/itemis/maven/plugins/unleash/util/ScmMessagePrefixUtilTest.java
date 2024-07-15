package com.itemis.maven.plugins.unleash.util;

import org.junit.Assert;
import org.junit.Test;

public class ScmMessagePrefixUtilTest {

  @Test
  public void testUnescapeMessagePrefix() {
    Assert.assertEquals("JIRA-123" + System.lineSeparator() + System.lineSeparator(),
        ScmMessagePrefixUtil.unescapeScmMessagePrefix("JIRA-123\\n\\n"));
    Assert.assertEquals("JIRA-123\r\n\r\n", ScmMessagePrefixUtil.unescapeScmMessagePrefix("JIRA-123\\r\\n\\r\\n"));
    Assert.assertEquals("JIRA-123\tabc\r\n\n",
        ScmMessagePrefixUtil.unescapeScmMessagePrefix("JIRA-123\\tabc\\r\\n\\n"));
    Assert.assertEquals("JIRA-123\tabc" + System.lineSeparator() + System.lineSeparator(),
        ScmMessagePrefixUtil.unescapeScmMessagePrefix("JIRA-123\\tabc\\n\\n"));
    Assert.assertEquals("JIRA-123 :: abc ", ScmMessagePrefixUtil.unescapeScmMessagePrefix("JIRA-123 :: abc\\040"));
  }

}
