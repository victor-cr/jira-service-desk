package com.bics.jira.mail.converter;

import com.bics.jira.mail.converter.html.NodeFormatter;
import com.bics.jira.mail.converter.html.Tag;
import com.bics.jira.mail.converter.html.TreeContext;
import com.bics.jira.mail.model.mail.MessageAdapter;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.ArrayList;
import java.util.Collection;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 10.02.13 1:54
 */
public class StripQuotesHtmlConverter extends OutlookHtmlConverter implements BodyConverter {
    private final Collection<NodeFormatter> formatters;

    public StripQuotesHtmlConverter() {
        ArrayList<NodeFormatter> formatters = new ArrayList<NodeFormatter>(super.getFormatters());

        formatters.add(new OutlookAnswerIgnore());
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

    private abstract static class AnswerIgnore implements NodeFormatter {
        @Override
        public void format(TreeContext context, Node node) {
            context.stop();
        }
    }

    private static class OutlookAnswerIgnore extends AnswerIgnore {
        private static final String HTML_STYLE_ATTR = "attr";
        private static final String STYLE_VALUE = "border:none;border-top:solid #B5C4DF 1.0pt;padding:3.0pt 0cm 0cm 0cm";

        @Override
        public boolean isSupported(TreeContext context, Node node) {
            return Tag.DIV.is(node) && node.hasAttr(HTML_STYLE_ATTR) && STYLE_VALUE.equalsIgnoreCase(node.attr(HTML_STYLE_ATTR));
        }
    }

    private static class IPhoneAnswerIgnore extends AnswerIgnore {
        private static final String HTML_TYPE_ATTR = "type";
        private static final String TYPE_VALUE = "cite";

        @Override
        public boolean isSupported(TreeContext context, Node node) {
            return Tag.BLOCKQUOTE.is(node) && node.hasAttr(HTML_TYPE_ATTR) && TYPE_VALUE.equalsIgnoreCase(node.attr(HTML_TYPE_ATTR));
        }
    }

    private static class WindowsPhoneAnswerIgnore extends AnswerIgnore {
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
}
