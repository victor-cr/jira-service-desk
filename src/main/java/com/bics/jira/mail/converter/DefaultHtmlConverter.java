package com.bics.jira.mail.converter;

import com.atlassian.mail.HtmlToTextConverter;
import com.bics.jira.mail.model.CreateOrCommentModel;
import com.bics.jira.mail.model.ServiceDeskModel;
import com.bics.jira.mail.model.mail.MessageAdapter;

import java.io.IOException;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 10.02.13 1:54
 */
public class DefaultHtmlConverter implements BodyConverter {
    private final HtmlToTextConverter htmlToTextConverter = new HtmlToTextConverter();

    @Override
    public boolean isSupported(ServiceDeskModel model, MessageAdapter message, boolean stripQuotes) {
        return true;
    }

    @Override
    public String convert(String body) {
        try {
            return htmlToTextConverter.convert(body);
        } catch (IOException e) {
            return "*Cannot render description*"; //Nothing should be here.
        }
    }
}
