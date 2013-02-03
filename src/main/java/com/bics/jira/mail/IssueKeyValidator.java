package com.bics.jira.mail;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import org.apache.commons.lang.StringUtils;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 03.02.13 12:57
 */
public class IssueKeyValidator {
    private final IssueManager issueManager;

    public IssueKeyValidator(IssueManager issueManager) {
        this.issueManager = issueManager;
    }

    public Issue validateIssue(String issueKey, MessageHandlerErrorCollector collector) {
        if (StringUtils.isBlank(issueKey)) {
            collector.error("Issue key has to be defined. Found '" + issueKey + "'");
            return null;
        }

        Issue issue = issueManager.getIssueObject(issueKey);

        if (issue == null) {
            collector.error("Cannot add a comment from mail to issue '" + issueKey + "'. The issue does not exist.");
            return null;
        }

        if (!issueManager.isEditable(issue)) {
            collector.error("Cannot add a comment from mail to issue '" + issueKey + "'. The issue is not editable.");
            return null;
        }

        return issue;
    }

}