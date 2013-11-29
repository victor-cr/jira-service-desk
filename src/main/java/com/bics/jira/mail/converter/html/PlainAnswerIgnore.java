package com.bics.jira.mail.converter.html;

import com.atlassian.core.util.ClassLoaderUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.jsoup.nodes.Node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
* Java Doc here
*
* @author Victor Polischuk
* @since 22/11/13 15:47
*/
public class PlainAnswerIgnore extends WikiText {
    private final Collection<String> separators = new ArrayList<String>();

    public PlainAnswerIgnore() {
        try {
            LineIterator i = IOUtils.lineIterator(ClassLoaderUtils.getResourceAsStream("outlook-email.translations", getClass()), "UTF-8");

            while (i.hasNext()) {
                separators.add(i.nextLine());
            }
        } catch (IOException e) {
            // ignore
        }
    }

    @Override
    public void format(TreeContext context, Node node) {
        if (!separators.isEmpty()) {
            String text = getText(node);

            for (String separator : separators) {
                int i = text.indexOf(separator);

                if (i != -1) {
                    context.text(text.substring(0, i));
                    context.stop();
                }
            }
        }
        super.format(context, node);
    }
}
