package com.bics.jira.mail.converter.html.context;

import com.bics.jira.mail.converter.html.Tag;
import com.bics.jira.mail.converter.html.TreeContext;

/**
* Java Doc here
*
* @author Victor Polischuk
* @since 17/04/13 13:54
*/
public abstract class DelegatingTreeContext<T extends TreeContext> implements TreeContext {
    protected final T context;

    public DelegatingTreeContext(T context) {
        this.context = context;
    }

    @Override
    public void stop() {
        context.stop();
    }

    @Override
    public boolean hasChild(Tag tag) {
        return context.hasChild(tag);
    }

    @Override
    public boolean hasParent(Tag tag) {
        return context.hasParent(tag);
    }

    @Override
    public Iterable<Tag> path() {
        return context.path();
    }

    @Override
    public TreeContext optional() {
        return context.optional();
    }

    @Override
    public TreeContext newLine() {
        return context.newLine();
    }

    @Override
    public TreeContext whitespace() {
        return context.whitespace();
    }

    @Override
    public TreeContext glue() {
        return context.glue();
    }

    @Override
    public TreeContext nowrap() {
        return context.nowrap();
    }

    @Override
    public TreeContext append(String sequence) {
        return context.append(sequence);
    }

    @Override
    public TreeContext appendInner() {
        return context.appendInner();
    }
}
