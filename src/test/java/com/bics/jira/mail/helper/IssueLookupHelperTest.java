package com.bics.jira.mail.helper;

import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.mock.MockApplicationProperties;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.mock.security.MockSimpleAuthenticationContext;
import com.atlassian.jira.plugins.mail.DryRunMessageHandlerExecutionMonitor;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.atlassian.jira.user.MockUser;
import com.bics.jira.mail.mock.MockIssueManager;
import com.bics.jira.mail.mock.MockSearchService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 14/07/13 19:34
 */
public class IssueLookupHelperTest extends Assert {
    private final MockSimpleAuthenticationContext jiraAuthenticationContext = new MockSimpleAuthenticationContext(new MockUser("test"));
    private final MockApplicationProperties applicationProperties = new MockApplicationProperties();
    private final MockIssueManager issueManager = new MockIssueManager();
    private final MockSearchService searchService = new MockSearchService();
    private final MessageHandlerErrorCollector monitor = new DryRunMessageHandlerExecutionMonitor();

    private final IssueLookupHelperImpl issueLookupHelper = new IssueLookupHelperImpl(jiraAuthenticationContext, applicationProperties, issueManager, searchService);
    private final MockIssue issue1 = new MockIssue(110);
    private final MockIssue issue2 = new MockIssue(111);

    @Before
    public void before() {
        applicationProperties.setString(APKeys.JIRA_PROJECTKEY_PATTERN, "([A-Z][A-Z]+)");

        issueManager.set("TEST-110", issue1);
        issueManager.set("TEST-111", issue2);
    }

    @Test
    public void testLookupByKey_SingleFirst() {
        MutableIssue expectedResult = issue1;
        MutableIssue actualResult = issueLookupHelper.lookupByKey("TEST-110 Subject JS-21 JAM-1323", 0, monitor);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testLookupByKey_SingleFirstCase() {
        MutableIssue expectedResult = issue1;
        MutableIssue actualResult = issueLookupHelper.lookupByKey("TEST-110 Subject TeST-111 JS-21 JAM-1323", 0, monitor);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testLookupByKey_SingleFirstWithJunk() {
        MutableIssue expectedResult = issue1;
        MutableIssue actualResult = issueLookupHelper.lookupByKey("eTEST-111 TEST-111Subject TEST-110 JS-21 JAM-1323", 0, monitor);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testLookupByKey_SingleMiddle() {
        MutableIssue expectedResult = issue1;
        MutableIssue actualResult = issueLookupHelper.lookupByKey("ssd fsdf TST-867 sd fTEST-111 TEST-110 JS-21 JAM-1323", 0, monitor);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testLookupByKey_SingleMiddleCase() {
        MutableIssue expectedResult = issue1;
        MutableIssue actualResult = issueLookupHelper.lookupByKey("sdlfkjsdf TST-10734 TEST-110 Subject TeST-111 JS-21 JAM-1323", 0, monitor);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testLookupByKey_SingleMiddleWithJunk() {
        MutableIssue expectedResult = issue1;
        MutableIssue actualResult = issueLookupHelper.lookupByKey("ssd fsdf TST-867 sd fTEST-111 TEST-110 TEST-111JS-21 JAM-1323", 0, monitor);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testLookupByKey_SingleLast() {
        MutableIssue expectedResult = issue1;
        MutableIssue actualResult = issueLookupHelper.lookupByKey("Subject JS-21 JAM-1323 TEST-110", 0, monitor);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testLookupByKey_SingleLastCase() {
        MutableIssue expectedResult = issue1;
        MutableIssue actualResult = issueLookupHelper.lookupByKey("Subject TeST-111 JS-21 JAM-1323 TEST-110", 0, monitor);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testLookupByKey_SingleLastWithJunk() {
        MutableIssue expectedResult = issue1;
        MutableIssue actualResult = issueLookupHelper.lookupByKey("eTEST-111 TEST-111Subject ,TEST-111_ JS-21 JAM-1323 TEST-110", 0, monitor);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testLookupByKey_Multiple() {
        MutableIssue expectedResult = issue2;
        MutableIssue actualResult = issueLookupHelper.lookupByKey("eTEST-111 TEST-111 JS-21 JAM-1323 TEST-110", 0, monitor);

        assertEquals(expectedResult, actualResult);
    }
}
