package com.bics.jira.mail.handler;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.EntityNotFoundException;
import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.bc.project.component.ProjectComponentManager;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.config.StatusManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.WorkflowManager;
import com.atlassian.jira.workflow.WorkflowSchemeManager;
import com.bics.jira.mail.ModelValidator;
import com.bics.jira.mail.model.HandlerModel;
import com.bics.jira.mail.model.ServiceModel;
import com.opensymphony.workflow.loader.ActionDescriptor;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Collection;
import java.util.Formatter;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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
    private final StatusManager statusManager;
    private final UserManager userManager;
    private final PermissionManager permissionManager;
    private final WorkflowManager workflowManager;
    private final WorkflowSchemeManager workflowSchemeManager;

    public ModelValidatorImpl(ProjectManager projectManager, IssueTypeManager issueTypeManager, ProjectComponentManager projectComponentManager, StatusManager statusManager, UserManager userManager, PermissionManager permissionManager, WorkflowManager workflowManager, WorkflowSchemeManager workflowSchemeManager) {
        this.projectManager = projectManager;
        this.issueTypeManager = issueTypeManager;
        this.projectComponentManager = projectComponentManager;
        this.statusManager = statusManager;
        this.userManager = userManager;
        this.permissionManager = permissionManager;
        this.workflowManager = workflowManager;
        this.workflowSchemeManager = workflowSchemeManager;
    }

    public boolean populateHandlerModel(HandlerModel handlerModel, ServiceModel serviceModel, MessageHandlerErrorCollector monitor) {
        Validator validator = new Validator(monitor);

        Project project = validator.validateProject(serviceModel.getProjectKey());

        IssueType issueType = validator.validateIssueType(serviceModel.getIssueTypeId(), project);
        ProjectComponent projectComponent = validator.validateProjectComponent(serviceModel.getComponentId(), project);
        int[] transitions = validator.validateStatusTransitions(serviceModel.getTransitions(), project, issueType);
        InternetAddress catchAddress = validator.validateCatchEmail(serviceModel.getCatchEmail());
        User reporterUser = validator.validateReporterUser(serviceModel.getReporterUsername(), project);
        Pattern splitRegex = validator.validateSplitRegex(serviceModel.getSplitRegex());

        validator.validateFlags(serviceModel.isCreateUsers(), serviceModel.isNotifyUsers(), serviceModel.isCcAssignee(), serviceModel.isCcWatcher(), serviceModel.isStripQuotes());

        if (!validator.success) {
            return false;
        }

        handlerModel.setProject(project);
        handlerModel.setIssueType(issueType);
        handlerModel.setProjectComponent(projectComponent);
        handlerModel.setTransitions(transitions);
        handlerModel.setCatchEmail(catchAddress);
        handlerModel.setReporterUser(reporterUser);
        handlerModel.setSplitRegex(splitRegex);
        handlerModel.setCreateUsers(serviceModel.isCreateUsers());
        handlerModel.setNotifyUsers(serviceModel.isNotifyUsers());
        handlerModel.setCcAssignee(serviceModel.isCcAssignee());
        handlerModel.setCcWatcher(serviceModel.isCcWatcher());
        handlerModel.setStripQuotes(serviceModel.isStripQuotes());

        return true;
    }

    private class Validator {
        private final MessageHandlerErrorCollector monitor;

        private boolean success = true;

        private Validator(MessageHandlerErrorCollector monitor) {
            this.monitor = monitor;
        }

        public Project validateProject(String projectKey) {
            Project project = projectManager.getProjectObjByKey(projectKey);

            assertError(project == null, "Project with key %s was not found.", projectKey);

            return project;
        }

        public IssueType validateIssueType(String issueTypeId, Project project) {
            IssueType issueType = issueTypeManager.getIssueType(issueTypeId);

            assertError(issueType == null, "Issue type id %s was not found.", issueTypeId);

            if (project != null && issueType != null) {
                assertError(!project.getIssueTypes().contains(issueType), "Issue type %s is not applicable to project %s.", issueType.getName(), project.getName());
            }

            return issueType;
        }

        public ProjectComponent validateProjectComponent(Long componentId, Project project) {
            if (componentId == null) {
                monitor.info("Project component is not set.");
                return null;
            }

            try {
                ProjectComponent projectComponent = projectComponentManager.find(componentId);

                if (project != null) {
                    assertError(project.getId().equals(projectComponent.getProjectId()), "Project component %s is not applicable to project %s.", projectComponent.getName(), project.getName());
                }

                return projectComponent;
            } catch (EntityNotFoundException e) {
                assertError(true, "Project component id %s was not found.", componentId);
                return null;
            }
        }

        public int[] validateStatusTransitions(String[] transitions, Project project, IssueType issueType) {
            if (transitions == null || transitions.length == 0) {
                monitor.info("Transitions are not set.");
                return null;
            }

            if (project == null || issueType == null) {
                return null;
            }

            JiraWorkflow workflow = workflowManager.getWorkflow(project.getId(), issueType.getId());

            if (workflow == null) {
                assertError(true, "The project " + project.getName() + " does not have a workflow for the issue type " + issueType.getName() + ".");
                return null;
            }

            int[] codes = new int[transitions.length];
            Collection<ActionDescriptor> actions = workflow.getAllActions();

            for (int i = 0; i < transitions.length; i++) {
                try {
                    int code = Integer.parseInt(transitions[i]);

                    for (ActionDescriptor action : actions) {
                        if (action.getId() == code || code == 0) {
                            codes[i] = code;
                            break;
                        }
                    }
                } catch (NumberFormatException e) {
                    monitor.warning("Parsing error: " + transitions[i] + ". Ignoring...", e);
                }

                assertError(codes[i] == 0, "The project " + project.getName() + " does not have a workflow action with id " + transitions[i] + " for the issue type " + issueType.getName() + ".");
            }

            return codes;
        }

        public InternetAddress validateCatchEmail(String catchEmail) {
            if (catchEmail == null) {
                monitor.info("Catch email is not set.");
                return null;
            }

            try {
                return new InternetAddress(catchEmail);
            } catch (AddressException e) {
                assertError(true, "Catch email %s is incorrect.", catchEmail);
                return null;
            }
        }

        public User validateReporterUser(String reporterUsername, Project project) {
            if (reporterUsername == null) {
                monitor.info("Default reporter user is not set.");
                return null;
            }

            User user = userManager.getUser(reporterUsername);

            assertError(user == null, "Default reporter user %s was not found.", reporterUsername);

            if (user != null && project != null) {
                assertError(!permissionManager.hasPermission(Permissions.CREATE_ISSUE, project, user), "Default reporter user %s has no rights to create an issue in the project %s.", user.getName(), project.getName());
            }

            return user;
        }

        public Pattern validateSplitRegex(String splitRegex) {
            if (splitRegex == null) {
                monitor.info("Split regular expression is not set.");
                return null;
            }

            try {
                return Pattern.compile(splitRegex);
            } catch (PatternSyntaxException e) {
                assertError(true, "Split regular expression has incorrect syntax s%.", splitRegex);
                return null;
            }
        }

        public void validateFlags(boolean createUsers, boolean notifyUsers, boolean ccAssignee, boolean ccWatcher, boolean stripQuotes) {
            assertError(notifyUsers && !createUsers, "Notify users flag does not make sense unless create user flag is set");
            assertError(createUsers && !userManager.hasWritableDirectory(), "Create users flag does not make sense unless writable user directory is configured.");
        }

        private void assertError(boolean condition, String errorPattern, Object... args) {
            if (condition) {
                monitor.error(new Formatter().format(errorPattern, args).toString());
                success = false;
            }
        }
    }
}