package com.bics.jira.mail.validator.rule;

import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.bics.jira.mail.UserHelper;
import com.bics.jira.mail.model.service.ServiceDeskModel;
import com.bics.jira.mail.model.web.ServiceDeskWebModel;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 19/07/13 12:25
 */
public class UserAttributeValidationRule<M extends ServiceDeskModel, W extends ServiceDeskWebModel> extends ValidationRule<M, W> {
    private final UserHelper userHelper;

    public UserAttributeValidationRule(MessageHandlerErrorCollector monitor, UserHelper userHelper) {
        super(monitor);

        this.userHelper = userHelper;
    }

    @Override
    public void validate(W webModel, M serviceModel) {
        assertError(webModel.isNotifyUsers() && !webModel.isCreateUsers(), "Notify users flag does not make sense unless create user flag is set");
        assertError(webModel.isCreateUsers() && !userHelper.canAddUsers(), "Create users flag does not make sense unless writable user directory is configured or number of already registered users increased.");

        serviceModel.setCreateUsers(webModel.isCreateUsers());
        serviceModel.setNotifyUsers(webModel.isNotifyUsers());
        serviceModel.setCcAssignee(webModel.isCcAssignee());
        serviceModel.setCcWatcher(webModel.isCcWatcher());
        serviceModel.setStripQuotes(webModel.isStripQuotes());
    }
}
