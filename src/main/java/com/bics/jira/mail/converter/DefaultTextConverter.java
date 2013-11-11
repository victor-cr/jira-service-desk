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
public class DefaultTextConverter implements BodyConverter {
    @Override
    public boolean isSupported(MessageAdapter message, boolean stripQuotes) {
        return true;
    }

    @Override
    public Body convert(String body, Collection<Attachment> attachments) {
        return new Body(body, attachments);
    }
}
