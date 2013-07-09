package com.bics.jira.mail.converter.html;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 13/04/13 11:41
 */
public class WikiText implements NodeFormatter {
    private static final Pattern[] CHECKS = {
            Pattern.compile("(?<!\\w)([\\-*+_~!{\\|\\[\\^])(?=\\w)"),
            Pattern.compile("(?<=\\w)([\\-*+_~!}\\|\\]\\^])(?!\\w)"),
            Pattern.compile("(?<!\\w)([?]{2})(?=\\w)"),
            Pattern.compile("(?<=\\w)([?]{2})(?!\\w)"),
            Pattern.compile("^(bq\\.\\s)"),
            Pattern.compile("^(h1\\.\\s)"),
            Pattern.compile("^(h2\\.\\s)"),
            Pattern.compile("^(h3\\.\\s)"),
            Pattern.compile("^(h4\\.\\s)"),
            Pattern.compile("^(h5\\.\\s)"),
            Pattern.compile("^(h6\\.\\s)"),
            Pattern.compile("(\\\\{2})"),
            Pattern.compile("^(----\\s)"),
            Pattern.compile("^([-*#]\\s)")
    };

    @Override
    public boolean isSupported(TreeContext context, Node node) {
        return node instanceof TextNode;
    }

    @Override
    public void format(TreeContext context, Node node) {
        String text = ((TextNode) node).text();

        text = StringUtils.replaceChars(text, '\u00A0', ' ');
        text = StringUtils.replaceChars(text, '\u2007', ' ');
        text = StringUtils.replaceChars(text, '\u202F', ' ');

        if (StringUtils.isNotBlank(text)) {
            for (Pattern check : CHECKS) {
                Matcher matcher = check.matcher(text);

                text = matcher.replaceAll("\\\\$1");
            }

            if (Character.isWhitespace(text.charAt(0))) {
                context.whitespace();
            }

            context.text(StringUtils.stripToEmpty(text));

            if (Character.isWhitespace(text.charAt(text.length() - 1))) {
                context.whitespace();
            }
        }
    }
}
