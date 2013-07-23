package com.bics.jira.mail.converter;

import com.bics.jira.mail.model.mail.MessageAdapter;

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
    public String convert(String body) {
        return body;
    }
}
