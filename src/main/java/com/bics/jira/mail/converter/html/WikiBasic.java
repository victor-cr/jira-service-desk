package com.bics.jira.mail.converter.html;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Node;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 13/04/13 11:41
 */
public class WikiBasic implements NodeFormatter {
    private final String symbol;
    private final Tag[] tags;

    public WikiBasic(String symbol, Tag... tags) {
        this.symbol = symbol;
        this.tags = tags;
    }

    @Override
    public boolean isSupported(TreeContext context, Node node) {
        for (Tag tag : tags) {
            if (tag.is(node)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void format(TreeContext context, Node node) {
        TreeContext.Checkpoint checkpoint = context.checkpoint();

        for (Tag tag : tags) {
            if (context.hasParent(tag)) {
                context.whitespace().content().whitespace();

                if (StringUtils.isBlank(checkpoint.diff())) {
                    checkpoint.rollback();
                }

                return;
            }
        }

        context.whitespace().symbol(symbol).trimContent().symbol(symbol).whitespace();

        if (StringUtils.trimToEmpty(checkpoint.diff()).equals(symbol + symbol)) {
            checkpoint.rollback();
        }
    }
}
