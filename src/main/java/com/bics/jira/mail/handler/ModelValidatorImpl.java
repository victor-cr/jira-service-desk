package com.bics.jira.mail.handler;

import com.atlassian.jira.bc.project.component.ProjectComponentManager;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.project.version.VersionManager;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.bics.jira.mail.ModelValidator;
import com.bics.jira.mail.model.HandlerModel;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 03.02.13 12:57
 */
public class ModelValidatorImpl implements ModelValidator {
    private final ProjectManager projectManager;
    private final IssueTypeManager issueTypeManager;
    private final ProjectComponentManager projectComponentManager;
    private final VersionManager versionManager;
    private final FieldManager fieldManager;

    public ModelValidatorImpl(ProjectManager projectManager, IssueTypeManager issueTypeManager, ProjectComponentManager projectComponentManager, VersionManager versionManager, FieldManager fieldManager) {
        this.projectManager = projectManager;
        this.issueTypeManager = issueTypeManager;
        this.projectComponentManager = projectComponentManager;
        this.versionManager = versionManager;
        this.fieldManager = fieldManager;
    }

    public boolean validateModel(HandlerModel model, MessageHandlerErrorCollector monitor) {
        boolean valid = true;
        //TODO: validation here

        Project project = projectManager.getProjectObjByKey(model.getProjectKey());

        if (project == null) {
            monitor.error("Project with key " + model.getProjectKey() + " was not found.");
            return false;
        }

        IssueType issueType = issueTypeManager.getIssueType(model.getIssueTypeKey());

        if (issueType == null) {
            monitor.error("Issue type with key " + model.getIssueTypeKey() + " was not found.");
            return false;
        }

        if (!project.getIssueTypes().contains(issueType)) {
            monitor.error("Issue type " + issueType.getName() + " is not applicable to project " + project.getName() + ".");
            return false;
        }

        if (StringUtils.isBlank(model.getMailIdField())) {
            monitor.info("Custom field for storage extra mail information is not set");
        } else {
            CustomField customField = fieldManager.getCustomField(model.getMailIdField());

            if (customField == null) {
                monitor.error("Custom field " + model.getMailIdField() + " is not found");
                return false;
            }

            if (!customField.isInScope(project, Collections.singletonList(issueType.getId()))) {
                monitor.error("Custom field " + customField.getFieldName() + " is not applicable to project " + project.getName() + " and issue type " + issueType.getName() + ".");
                return false;
            }
        }

        return valid;
    }
}