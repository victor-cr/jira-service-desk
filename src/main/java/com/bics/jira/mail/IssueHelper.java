package com.bics.jira.mail;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.exception.PermissionException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.atlassian.jira.web.util.AttachmentException;
import com.bics.jira.mail.model.mail.MessageAdapter;
import com.bics.jira.mail.model.ServiceDeskModel;

import javax.mail.MessagingException;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 04.02.13 21:57
 */
public interface IssueHelper<M extends ServiceDeskModel> {
    Issue process(User author, M model, MessageAdapter message, MessageHandlerErrorCollector monitor) throws MessagingException, CreateException, AttachmentException, PermissionException;
}
