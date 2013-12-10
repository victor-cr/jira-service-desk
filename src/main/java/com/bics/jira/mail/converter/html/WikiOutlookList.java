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
public class WikiOutlookList implements NodeFormatter {
    private static final String OL = "*";
    private static final String ATTR_STYLE = "style";
    private static final String MSO_LIST_KEY = "mso-list:";
    private static final String IGNORE_MSO_LIST_KEY = "mso-list:Ignore";
    private static final Pattern LIST_DEPTH = Pattern.compile("level(\\d+)");

    @Override
    public boolean isSupported(TreeContext context, Node node) {
        return Tag.P.is(node) && node.hasAttr(ATTR_STYLE) && node.attr(ATTR_STYLE).contains(MSO_LIST_KEY) && !node.attr(ATTR_STYLE).contains(IGNORE_MSO_LIST_KEY);
    }

    @Override
    public void format(TreeContext context, Node node) {
        String style = StringUtils.substringAfter(node.attr(ATTR_STYLE), MSO_LIST_KEY);

        Matcher matcher = LIST_DEPTH.matcher(style);
        String prefix = "*";

        if (matcher.find()) {
            try {
                int depth = Integer.parseInt(matcher.group(1));
                prefix = StringUtils.repeat(OL, depth);
            } catch (NumberFormatException e) {
                // ignore
            }
        }

        context.newLine().symbol(prefix).whitespace().trimContent();
    }
}
