package com.bics.jira.mail.converter.html;

import org.jsoup.nodes.Node;

import java.util.regex.Pattern;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 13/04/13 11:41
 */
public class WikiTable implements NodeFormatter {
    private static final Pattern EMPTY_TABLE = Pattern.compile("[ \n\r|]*");

    @Override
    public boolean isSupported(TreeContext context, Node node) {
        return Tag.TABLE.is(node) && !context.hasChild(Tag.TABLE);
    }

    @Override
    public void format(TreeContext context, Node node) {
        TreeContext.Checkpoint checkpoint = context.checkpoint();

        context.newLine().content();

        String diff = checkpoint.diff();

        if (diff == null || EMPTY_TABLE.matcher(diff).matches()) {
            checkpoint.rollback();
        }

        context.newLine();
    }
}
