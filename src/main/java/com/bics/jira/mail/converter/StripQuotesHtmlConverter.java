package com.bics.jira.mail.converter;

import com.atlassian.core.util.ClassLoaderUtils;
import com.bics.jira.mail.converter.html.IgnoreOutlookQuote;
import com.bics.jira.mail.converter.html.IgnoreQuote;
import com.bics.jira.mail.converter.html.NodeFormatter;
import com.bics.jira.mail.converter.html.Tag;
import com.bics.jira.mail.converter.html.TreeContext;
import com.bics.jira.mail.converter.html.WikiText;
import com.bics.jira.mail.model.mail.MessageAdapter;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 10.02.13 1:54
 */
public class StripQuotesHtmlConverter extends OutlookHtmlConverter implements BodyConverter {
    private final Collection<NodeFormatter> formatters;

    public StripQuotesHtmlConverter() {
        List<NodeFormatter> formatters = new LinkedList<NodeFormatter>(super.getFormatters());

        Iterables.removeIf(formatters, new Predicate<NodeFormatter>() {
            @Override
            public boolean apply(@Nullable NodeFormatter input) {
                return input instanceof WikiText;
            }
        });

        formatters.add(new PlainAnswerIgnore());
        formatters.add(new IgnoreOutlookQuote());
        formatters.add(new IPhoneAnswerIgnore());
        formatters.add(new WindowsPhoneAnswerIgnore());

        this.formatters = formatters;
    }

    @Override
    public boolean isSupported(MessageAdapter message, boolean stripQuotes) {
        return stripQuotes && super.isSupported(message, stripQuotes);
    }

    @Override
    protected Collection<NodeFormatter> getFormatters() {
        return formatters;
    }

    private static class IPhoneAnswerIgnore extends IgnoreQuote {
        private static final String HTML_TYPE_ATTR = "type";
        private static final String TYPE_VALUE = "cite";

        @Override
        public boolean isSupported(TreeContext context, Node node) {
            return Tag.BLOCKQUOTE.is(node) && node.hasAttr(HTML_TYPE_ATTR) && TYPE_VALUE.equalsIgnoreCase(node.attr(HTML_TYPE_ATTR));
        }
    }

    private static class WindowsPhoneAnswerIgnore extends IgnoreQuote {
        private static final String HTML_DIR_ATTR = "dir";
        private static final String DIR_VALUE = "ltr";

        @Override
        public boolean isSupported(TreeContext context, Node node) {
            if (Tag.DIV.is(node) && node.hasAttr(HTML_DIR_ATTR) && DIR_VALUE.equalsIgnoreCase(node.attr(HTML_DIR_ATTR))) {
                for (Node child : node.childNodes()) {
                    if (Tag.HR.is(child)) {
                        return true;
                    } else if (child instanceof Element) {
                        return false;
                    }
                }
            }

            return false;
        }
    }

    private static class PlainAnswerIgnore extends WikiText {
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
}
