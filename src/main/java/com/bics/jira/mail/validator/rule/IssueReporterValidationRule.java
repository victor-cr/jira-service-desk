package com.bics.jira.mail.validator.rule;

import com.atlassian.jira.project.Project;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.atlassian.jira.user.ApplicationUser;
import com.bics.jira.mail.UserHelper;
import com.bics.jira.mail.model.service.CreateOrCommentModel;
import com.bics.jira.mail.model.web.CreateOrCommentWebModel;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 19/07/13 12:25
 */
public class IssueReporterValidationRule extends ValidationRule<CreateOrCommentModel, CreateOrCommentWebModel> {
    protected final UserHelper userHelper;

    public IssueReporterValidationRule(MessageHandlerErrorCollector monitor, UserHelper userHelper) {
        super(monitor);
        this.userHelper = userHelper;
    }

    @Override
    public void validate(CreateOrCommentWebModel webModel, CreateOrCommentModel serviceModel) {
        String reporterUsername = webModel.getReporterUsername();

        if (reporterUsername == null) {
            monitor.info("Default reporter user is not set.");
            return;
        }

        ApplicationUser user = userHelper.find(reporterUsername);

        assertError(user == null, "Default reporter user %s was not found.", reporterUsername);

        Project project = serviceModel.getProject();

        if (user != null && project != null) {
            assertError(!userHelper.canCreateIssue(user, project), "Default reporter user %s has no rights to create an issue in the project %s.", user.getName(), project.getName());
        }

        serviceModel.setReporterUser(user);
    }
}
