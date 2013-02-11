package com.bics.jira.mail.converter;

import com.bics.jira.mail.model.MimeType;

import javax.mail.Message;
import java.util.Collection;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 10.02.13 1:54
 */
public class Outlook2010HtmlConverter implements BodyConverter {
    @Override
    public boolean isSupported(Message message, MimeType mimeType) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<String> convert(String body) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
