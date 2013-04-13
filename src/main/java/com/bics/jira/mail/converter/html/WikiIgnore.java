package com.bics.jira.mail.converter.html;

import org.jsoup.nodes.Comment;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Node;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 13/04/13 11:41
 */
public class WikiIgnore implements NodeFormatter {
    @Override
    public boolean isSupported(TreeContext context, Node node) {
        return Tag.STYLE.is(node) || Tag.SCRIPT.is(node) || Tag.HEAD.is(node) || node instanceof Comment || node instanceof DocumentType;
    }

    @Override
    public void format(TreeContext context, Node node) {
    }
}
