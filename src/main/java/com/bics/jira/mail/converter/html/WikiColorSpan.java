package com.bics.jira.mail.converter.html;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Node;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 13/04/13 11:41
 */
public class WikiColorSpan implements NodeFormatter {
    private static final String HTML_ATTR_STYLE = "style";
    private static final String MACRO_COLOR_OPEN = "{color";
    private static final String MACRO_COLOR_CLOSE = "}";
    private static final Pattern COLOR_REGEX = Pattern.compile("(?:^color:|[ ;]color:)(#[0-9A-F]{3,6}|\\w+)", Pattern.CASE_INSENSITIVE);


    @Override
    public boolean isSupported(TreeContext context, Node node) {
        return Tag.SPAN.is(node) && !context.hasParent(Tag.A) && node.hasAttr(HTML_ATTR_STYLE) && COLOR_REGEX.matcher(node.attr(HTML_ATTR_STYLE)).find();
    }

    @Override
    public void format(TreeContext context, Node node) {
        TreeContext.Checkpoint checkpoint = context.checkpoint();

        Matcher matcher = COLOR_REGEX.matcher(node.attr(HTML_ATTR_STYLE));

        if (!matcher.find()) {
            return;
        }

        String start = MACRO_COLOR_OPEN + ":" + matcher.group(1) + MACRO_COLOR_CLOSE;

        context.symbol(start).content();

        if (StringUtils.isBlank(StringUtils.substringAfter(checkpoint.diff(), start))) {
            checkpoint.rollback();
        } else {
            context.symbol(MACRO_COLOR_OPEN + MACRO_COLOR_CLOSE);
        }
    }
}
