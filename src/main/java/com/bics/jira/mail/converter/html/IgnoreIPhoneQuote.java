package com.bics.jira.mail.converter.html;

import com.bics.jira.mail.converter.html.IgnoreQuote;
import com.bics.jira.mail.converter.html.Tag;
import com.bics.jira.mail.converter.html.TreeContext;
import org.jsoup.nodes.Node;

/**
* Java Doc here
*
* @author Victor Polischuk
* @since 22/11/13 15:46
*/
public class IgnoreIPhoneQuote extends IgnoreQuote {
    private static final String HTML_TYPE_ATTR = "type";
    private static final String TYPE_VALUE = "cite";

    @Override
    public boolean isSupported(TreeContext context, Node node) {
        return Tag.BLOCKQUOTE.is(node) && node.hasAttr(HTML_TYPE_ATTR) && TYPE_VALUE.equalsIgnoreCase(node.attr(HTML_TYPE_ATTR));
    }
}
