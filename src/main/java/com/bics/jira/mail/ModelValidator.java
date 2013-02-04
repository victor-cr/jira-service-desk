package com.bics.jira.mail;

import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.bics.jira.mail.model.HandlerModel;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 04.02.13 21:57
 */
public interface ModelValidator {
    boolean validateModel(HandlerModel model, MessageHandlerErrorCollector monitor);
}
