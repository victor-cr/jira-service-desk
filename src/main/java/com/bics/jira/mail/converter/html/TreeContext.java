package com.bics.jira.mail.converter.html;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 13/04/13 11:42
 */
public interface TreeContext {
    void stop();

    boolean hasChild(Tag tag);

    boolean hasParent(Tag tag);

    Iterable<Tag> path();

    TreeContext optional();

    TreeContext newLine();

    TreeContext whitespace();

    TreeContext glue();

    TreeContext nowrap();

    TreeContext append(String sequence);

    TreeContext appendInner();
}
