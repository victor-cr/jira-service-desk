package com.bics.jira.mail.validator;

import com.atlassian.jira.bc.ValidationErrorsException;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.bics.jira.mail.ModelValidator;
import com.bics.jira.mail.model.service.ServiceDeskModel;
import com.bics.jira.mail.model.web.ServiceDeskWebModel;
import com.bics.jira.mail.validator.rule.ValidationRule;

import java.util.Collection;
import java.util.Map;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 03.02.13 12:57
 */
public abstract class ServiceDeskModelValidator<M extends ServiceDeskModel, W extends ServiceDeskWebModel> implements ModelValidator<M> {
    protected abstract W convertWebModel(Map<String, String> params);

    protected abstract Collection<ValidationRule<M, W>> createRuleSet(MessageHandlerErrorCollector monitor);

    @Override
    public boolean populateHandlerModel(M model, Map<String, String> params, MessageHandlerErrorCollector monitor) {
        W webModel = convertWebModel(params);

        Collection<ValidationRule<M, W>> rules = createRuleSet(monitor);

        try {
            for (ValidationRule<M, W> rule : rules) {
                rule.validate(webModel, model);
            }

            return true;
        } catch (ValidationErrorsException e) {
            return false;
        } catch (Exception e) {
            monitor.error(e.getMessage(), e);
            return false;
        }
    }
}