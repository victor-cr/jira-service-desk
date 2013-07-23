package com.bics.jira.mail.validator.rule;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.bics.jira.mail.UserHelper;
import com.bics.jira.mail.model.service.CommentOnlyModel;
import com.bics.jira.mail.model.web.CommentOnlyWebModel;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 19/07/13 12:25
 */
public class CommentReporterValidationRule extends ValidationRule<CommentOnlyModel, CommentOnlyWebModel> {
    protected final UserHelper userHelper;

    public CommentReporterValidationRule(MessageHandlerErrorCollector monitor, UserHelper userHelper) {
        super(monitor);
        this.userHelper = userHelper;
    }

    @Override
    public void validate(CommentOnlyWebModel webModel, CommentOnlyModel serviceModel) {
        String reporterUsername = webModel.getReporterUsername();

        if (reporterUsername == null) {
            monitor.info("Default reporter user is not set.");
            return;
        }

        User user = userHelper.find(reporterUsername);

        assertError(user == null, "Default reporter user %s was not found.", reporterUsername);

        serviceModel.setReporterUser(user);
    }
}
