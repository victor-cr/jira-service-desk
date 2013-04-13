package com.bics.jira.mail.converter.html;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 13/04/13 11:41
 */
public class WikiHeaderCell extends WikiCell implements NodeFormatter {
    private static final String CELL_START = "|";
    private static final Node EMPTY = new Element(Tag.TH.toTag(), "");

    @Override
    public boolean isSupported(TreeContext context, Node node) {
        return Tag.TH.is(node) && context.hasParent(Tag.TR);
    }

    @Override
    protected void printLeft(TreeContext context) {
        context.append(CELL_START).whitespace();
    }

    @Override
    protected Node empty() {
        return EMPTY.clone();
    }
}
