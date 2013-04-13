package com.bics.jira.mail.converter.html;

import org.jsoup.nodes.Node;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 13/04/13 11:42
 */
public interface NodeFormatter {
    boolean isSupported(TreeContext context, Node node);

    void format(TreeContext context, Node node);
}
