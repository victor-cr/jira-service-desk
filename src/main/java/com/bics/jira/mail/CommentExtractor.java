package com.bics.jira.mail;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.bics.jira.mail.model.HandlerModel;
import com.bics.jira.mail.model.MessageAdapter;

import javax.mail.MessagingException;
import java.util.Collection;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 04.02.13 21:57
 */
public interface CommentExtractor {
    String extractComment(HandlerModel model, MessageAdapter message) throws MessagingException;

    String extractBody(HandlerModel model, MessageAdapter message) throws MessagingException;
}
