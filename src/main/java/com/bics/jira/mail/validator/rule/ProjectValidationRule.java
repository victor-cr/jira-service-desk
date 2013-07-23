package com.bics.jira.mail.validator.rule;

import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.bics.jira.mail.model.service.CreateOrCommentModel;
import com.bics.jira.mail.model.web.CreateOrCommentWebModel;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 19/07/13 12:25
 */
public class ProjectValidationRule extends ValidationRule<CreateOrCommentModel, CreateOrCommentWebModel> {
    private final ProjectManager projectManager;

    public ProjectValidationRule(MessageHandlerErrorCollector monitor, ProjectManager projectManager) {
        super(monitor);
        this.projectManager = projectManager;
    }

    @Override
    public void validate(CreateOrCommentWebModel webModel, CreateOrCommentModel serviceModel) {
        String projectKey = webModel.getProjectKey();

        Project project = projectManager.getProjectObjByKey(projectKey);

        assertError(project == null, "Project with key %s was not found.", projectKey);

        serviceModel.setProject(project);
    }
}
