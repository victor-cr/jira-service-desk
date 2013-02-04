package com.bics.jira.mail.handler;

import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.fields.Field;
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

    public IssueBuilderImpl(IssueManager issueManager) {
        this.issueManager = issueManager;
    }

    @Override
    public Issue build(Project project, Field customField, MessageAdapter message, MessageHandlerErrorCollector monitor) throws CreateException {
        return null;
    }
}
