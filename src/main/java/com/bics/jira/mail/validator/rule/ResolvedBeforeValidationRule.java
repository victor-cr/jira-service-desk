package com.bics.jira.mail.validator.rule;

import com.atlassian.jira.config.StatusManager;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.bics.jira.mail.model.service.ServiceDeskModel;
import com.bics.jira.mail.model.web.ServiceDeskWebModel;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 19/07/13 12:25
 */
public class ResolvedBeforeValidationRule<M extends ServiceDeskModel, W extends ServiceDeskWebModel> extends ValidationRule<M, W> {
    public ResolvedBeforeValidationRule(MessageHandlerErrorCollector monitor) {
        super(monitor);
    }

    @Override
    public void validate(W webModel, M serviceModel) {
        Long resolvedBefore = webModel.getResolvedBefore();

        if (resolvedBefore == null || resolvedBefore == 0L) {
            monitor.info("Resolved interval is not set.");
            resolvedBefore = 0L;
        }

        assertError(resolvedBefore < 0L, "A value of resolved before cannot be negative.");

        serviceModel.setResolvedBefore(resolvedBefore);
    }
}
