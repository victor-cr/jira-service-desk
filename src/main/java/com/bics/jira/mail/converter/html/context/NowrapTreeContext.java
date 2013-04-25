package com.bics.jira.mail.converter.html.context;

import com.bics.jira.mail.converter.html.TreeContext;
import org.apache.commons.lang.StringUtils;

/**
* Java Doc here
*
* @author Victor Polischuk
* @since 17/04/13 13:54
*/
public class NowrapTreeContext extends DelegatingTreeContext<TreeContextImpl> implements TreeContext {
    private final int position;

    public NowrapTreeContext(TreeContextImpl context) {
        super(context);

        this.position = context.out.length();
    }

    @Override
    public TreeContext newLine() {
        return this;
    }

    @Override
    public TreeContext append(String sequence) {
        sequence = StringUtils.replaceChars(sequence, '\n', ' ');
        sequence = StringUtils.replaceChars(sequence, '\r', ' ');

        super.append(sequence);

        return this;
    }

    @Override
    public TreeContext appendInner() {
        context.appendInner();

        for (int i = position; i < context.out.length(); i++) {
            char ch = context.out.charAt(i);

            if (ch == '\n' || ch == '\r') {
                context.out.setCharAt(i, ' ');
            }
        }

        return this;
    }
}
