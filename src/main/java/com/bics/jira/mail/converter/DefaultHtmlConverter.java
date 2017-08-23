package com.bics.jira.mail.converter;

import com.atlassian.mail.converters.basic.HtmlToTextConverter;
import com.bics.jira.mail.model.mail.Attachment;
import com.bics.jira.mail.model.mail.Body;
import com.bics.jira.mail.model.mail.MessageAdapter;

import java.io.IOException;
import java.util.Collection;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 10.02.13 1:54
 */
public class DefaultHtmlConverter implements BodyConverter {
    private final HtmlToTextConverter htmlToTextConverter = new HtmlToTextConverter();

    @Override
    public boolean isSupported(MessageAdapter message, boolean stripQuotes) {
        return true;
    }

    @Override
    public Body convert(String body, Collection<Attachment> attachments) {
        try {
            body = htmlToTextConverter.convert(body);

            return new Body(body, attachments);
        } catch (IOException e) {
            return new Body("*Cannot render description*", attachments); //Nothing should be here.
        }
    }
}
