package com.bics.jira.mail.converter;

import com.atlassian.core.util.ClassLoaderUtils;
import com.bics.jira.mail.model.mail.Attachment;
import com.bics.jira.mail.model.mail.Body;
import com.bics.jira.mail.model.mail.MessageAdapter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 10.02.13 1:54
 */
public class StripQuotesTextConverter implements BodyConverter {
    private final Collection<String> separators = new ArrayList<String>();

    public StripQuotesTextConverter() throws IOException {
        LineIterator i = IOUtils.lineIterator(ClassLoaderUtils.getResourceAsStream("outlook-email.translations", getClass()), "UTF-8");

        while (i.hasNext()) {
            separators.add(i.nextLine());
        }
    }

    @Override
    public boolean isSupported(MessageAdapter message, boolean stripQuotes) {
        return stripQuotes;
    }

    @Override
    public Body convert(String body, Collection<Attachment> attachments) {
        int min = body.length() - 1;

        for (String separator : separators) {
            int current = body.indexOf(separator, 0);

            if (min > current && current != -1) {
                min = current;
            }
        }

        return new Body(body.substring(0, min), attachments);
    }
}
