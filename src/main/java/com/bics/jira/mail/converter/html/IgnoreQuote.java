package com.bics.jira.mail.converter.html;

import com.bics.jira.mail.converter.html.NodeFormatter;
import com.bics.jira.mail.converter.html.TreeContext;
import org.jsoup.nodes.Node;

/**
* Java Doc here
*
* @author Victor Polischuk
* @since 21/11/13 14:39
*/
public abstract class IgnoreQuote implements NodeFormatter {
    @Override
    public void format(TreeContext context, Node node) {
        context.stop();
    }
}
