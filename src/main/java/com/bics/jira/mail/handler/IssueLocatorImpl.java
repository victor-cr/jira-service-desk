package com.bics.jira.mail.handler;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;
import com.bics.jira.mail.IssueLocator;
import com.bics.jira.mail.model.HandlerModel;
import com.bics.jira.mail.model.MessageAdapter;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.List;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 04.02.13 21:38
 */
public class IssueLocatorImpl implements IssueLocator {
    private static final Logger LOG = Logger.getLogger(IssueLocatorImpl.class);
    private static final long RESOLUTION_DELTA = 1000L * 60 * 60 * 24 * 30;

    private final IssueManager issueManager;
    private final SearchService searchService;

    public IssueLocatorImpl(IssueManager issueManager, SearchService searchService) {
        this.issueManager = issueManager;
        this.searchService = searchService;
    }

    @Override
    public MutableIssue find(HandlerModel model, MessageAdapter message, MessageHandlerErrorCollector monitor) {
        Project project = model.getProject();

        String subject = message.getSubject().replaceAll("\\W", " ");

        Query unresolvedQuery = JqlQueryBuilder.newClauseBuilder()
                .project(project.getId()).and()
                .unresolved().and()
                .summary(subject).buildQuery();

        Query recentlyResolvedQuery = JqlQueryBuilder.newClauseBuilder()
                .project(project.getId()).and()
                .resolutionDateBetween(new Date(System.currentTimeMillis() - RESOLUTION_DELTA), new Date()).and()
                .summary(subject).buildQuery();

        try {
            List<Issue> issues = searchService.search(project.getLead(), unresolvedQuery, new PagerFilter(1)).getIssues();

            if (issues.isEmpty()) {
                issues = searchService.search(project.getLead(), recentlyResolvedQuery, new PagerFilter(1)).getIssues();
            }

            if (!issues.isEmpty()) {
                Issue issue = issues.get(0);

                return issueManager.getIssueObject(issue.getId());
            }
        } catch (SearchException e) {
            LOG.error("Cannot search for an issue.", e);
        }

        return null;
    }
}
