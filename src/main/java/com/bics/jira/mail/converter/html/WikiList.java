package com.bics.jira.mail.converter.html;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import org.jsoup.nodes.Node;

import java.util.Arrays;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 13/04/13 11:41
 */
public class WikiList implements NodeFormatter {
    private static final String UL = "#";
    private static final String OL = "*";

    @Override
    public boolean isSupported(TreeContext context, Node node) {
        return Tag.LI.is(node);
    }

    @Override
    public void format(TreeContext context, Node node) {
        StringBuilder prefix = new StringBuilder();
        Iterable<Tag> tags = Iterables.filter(context.path(), Predicates.in(Arrays.asList(Tag.UL, Tag.OL)));

        for (Tag tag : tags) {
            switch (tag) {
                case UL:
                    context.append(UL);
                    break;
                case OL:
                    context.append(OL);
                    break;
            }
        }

        if (prefix.length() == 0) {
            prefix.append('-');
        } else {
            prefix.reverse();
        }

        context.newLine().append(prefix.toString()).whitespace().appendInner();
    }
}
