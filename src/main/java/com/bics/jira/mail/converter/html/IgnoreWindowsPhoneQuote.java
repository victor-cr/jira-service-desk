package com.bics.jira.mail.converter.html;

import com.bics.jira.mail.converter.html.IgnoreQuote;
import com.bics.jira.mail.converter.html.Tag;
import com.bics.jira.mail.converter.html.TreeContext;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

/**
* Java Doc here
*
* @author Victor Polischuk
* @since 22/11/13 15:45
*/
public class IgnoreWindowsPhoneQuote extends IgnoreQuote {
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
