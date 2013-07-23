package com.bics.jira.mail.validator.rule;

import com.atlassian.jira.bc.EntityNotFoundException;
import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.bc.project.component.ProjectComponentManager;
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
public class ProjectComponentValidationRule extends ValidationRule<CreateOrCommentModel, CreateOrCommentWebModel> {
    private final ProjectComponentManager projectComponentManager;

    public ProjectComponentValidationRule(MessageHandlerErrorCollector monitor, ProjectComponentManager projectComponentManager) {
        super(monitor);
        this.projectComponentManager = projectComponentManager;
    }

    @Override
    public void validate(CreateOrCommentWebModel webModel, CreateOrCommentModel serviceModel) {
        Long componentId = webModel.getComponentId();

        if (componentId == null) {
            monitor.info("Project component is not set.");
            return;
        }

        Project project = serviceModel.getProject();

        try {
            ProjectComponent projectComponent = projectComponentManager.find(componentId);

            if (project != null) {
                assertError(project.getId().equals(projectComponent.getProjectId()), "Project component %s is not applicable to project %s.", projectComponent.getName(), project.getName());
            }

            serviceModel.setProjectComponent(projectComponent);
        } catch (EntityNotFoundException e) {
            assertError(true, "Project component id %s was not found.", componentId);
        }
    }
}
