package com.bics.jira.mail.model.web;

import java.util.Map;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 03.02.13 22:29
 */
public class CommentOnlyWebModel extends ServiceDeskWebModel {
    @Override
    public CommentOnlyWebModel fromServiceParams(Map<String, String> params) {
        super.fromServiceParams(params);

        return this;
    }
}
