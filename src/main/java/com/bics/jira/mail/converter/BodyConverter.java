package com.bics.jira.mail.converter;

import com.bics.jira.mail.model.mail.MessageAdapter;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 10.02.13 1:54
 */
public interface BodyConverter {
    boolean isSupported(MessageAdapter message, boolean stripQuotes);

    String convert(String body);
}
