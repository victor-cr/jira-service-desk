package com.bics.jira.mail.converter;

import com.atlassian.core.util.ClassLoaderUtils;
import com.bics.jira.mail.model.HandlerModel;
import com.bics.jira.mail.model.MessageAdapter;
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
    public boolean isSupported(HandlerModel model, MessageAdapter message, boolean full) {
        return !full && model.isStripQuotes();
    }

    @Override
    public String convert(String body) {
        int min = body.length() - 1;

        for (String separator : separators) {
            int current = body.indexOf(separator, 0);

            if (min > current && current != -1) {
                min = current;
            }
        }

        return body.substring(0, min);
    }
}
