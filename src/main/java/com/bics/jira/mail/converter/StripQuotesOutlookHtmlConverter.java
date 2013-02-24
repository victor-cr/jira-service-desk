package com.bics.jira.mail.converter;

import com.bics.jira.mail.model.HandlerModel;
import com.bics.jira.mail.model.MessageAdapter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeVisitor;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 10.02.13 1:54
 */
public class StripQuotesOutlookHtmlConverter extends OutlookHtmlConverter implements BodyConverter {
    private static final String DIV_STYLE = "border:none;border-top:solid #B5C4DF 1.0pt;padding:3.0pt 0cm 0cm 0cm";
    @Override
    public boolean isSupported(HandlerModel model, MessageAdapter message, boolean stripQuotes) {
        return stripQuotes && super.isSupported(model, message, stripQuotes);
    }

    @Override
    public String convert(String body) {
        Document document = Jsoup.parse(body);
        WikiNodeVisitor visitor = new StripWikiNodeVisitor();

        document.traverse(visitor);

        return visitor.out.toString();
    }

    private static class StripWikiNodeVisitor extends WikiNodeVisitor implements NodeVisitor {
        private boolean strip;

        @Override
        protected boolean shallIgnore(Node node, int depth) {
            if (strip || super.shallIgnore(node, depth)) {
                return true;
            }

            strip = "div".equalsIgnoreCase(node.nodeName()) && DIV_STYLE.equalsIgnoreCase(node.attr("style"));

            return strip;
        }
    }
}
