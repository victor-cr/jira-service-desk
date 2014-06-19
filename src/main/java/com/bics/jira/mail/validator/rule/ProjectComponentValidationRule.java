package com.bics.jira.mail.validator.rule;

import com.atlassian.jira.bc.EntityNotFoundException;
import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.bc.project.component.ProjectComponentManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.bics.jira.mail.model.service.CreateOrCommentModel;
import com.bics.jira.mail.model.web.CreateOrCommentWebModel;
import org.apache.commons.lang.StringUtils;

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
        String componentName = webModel.getComponentName();

        if (StringUtils.isBlank(componentName)) {
            monitor.info("Project component name is not set.");
            return;
        }

        Project project = serviceModel.getProject();

        if (project == null) {
            monitor.info("Project is not set.");
            return;
        }

        ProjectComponent projectComponent = projectComponentManager.findByComponentName(project.getId(), componentName);

        assertError(projectComponent == null, "Project component %s is not applicable to project %s.", componentName, project.getName());

        serviceModel.setComponentName(componentName);
    }
}
