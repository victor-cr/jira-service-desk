package com.bics.jira.mail;

import com.bics.jira.mail.model.HandlerModel;
import com.bics.jira.mail.model.MessageAdapter;

import javax.mail.MessagingException;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 04.02.13 21:57
 */
public interface MailHelper {
    String extractComment(HandlerModel model, MessageAdapter message) throws MessagingException;

    String extractBody(HandlerModel model, MessageAdapter message) throws MessagingException;
}
