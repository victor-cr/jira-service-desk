package com.bics.jira.mail.converter.html;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import javax.annotation.Nullable;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 13/04/13 11:41
 */
public class WikiCell implements NodeFormatter {
    private static final String HTML_ATTR_NOWRAP = "nowrap";
    private static final String HTML_ATTR_COLSPAN = "colspan";
    private static final String HTML_ATTR_ROWSPAN = "rowspan";
    private static final String CELL_STOP = "|";
    private static final Node EMPTY = new Element(Tag.TD.toTag(), "");
    private static final Filter FILTER = new Filter();


    @Override
    public boolean isSupported(TreeContext context, Node node) {
        return Tag.TD.is(node) && context.hasParent(Tag.TR);
    }

    @Override
    public void format(TreeContext context, Node node) {
        int colspan = span(node, HTML_ATTR_COLSPAN);
        int rowspan = span(node, HTML_ATTR_ROWSPAN);

        while (colspan-- > 1) {
            node.after(empty());
        }

        Node parent = node.parent();

        int i = Iterables.indexOf(Iterables.filter(parent.childNodes(), FILTER), Predicates.equalTo(node));

        while (rowspan-- > 1 && (parent = parent.nextSibling()) != null) {
            if (!Tag.TR.is(parent)) {
                rowspan++;
                continue;
            }

            Iterable<Node> nodes = Iterables.filter(parent.childNodes(), FILTER);

            int index = Math.min(Iterables.size(nodes) - 1, i);

            Node target = Iterables.get(nodes, index);

            if (i > index) {
                target.after(empty());
            } else {
                target.before(empty());
            }
        }

        printLeft(context);

        context.nowrap().appendInner();

        printRight(context);
    }

    protected void printLeft(TreeContext context) {
        context.whitespace();
    }

    protected void printRight(TreeContext context) {
        context.whitespace().append(CELL_STOP);
    }

    protected Node empty() {
        return EMPTY.clone();
    }

    protected int span(Node node, String attr) {
        String colspan = node.attr(attr);

        try {
            return Integer.parseInt(colspan);
        } catch (RuntimeException e) {
            return 1;
        }
    }

    protected static class Filter implements Predicate<Node> {
        @Override
        public boolean apply(@Nullable Node node) {
            return Tag.TD.is(node) || Tag.TH.is(node);
        }
    }
}
