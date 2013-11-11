package com.bics.jira.mail.converter.html;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Node;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 13/04/13 11:41
 */
public class WikiInlineImage implements NodeFormatter {
    private static final String IMAGE_START = "!";
    private static final String IMAGE_STOP = "!";
    private static final String IMAGE_ATTR_SECTION = "|";
    private static final String IMAGE_ATTR_SEPARATOR = ",";
    private static final String IMAGE_ATTR_ASSIGNMENT = "=";
    private static final String HTML_WIDTH = "width";
    private static final String WIKI_WIDTH = "width";
    private static final String HTML_HEIGHT = "height";
    private static final String WIKI_HEIGHT = "height";
    private static final String HTML_SRC = "src";
    private static final String INLINE_PREFIX_SRC = "cid:";

    @Override
    public boolean isSupported(TreeContext context, Node node) {
        return Tag.IMG.is(node) && isInline(node);
    }

    @Override
    public void format(TreeContext context, Node node) {
        String src = node.attr(HTML_SRC);
        String width = node.attr(HTML_WIDTH);
        String height = node.attr(HTML_HEIGHT);

        String imageName = context.getInlineName(StringUtils.substringAfter(src, INLINE_PREFIX_SRC));

        if (imageName == null) {
            return;
        }

        context.whitespace().symbol(IMAGE_START).symbol(imageName);

        boolean hasWidth = StringUtils.isNotBlank(width);

        if (hasWidth) {
            context.symbol(IMAGE_ATTR_SECTION).symbol(WIKI_WIDTH).symbol(IMAGE_ATTR_ASSIGNMENT).symbol(width);
        }

        if (StringUtils.isNotBlank(height)) {
            context.symbol(hasWidth ? IMAGE_ATTR_SEPARATOR : IMAGE_ATTR_SECTION).symbol(WIKI_HEIGHT).symbol(IMAGE_ATTR_ASSIGNMENT).symbol(height);
        }

        context.symbol(IMAGE_STOP).whitespace();
    }

    protected boolean isInline(Node node) {
        return StringUtils.startsWithIgnoreCase(node.attr(HTML_SRC), INLINE_PREFIX_SRC);
    }
}
