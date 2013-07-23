package com.bics.jira.mail;

import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.bics.jira.mail.model.service.ServiceDeskModel;

import java.util.Map;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 04.02.13 21:57
 */
public interface ModelValidator<M extends ServiceDeskModel> {
    boolean populateHandlerModel(M model, Map<String, String> params, MessageHandlerErrorCollector monitor);
}
