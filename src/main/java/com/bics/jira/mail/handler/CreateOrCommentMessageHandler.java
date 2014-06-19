package com.bics.jira.mail.handler;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.bc.project.component.ProjectComponentManager;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.exception.PermissionException;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.atlassian.jira.util.Predicate;
import com.atlassian.jira.web.util.AttachmentException;
import com.bics.jira.mail.CreateOrCommentModelValidator;
import com.bics.jira.mail.IssueHelper;
import com.bics.jira.mail.IssueLookupHelper;
import com.bics.jira.mail.UserHelper;
import com.bics.jira.mail.model.mail.MessageAdapter;
import com.bics.jira.mail.model.service.CreateOrCommentModel;
import org.apache.commons.lang.StringUtils;

import javax.mail.MessagingException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 03.02.13 12:29
 */
public class CreateOrCommentMessageHandler extends ServiceDeskMessageHandler<CreateOrCommentModel> {
    private final ProjectComponentManager projectComponentManager;

    public CreateOrCommentMessageHandler(JiraAuthenticationContext jiraAuthenticationContext, AttachmentManager attachmentManager, CreateOrCommentModelValidator modelValidator, IssueHelper issueHelper, UserHelper userHelper, IssueLookupHelper issueLookupHelper, ProjectComponentManager projectComponentManager) {
        super(jiraAuthenticationContext, attachmentManager, modelValidator, issueHelper, userHelper, issueLookupHelper);

        this.projectComponentManager = projectComponentManager;
    }

    @Override
    protected CreateOrCommentModel createModel() {
        return new CreateOrCommentModel();
    }

    @Override
    protected Predicate<User> searchPredicate(MessageAdapter adapter, MessageHandlerErrorCollector monitor) {
        return new Predicate<User>() {
            @Override
            public boolean evaluate(User user) {
                return userHelper.canCreateIssue(user, model.getProject());
            }
        };
    }

    @Override
    protected MutableIssue findIssue(MessageAdapter adapter, MessageHandlerErrorCollector monitor) {
        Project project = model.getProject();

        return issueLookupHelper.lookupBySubject(project, adapter.getSubject(), model.getResolvedBefore(), monitor);
    }

    @Override
    protected User chooseAssignee(Collection<User> users, String subject) {
        if (users != null && !users.isEmpty() && model.isCcAssignee()) {
            for (User user : users) {
                if (userHelper.canAssignTo(user, model.getProject())) {
                    return user;
                }
            }
        }

        ProjectComponent component = getProjectComponent(subject);

        return userHelper.getDefaultAssignee(model.getProject(), component);
    }

    @Override
    protected MutableIssue create(User author, User assignee, MessageAdapter adapter, Collection<User> watchers, MessageHandlerErrorCollector monitor) throws PermissionException, MessagingException, CreateException, AttachmentException {
        Project project = model.getProject();

        if (!userHelper.canCreateIssue(author, project)) {
            throw new PermissionException("User " + author.getName() + " cannot create issues in the project " + project.getKey() + ".");
        }

        ProjectComponent component = getProjectComponent(adapter.getSubject());

        return issueHelper.create(author, assignee, project, model.getIssueType(), component, adapter, watchers, monitor);
    }

    private ProjectComponent getProjectComponent(String subject) {
        ProjectComponent component = getProjectComponentByName(model.getComponentName());

        if (component == null && model.getComponentRegex() != null && StringUtils.isNotBlank(subject)) {
            Pattern pattern = model.getComponentRegex();
            Matcher matcher = pattern.matcher(subject);

            if (matcher.find()) {
                int count = matcher.groupCount();

                for (int i = 1; component == null && i <= count; i++) {
                    String componentName = matcher.group(i);

                    component = getProjectComponentByName(componentName);
                }
            }
        }

        return component;
    }

    private ProjectComponent getProjectComponentByName(String componentName) {
        if (StringUtils.isBlank(componentName)) {
            return null;
        }

        return projectComponentManager.findByComponentName(model.getProject().getId(), componentName);

    }
}