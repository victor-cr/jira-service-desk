package com.bics.jira.mail;

import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.bics.jira.mail.model.service.CreateOrCommentModel;

import java.util.Map;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 04.02.13 21:57
 */
public interface CreateOrCommentModelValidator extends ModelValidator<CreateOrCommentModel> {
    boolean populateHandlerModel(CreateOrCommentModel model, Map<String, String> params, MessageHandlerErrorCollector monitor);
}
