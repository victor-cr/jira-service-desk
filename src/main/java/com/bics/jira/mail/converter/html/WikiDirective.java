package com.bics.jira.mail.converter.html;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Node;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 13/04/13 11:41
 */
public class WikiDirective implements NodeFormatter {
    private static final String DIRECTIVE_OPEN = "[if ";
    private static final String DIRECTIVE_CLOSE = "[endif]";

    @Override
    public boolean isSupported(TreeContext context, Node node) {
        if (!(node instanceof Comment)) {
            return false;
        }

        String data = ((Comment) node).getData();

        return StringUtils.startsWithIgnoreCase(data, DIRECTIVE_OPEN) || StringUtils.startsWithIgnoreCase(data, DIRECTIVE_CLOSE);
    }

    @Override
    public void format(TreeContext context, Node node) {
        String data = ((Comment) node).getData();

        context.ignore(StringUtils.startsWithIgnoreCase(data, DIRECTIVE_OPEN));
    }
}
