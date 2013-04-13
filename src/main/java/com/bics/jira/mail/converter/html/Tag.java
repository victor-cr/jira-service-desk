package com.bics.jira.mail.converter.html;

import org.jsoup.nodes.Node;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 12/04/13 12:15
 */
public enum Tag {
    A,
    DIV,
    SPAN,
    HEAD,
    SCRIPT,
    STYLE,
    B,
    STRONG,
    I,
    EM,
    U,
    STRIKE,
    BR,
    P,
    TABLE,
    TR,
    TH,
    TD,
    UL,
    IMG,
    OL,
    LI;

    public boolean is(Node node) {
        return node != null && this.name().equalsIgnoreCase(node.nodeName());
    }

    public org.jsoup.parser.Tag toTag() {
        return org.jsoup.parser.Tag.valueOf(this.name());
    }

    public static Tag valueOf(Node node) {
        try {
            String nodeName = node.nodeName().toUpperCase();

            return Tag.valueOf(nodeName);
        } catch (RuntimeException e) {
            return null;
        }
    }
}
