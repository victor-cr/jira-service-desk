package com.bics.jira.mail.converter.html;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.regex.Pattern;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 13/04/13 11:41
 */
public class WikiText implements NodeFormatter {
    private static final Pattern[] CHECKS = {
            Pattern.compile("[*].+[*]"),
            Pattern.compile("[?][?].+[?][?]"),
            Pattern.compile("[+].+[+]"),
            Pattern.compile("\\^.+\\^"),
            Pattern.compile("-.+-"),
            Pattern.compile("_.+_"),
            Pattern.compile("~.+~"),
            Pattern.compile("^bq\\.\\s"),
            Pattern.compile("^h1.\\s"),
            Pattern.compile("^h2.\\s"),
            Pattern.compile("^h3.\\s"),
            Pattern.compile("^h4.\\s"),
            Pattern.compile("^h5.\\s"),
            Pattern.compile("^h6.\\s"),
            Pattern.compile("\\\\"),
            Pattern.compile("----"),
            Pattern.compile("\\[.+]"),
            Pattern.compile("^[-*#]\\s"),
            Pattern.compile("!.+!"),
            Pattern.compile("\\|.+\\|"),
            Pattern.compile("\\{.+}")
    };
    private static final String NO_FORMAT = "{noformat}";

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
        text = StringUtils.stripToEmpty(text);

        if (StringUtils.isNotBlank(text)) {
            if (isNotSafe(text)) {
                context.symbol(NO_FORMAT).text(text).symbol(NO_FORMAT);
            } else {
                context.text(text);
            }
        }
    }

    private boolean isNotSafe(String text) {
        for (Pattern check : CHECKS) {
            if (check.matcher(text).find()) {
                return true;
            }
        }

        return false;
    }
}
