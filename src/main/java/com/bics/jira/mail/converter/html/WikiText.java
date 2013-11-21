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
            Pattern.compile("(?<=\\()([+])(?=\\))"),
            Pattern.compile("^(bq\\.\\s)", Pattern.MULTILINE),
            Pattern.compile("^(h1\\.\\s)", Pattern.MULTILINE),
            Pattern.compile("^(h2\\.\\s)", Pattern.MULTILINE),
            Pattern.compile("^(h3\\.\\s)", Pattern.MULTILINE),
            Pattern.compile("^(h4\\.\\s)", Pattern.MULTILINE),
            Pattern.compile("^(h5\\.\\s)", Pattern.MULTILINE),
            Pattern.compile("^(h6\\.\\s)", Pattern.MULTILINE),
            Pattern.compile("(\\\\{2})"),
            Pattern.compile("^(----\\s*)$", Pattern.MULTILINE),
            Pattern.compile("^([-*#]\\s)", Pattern.MULTILINE)
    };

    @Override
    public boolean isSupported(TreeContext context, Node node) {
        return node instanceof TextNode;
    }

    @Override
    public void format(TreeContext context, Node node) {
        String text = getText(node);

        boolean notBlank = StringUtils.isNotBlank(text);

        if (notBlank) {
            for (Pattern check : CHECKS) {
                Matcher matcher = check.matcher(text);

                text = matcher.replaceAll("\\\\$1");
            }

            if (Character.isWhitespace(text.charAt(0))) {
                context.whitespace();
            }
        }

        context.text(text);

        if (notBlank && Character.isWhitespace(text.charAt(text.length() - 1))) {
            context.whitespace();
        }
    }

    protected static String getText(Node node) {
        String text = ((TextNode) node).getWholeText();

        return StringUtils.replaceChars(text, "\u00A0\u2007\u202F", "   ");
    }
}
