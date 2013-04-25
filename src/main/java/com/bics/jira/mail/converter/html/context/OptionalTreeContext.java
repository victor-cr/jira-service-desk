package com.bics.jira.mail.converter.html.context;

import com.bics.jira.mail.converter.html.Tag;
import com.bics.jira.mail.converter.html.TreeContext;

/**
* Java Doc here
*
* @author Victor Polischuk
* @since 17/04/13 13:54
*/
public class OptionalTreeContext extends DelegatingTreeContext<TreeContextImpl> implements TreeContext {
    private final int length;
    private boolean hasContent = true;

    public OptionalTreeContext(TreeContextImpl context) {
        super(context);

        this.length = context.out.length();
    }

    @Override
    public TreeContext optional() {
        if (!hasContent) {
            return this;
        }

        return context.optional();
    }

    @Override
    public TreeContext newLine() {
        if (hasContent) {
            context.newLine();
        }

        return this;
    }

    @Override
    public TreeContext whitespace() {
        if (hasContent) {
            context.whitespace();
        }

        return this;
    }

    @Override
    public TreeContext glue() {
        if (hasContent) {
            context.glue();
        }

        return this;
    }

    @Override
    public TreeContext append(String sequence) {
        if (hasContent) {
            context.append(sequence);
        }

        return this;
    }

    @Override
    public TreeContext appendInner() {
        if (hasContent) {
            int initialLength = context.out.length();

            context.appendInner();

            if (context.out.length() <= initialLength) {
                hasContent = false;

                context.out.setLength(length);
            }
        }

        return this;
    }
}
