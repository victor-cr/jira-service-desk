package com.bics.jira.mail.model;

import com.atlassian.crowd.embedded.api.User;
import org.apache.log4j.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 04.02.13 22:02
 */
public class MessageAdapter {
    private static final Logger LOG = Logger.getLogger(MessageAdapter.class);
    private static final String KEY_JIRA_FINGER_PRINT = "X-JIRA-FingerPrint";

    private final Message message;
    private final List<Part> parts;

    public MessageAdapter(Message message) throws MessagingException {
        this.message = message;

        try {
            parts = flatten(message.getContent());
        } catch (IOException e) {
            throw new MessagingException("Cannot read message content", e);
        }
    }

    public User getReporter(User defaultReporter) {
        return defaultReporter;
    }

    public boolean isLoopMail(String instanceFingerPrint) {
        try {
            String headerArray[] = message.getHeader(KEY_JIRA_FINGER_PRINT);

            return headerArray == null || Arrays.asList(headerArray).contains(instanceFingerPrint);
        } catch (MessagingException e) {
            LOG.warn(e.getMessage(), e);
            return false;
        }

    }

    public String getSubject() throws MessagingException {
        return message.getSubject();
    }

/*
    public List<String> getBlocks() throws MessagingException {
        try {
            Object content = message.getContent();

            return message.getSubject();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
*/

    private static List<Part> flatten(Object content) throws MessagingException { //TODO: not correct. fix it
        if (content instanceof Multipart) {
            Multipart multipart = (Multipart) content;

            List<Part> parts = new LinkedList<Part>();
            int count = multipart.getCount();

            for (int i = 0; i < count; i++) {
                List<Part> children = flatten(multipart.getBodyPart(i));

                parts.addAll(children);
            }

            return parts;
        }

        if (content instanceof Part) {
            return Collections.singletonList((Part) content);
        }

        return null;
    }
}
