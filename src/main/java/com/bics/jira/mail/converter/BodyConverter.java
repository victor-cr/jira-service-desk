package com.bics.jira.mail.converter;

import com.bics.jira.mail.model.mail.Attachment;
import com.bics.jira.mail.model.mail.Body;
import com.bics.jira.mail.model.mail.MessageAdapter;

import java.util.Collection;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 10.02.13 1:54
 */
public interface BodyConverter {
    boolean isSupported(MessageAdapter message, boolean stripQuotes);

    Body convert(String body, Collection<Attachment> attachments);
}
