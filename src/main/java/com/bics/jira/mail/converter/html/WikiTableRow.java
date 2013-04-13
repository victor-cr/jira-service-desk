package com.bics.jira.mail.converter.html;

import org.jsoup.nodes.Node;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 13/04/13 11:41
 */
public class WikiTableRow implements NodeFormatter {
    private static final String ROW_START = "|";

    @Override
    public boolean isSupported(TreeContext context, Node node) {
        return Tag.TR.is(node) && context.hasParent(Tag.TABLE);
    }

    @Override
    public void format(TreeContext context, Node node) {
        context.newLine().append(ROW_START).appendInner().newLine();
    }
}
