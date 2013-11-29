package com.bics.jira.mail.converter.html;

import org.jsoup.nodes.Node;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 21/11/13 14:41
 */
public class IgnoreOutlookQuote extends IgnoreQuote {
    private static final String HTML_STYLE_ATTR = "style";
    private static final String STYLE_VALUE = "border:none;border-top:solid #B5C4DF 1.0pt;padding:3.0pt 0cm 0cm 0cm";

    @Override
    public boolean isSupported(TreeContext context, Node node) {
        return Tag.DIV.is(node) && node.hasAttr(HTML_STYLE_ATTR) && STYLE_VALUE.equalsIgnoreCase(node.attr(HTML_STYLE_ATTR));
    }
}
