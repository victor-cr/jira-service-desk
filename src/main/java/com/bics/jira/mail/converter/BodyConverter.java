package com.bics.jira.mail.converter;

import com.bics.jira.mail.model.CreateOrCommentModel;
import com.bics.jira.mail.model.ServiceDeskModel;
import com.bics.jira.mail.model.mail.MessageAdapter;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 10.02.13 1:54
 */
public interface BodyConverter {
    boolean isSupported(ServiceDeskModel model, MessageAdapter message, boolean stripQuotes);

    String convert(String body);
}
