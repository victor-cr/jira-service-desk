package com.bics.jira.mail.model;

import com.atlassian.crowd.embedded.api.User;

import javax.mail.Message;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 04.02.13 22:02
 */
public class MessageAdapter {
    private final Message message;

    public MessageAdapter(Message message) {
        this.message = message;
    }

    public User getReporter() {
        return null;
    }
}
