package com.bics.jira.mail.helper;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.jql.builder.JqlClauseBuilderFactory;
import com.atlassian.jira.jql.builder.JqlClauseBuilderFactoryImpl;
import com.atlassian.jira.jql.util.JqlDateSupportImpl;
import com.atlassian.jira.mock.MockApplicationProperties;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.mock.security.MockSimpleAuthenticationContext;
import com.atlassian.jira.plugins.mail.DryRunMessageHandlerExecutionMonitor;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.atlassian.jira.timezone.TimeZoneManagerImpl;
import com.atlassian.jira.user.MockUser;
import com.bics.jira.mail.mock.MockIssueManager;
import com.bics.jira.mail.mock.MockSearchService;
import com.bics.jira.mail.mock.MockUserPreferencesManager;
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
    private static final long RESOLVED_BEFORE = 30L * 24 * 60 * 60 * 1000;

    private final MockComponentWorker worker = new MockComponentWorker();
    private final MockSimpleAuthenticationContext jiraAuthenticationContext = new MockSimpleAuthenticationContext(new MockUser("test"));
    private final MockApplicationProperties applicationProperties = new MockApplicationProperties();
    private final MockIssueManager issueManager = new MockIssueManager();
    private final MockSearchService searchService = new MockSearchService();
    private final MessageHandlerErrorCollector monitor = new DryRunMessageHandlerExecutionMonitor();

    private final IssueLookupHelperImpl issueLookupHelper = new IssueLookupHelperImpl(jiraAuthenticationContext, applicationProperties, issueManager, searchService);
    private final MockProject project = new MockProject(1000L);
    private final MockIssue issue1 = new MockIssue(110L);
    private final MockIssue issue2 = new MockIssue(111L);

    @Before
    public void before() {
        worker.addMock(JqlClauseBuilderFactory.class, new JqlClauseBuilderFactoryImpl(new JqlDateSupportImpl(new TimeZoneManagerImpl(jiraAuthenticationContext, new MockUserPreferencesManager(), applicationProperties))));

        ComponentAccessor.initialiseWorker(worker);

        applicationProperties.setString(APKeys.JIRA_PROJECTKEY_PATTERN, "([A-Z][A-Z]+)");

        issue1.setSummary("aaa");
        issue2.setSummary("HHH problem advanced search specific filter");

        issueManager.set("TEST-110", issue1);
        issueManager.set("TEST-111", issue2);
        searchService.set(issue1);
        searchService.set(issue2);
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

    @Test
    public void testLookupBySubject_SimpleFound() {
        MutableIssue expectedResult = issue1;
        MutableIssue actualResult = issueLookupHelper.lookupBySubject(project, "aaa", RESOLVED_BEFORE, monitor);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testLookupBySubject_Complex() {
        MutableIssue expectedResult = issue2;
        MutableIssue actualResult = issueLookupHelper.lookupBySubject(project, "HHH problem advanced search specific filter", RESOLVED_BEFORE, monitor);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testLookupBySubject_NotFound() {
        MutableIssue actualResult = issueLookupHelper.lookupBySubject(project, "bbb", RESOLVED_BEFORE, monitor);

        assertNull(actualResult);
    }
}
