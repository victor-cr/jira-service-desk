package com.bics.jira.mail.validator.rule;

import com.atlassian.jira.bc.ValidationErrorsException;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.bics.jira.mail.model.service.ServiceDeskModel;
import com.bics.jira.mail.model.web.ServiceDeskWebModel;

import java.util.Formatter;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 19/07/13 12:21
 */
public abstract class ValidationRule<M extends ServiceDeskModel, W extends ServiceDeskWebModel> {
    protected final MessageHandlerErrorCollector monitor;

    public ValidationRule(MessageHandlerErrorCollector monitor) {
        this.monitor = monitor;
    }

    public abstract void validate(W webModel, M serviceModel);

    protected void assertError(boolean condition, String errorPattern, Object... args) {
        if (condition) {
            String message = new Formatter().format(errorPattern, args).toString();

            monitor.error(message);
            throw new ValidationErrorsException(message);
        }
    }
}
