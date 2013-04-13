package com.bics.jira.mail.converter;

import com.bics.jira.mail.converter.html.NodeFormatter;
import com.bics.jira.mail.converter.html.Tag;
import com.bics.jira.mail.converter.html.TreeContext;
import com.bics.jira.mail.model.HandlerModel;
import com.bics.jira.mail.model.MessageAdapter;
import org.jsoup.nodes.Node;

import java.util.ArrayList;
import java.util.Collection;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 10.02.13 1:54
 */
public class StripQuotesOutlookHtmlConverter extends OutlookHtmlConverter implements BodyConverter {
    private final Collection<NodeFormatter> formatters;

    public StripQuotesOutlookHtmlConverter() {
        ArrayList<NodeFormatter> formatters = new ArrayList<NodeFormatter>(super.getFormatters());

        formatters.add(new OutlookAnswerIgnore());

        this.formatters = formatters;
    }

    @Override
    public boolean isSupported(HandlerModel model, MessageAdapter message, boolean stripQuotes) {
        return stripQuotes && super.isSupported(model, message, stripQuotes);
    }

    @Override
    protected Collection<NodeFormatter> getFormatters() {
        return formatters;
    }

    private static class OutlookAnswerIgnore implements NodeFormatter {
        private static final String DIV_STYLE = "border:none;border-top:solid #B5C4DF 1.0pt;padding:3.0pt 0cm 0cm 0cm";

        @Override
        public boolean isSupported(TreeContext context, Node node) {
            return Tag.DIV.is(node) && DIV_STYLE.equalsIgnoreCase(node.attr("style"));
        }

        @Override
        public void format(TreeContext context, Node node) {
        }
    }
}
