package com.bics.jira.mail.converter.html;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 13/04/13 11:41
 */
public class WikiText implements NodeFormatter {
    @Override
    public boolean isSupported(TreeContext context, Node node) {
        return node instanceof TextNode;
    }

    @Override
    public void format(TreeContext context, Node node) {
        String text = ((TextNode) node).text();

        text = StringUtils.replaceChars(text, '\u00A0', ' ');
        text = StringUtils.replaceChars(text, '\u2007', ' ');
        text = StringUtils.replaceChars(text, '\u202F', ' ');
        text = StringUtils.stripToEmpty(text);

        if (StringUtils.isNotBlank(text)) {
            context.append(text);
        }
    }
}
