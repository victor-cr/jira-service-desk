package com.bics.jira.mail.validator.rule;

import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.bics.jira.mail.model.service.CreateOrCommentModel;
import com.bics.jira.mail.model.web.CreateOrCommentWebModel;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 19/07/13 12:25
 */
public class IssueTypeValidationRule extends ValidationRule<CreateOrCommentModel, CreateOrCommentWebModel> {
    private final IssueTypeManager issueTypeManager;

    public IssueTypeValidationRule(MessageHandlerErrorCollector monitor, IssueTypeManager issueTypeManager) {
        super(monitor);
        this.issueTypeManager = issueTypeManager;
    }

    @Override
    public void validate(CreateOrCommentWebModel webModel, CreateOrCommentModel serviceModel) {
        String issueTypeId = webModel.getIssueTypeId();
        Project project = serviceModel.getProject();

        IssueType issueType = issueTypeManager.getIssueType(issueTypeId);

        assertError(issueType == null, "Issue type id %s was not found.", issueTypeId);

        if (project != null && issueType != null) {
            assertError(!project.getIssueTypes().contains(issueType), "Issue type %s is not applicable to project %s.", issueType.getName(), project.getName());
        }

        serviceModel.setIssueType(issueType);
    }
}
