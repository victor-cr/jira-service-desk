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
    private static final String INLINE_SUFFIX_SRC = "@";

    @Override
    public boolean isSupported(TreeContext context, Node node) {
        return Tag.IMG.is(node) && isInline(node);
    }

    @Override
    public void format(TreeContext context, Node node) {
        String src = node.attr(HTML_SRC);
        String width = node.attr(HTML_WIDTH);
        String height = node.attr(HTML_HEIGHT);

        String imageName = StringUtils.substringBeforeLast(src.substring(4), INLINE_SUFFIX_SRC);

        context.whitespace().append(IMAGE_START).append(imageName);

        boolean hasWidth = StringUtils.isNotBlank(width);

        if (hasWidth) {
            context.append(IMAGE_ATTR_SECTION).append(WIKI_WIDTH).append(IMAGE_ATTR_ASSIGNMENT).append(width);
        }

        if (StringUtils.isNotBlank(height)) {
            context.append(hasWidth ? IMAGE_ATTR_SEPARATOR : IMAGE_ATTR_SECTION).append(WIKI_HEIGHT).append(IMAGE_ATTR_ASSIGNMENT).append(height);
        }

        context.append(IMAGE_STOP).whitespace();
    }

    protected boolean isInline(Node node) {
        return StringUtils.startsWithIgnoreCase(node.attr(HTML_SRC), INLINE_PREFIX_SRC);
    }
}
