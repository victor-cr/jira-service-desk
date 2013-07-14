package com.bics.jira.mail.model;

import java.util.regex.Pattern;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 03.02.13 22:29
 */
public class CommentOnlyModel extends ServiceDeskModel {
    private Pattern subjectKeyPattern;

    public Pattern getSubjectKeyPattern() {
        return subjectKeyPattern;
    }

    public void setSubjectKeyPattern(Pattern subjectKeyPattern) {
        this.subjectKeyPattern = subjectKeyPattern;
    }
}
