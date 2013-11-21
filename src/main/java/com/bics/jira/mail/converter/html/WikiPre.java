package com.bics.jira.mail.converter.html;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Node;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 13/04/13 11:41
 */
public class WikiPre implements NodeFormatter {
    @Override
    public boolean isSupported(TreeContext context, Node node) {
        return Tag.PRE.is(node);
    }

    @Override
    public void format(TreeContext context, Node node) {
        TreeContext.Checkpoint checkpoint = context.checkpoint();

        context.formatted(true).content().formatted(false);

        if (StringUtils.stripToEmpty(checkpoint.diff()).isEmpty()) {
            checkpoint.rollback();
        }
    }
}
