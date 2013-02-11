package com.bics.jira.mail;

import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.bics.jira.mail.model.HandlerModel;
import com.bics.jira.mail.model.MessageAdapter;

import javax.mail.MessagingException;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 04.02.13 21:57
 */
public interface IssueBuilder {
    Issue build(HandlerModel model, MessageAdapter message, MessageHandlerErrorCollector monitor) throws MessagingException;
}
