package com.bics.jira.mail.handler;

import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueFactory;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.bics.jira.mail.IssueBuilder;
import com.bics.jira.mail.model.MessageAdapter;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 04.02.13 21:38
 */
public class IssueBuilderImpl implements IssueBuilder {
    private final IssueManager issueManager;
    private final IssueFactory issueFactory;

    public IssueBuilderImpl(IssueManager issueManager, IssueFactory issueFactory) {
        this.issueManager = issueManager;
        this.issueFactory = issueFactory;
    }

    @Override
    public Issue build(Project project, MessageAdapter message, MessageHandlerErrorCollector monitor) throws CreateException {
        MutableIssue issue = issueFactory.getIssue();

        issue.se

        return null;
    }
}
