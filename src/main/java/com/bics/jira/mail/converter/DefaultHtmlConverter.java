package com.bics.jira.mail.converter;

import com.atlassian.mail.HtmlToTextConverter;
import com.bics.jira.mail.model.HandlerModel;
import com.bics.jira.mail.model.MessageAdapter;

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
    public boolean isSupported(HandlerModel model, MessageAdapter message, boolean full) {
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