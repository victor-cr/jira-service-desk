package com.bics.jira.mail.helper;

import com.bics.jira.mail.model.mail.Attachment;
import com.google.common.base.Predicate;

import javax.annotation.Nullable;

/**
* Java Doc here
*
* @author Victor Polischuk
* @since 11/11/13 11:52
*/
public class AttachmentPredicate implements Predicate<Attachment> {
    private final boolean inline;

    public AttachmentPredicate(boolean inline) {
        this.inline = inline;
    }

    @Override
    public boolean apply(@Nullable Attachment input) {
        return input != null && input.isInline() == inline;
    }
}
