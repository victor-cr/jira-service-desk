package com.bics.jira.mail.converter;

import com.bics.jira.mail.model.HandlerModel;
import com.bics.jira.mail.model.MessageAdapter;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 10.02.13 1:54
 */
public class DefaultTextConverter implements BodyConverter {
    @Override
    public boolean isSupported(HandlerModel model, MessageAdapter message, boolean full) {
        return true;
    }

    @Override
    public String convert(String body) {
        return body;
    }
}
