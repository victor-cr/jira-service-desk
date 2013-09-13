package com.bics.jira.mail;

import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 04.02.13 21:57
 */
public interface IssueLookupHelper {
    MutableIssue lookupByKey(String text, long resolvedBefore, MessageHandlerErrorCollector monitor);

    MutableIssue lookupBySubject(Project project, String subject, long resolvedBefore, MessageHandlerErrorCollector monitor);
}
