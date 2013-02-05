package com.bics.jira.mail.handler;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.bics.jira.mail.IssueLocator;
import com.bics.jira.mail.model.MessageAdapter;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 04.02.13 21:38
 */
public class IssueLocatorImpl implements IssueLocator {
    private final IssueManager issueManager;

    public IssueLocatorImpl(IssueManager issueManager) {
        this.issueManager = issueManager;
    }

    @Override
    public Issue find(Project project, MessageAdapter message, MessageHandlerErrorCollector monitor) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
