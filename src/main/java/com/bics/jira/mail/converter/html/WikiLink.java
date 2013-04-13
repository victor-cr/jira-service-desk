package com.bics.jira.mail.converter.html;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Node;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 13/04/13 11:41
 */
public class WikiLink implements NodeFormatter {
    private static final String LINK_START = "[";
    private static final String LINK_STOP = "]";
    private static final String HTML_HREF = "href";

    @Override
    public boolean isSupported(TreeContext context, Node node) {
        return Tag.A.is(node) && node.hasAttr(HTML_HREF);
    }

    @Override
    public void format(TreeContext context, Node node) {
        String link = encode(url(node));

        if (StringUtils.isNotBlank(link)) {
            context.appendInner().whitespace().append(LINK_START).append(link).append(LINK_STOP).whitespace();
        }
    }

    protected String url(Node node) {
        return node.attr(HTML_HREF);
    }

    protected String encode(String link) {
        if (StringUtils.isBlank(link)) {
            return null;
        }

        try {
            return new URL(link).toExternalForm();
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
