package com.bics.jira.mail;

import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.bics.jira.mail.model.HandlerModel;
import com.bics.jira.mail.model.ServiceModel;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 04.02.13 21:57
 */
public interface ModelValidator {
    boolean populateHandlerModel(HandlerModel handlerModel, ServiceModel model, MessageHandlerErrorCollector monitor);
}
