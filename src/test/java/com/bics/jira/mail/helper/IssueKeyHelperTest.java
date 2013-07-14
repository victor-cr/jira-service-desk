package com.bics.jira.mail.helper;

import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.mock.MockApplicationProperties;
import com.atlassian.jira.mock.issue.MockIssue;
import com.bics.jira.mail.IssueKeyHelper;
import com.bics.jira.mail.mock.MockIssueManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 14/07/13 19:34
 */
public class IssueKeyHelperTest extends Assert {
    private MockApplicationProperties applicationProperties = new MockApplicationProperties();
    private MockIssueManager issueManager = new MockIssueManager();

    private IssueKeyHelper issueKeyHelper = new IssueKeyHelperImpl(applicationProperties, issueManager);

    @Before
    public void before() {
        applicationProperties.setString(APKeys.JIRA_PROJECTKEY_PATTERN, "([A-Z][A-Z]+)");

        issueManager.set("TEST-110", new MockIssue(110));
        issueManager.set("TEST-111", new MockIssue(111));
    }

    @Test
    public void testFindIssueKey_SingleFirst() {
        Collection<String> expectedResult = Collections.singletonList("TEST-110");
        Collection<String> actualResult = issueKeyHelper.findIssueKeys("TEST-110 Subject JS-21 JAM-1323");

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testFindIssueKey_SingleFirstCase() {
        Collection<String> expectedResult = Collections.singletonList("TEST-110");
        Collection<String> actualResult = issueKeyHelper.findIssueKeys("TEST-110 Subject TeST-111 JS-21 JAM-1323");

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testFindIssueKey_SingleFirstWithJunk() {
        Collection<String> expectedResult = Collections.singletonList("TEST-110");
        Collection<String> actualResult = issueKeyHelper.findIssueKeys("eTEST-111 TEST-111Subject TEST-110 JS-21 JAM-1323");

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testFindIssueKey_SingleMiddle() {
        Collection<String> expectedResult = Collections.singletonList("TEST-110");
        Collection<String> actualResult = issueKeyHelper.findIssueKeys("ssd fsdf TST-867 sd fTEST-111 TEST-110 JS-21 JAM-1323");

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testFindIssueKey_SingleMiddleCase() {
        Collection<String> expectedResult = Collections.singletonList("TEST-110");
        Collection<String> actualResult = issueKeyHelper.findIssueKeys("sdlfkjsdf TST-10734 TEST-110 Subject TeST-111 JS-21 JAM-1323");

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testFindIssueKey_SingleMiddleWithJunk() {
        Collection<String> expectedResult = Collections.singletonList("TEST-110");
        Collection<String> actualResult = issueKeyHelper.findIssueKeys("ssd fsdf TST-867 sd fTEST-111 TEST-110 TEST-111JS-21 JAM-1323");

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testFindIssueKey_SingleLast() {
        Collection<String> expectedResult = Collections.singletonList("TEST-110");
        Collection<String> actualResult = issueKeyHelper.findIssueKeys("Subject JS-21 JAM-1323 TEST-110");

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testFindIssueKey_SingleLastCase() {
        Collection<String> expectedResult = Collections.singletonList("TEST-110");
        Collection<String> actualResult = issueKeyHelper.findIssueKeys("Subject TeST-111 JS-21 JAM-1323 TEST-110");

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testFindIssueKey_SingleLastWithJunk() {
        Collection<String> expectedResult = Collections.singletonList("TEST-110");
        Collection<String> actualResult = issueKeyHelper.findIssueKeys("eTEST-111 TEST-111Subject ,TEST-111_ JS-21 JAM-1323 TEST-110");

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testFindIssueKey_Multiple() {
        Collection<String> expectedResult = Arrays.asList("TEST-111", "TEST-110");
        Collection<String> actualResult = issueKeyHelper.findIssueKeys("eTEST-111 TEST-111 JS-21 JAM-1323 TEST-110");

        assertEquals(expectedResult, actualResult);
    }
}
