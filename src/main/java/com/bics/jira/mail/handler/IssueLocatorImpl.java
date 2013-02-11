package com.bics.jira.mail.handler;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;
import com.atlassian.query.QueryImpl;
import com.bics.jira.mail.IssueLocator;
import com.bics.jira.mail.model.HandlerModel;
import com.bics.jira.mail.model.MessageAdapter;
import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Scorer;

import javax.mail.MessagingException;
import java.io.IOException;
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

    private final SearchService searchService;

    public IssueLocatorImpl(SearchService searchService) {
        this.searchService = searchService;
    }

    @Override
    public Issue find(HandlerModel model, MessageAdapter message, MessageHandlerErrorCollector monitor) {
        Project project = model.getProject();

        Query query = JqlQueryBuilder.newClauseBuilder()
                .project(project.getId())
                .resolutionDateBetween(new Date(System.currentTimeMillis() - RESOLUTION_DELTA), new Date())
                .summary(message.getSubject())
                .buildQuery();

        try {
            SearchResults results = searchService.search(project.getLead(), query, PagerFilter.getUnlimitedFilter());

            List<Issue> issues = results.getIssues();

            if (!issues.isEmpty()) {
                return issues.get(0);
            }
        } catch (SearchException e) {
            LOG.error("Cannot search for an issue.", e);
        }

        return null;
    }
}
