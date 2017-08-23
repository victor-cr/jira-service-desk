package com.bics.jira.mail.helper;

import com.atlassian.jira.bc.ServiceResult;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueFactory;
import com.atlassian.jira.issue.IssueFieldConstants;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.priority.Priority;
import com.atlassian.jira.issue.security.IssueSecurityLevelManager;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.issue.watchers.WatcherManager;
import com.atlassian.jira.ofbiz.OfBizDelegator;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.jira.web.FieldVisibilityManager;
import com.atlassian.jira.web.util.AttachmentException;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.WorkflowManager;
import com.bics.jira.mail.IssueHelper;
import com.bics.jira.mail.MailHelper;
import com.bics.jira.mail.UserHelper;
import com.bics.jira.mail.model.mail.Attachment;
import com.bics.jira.mail.model.mail.Body;
import com.bics.jira.mail.model.mail.MessageAdapter;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.StepDescriptor;

import javax.annotation.Nullable;
import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 04.02.13 21:38
 */
public class IssueHelperImpl implements IssueHelper {
    private final JiraAuthenticationContext jiraAuthenticationContext;
    private final IssueService issueService;
    private final IssueFactory issueFactory;
    private final IssueManager issueManager;
    private final AttachmentManager attachmentManager;
    private final WorkflowManager workflowManager;
    private final WatcherManager watcherManager;
    private final CommentManager commentManager;
    private final IssueSecurityLevelManager issueSecurityLevelManager;
    private final CustomFieldManager customFieldManager;
    private final ConstantsManager constantsManager;
    private final MailHelper mailHelper;
    private final FieldVisibilityManager fieldVisibilityManager;
    private final UserHelper userHelper;
    private final OfBizDelegator ofBizDelegator;


    public IssueHelperImpl(JiraAuthenticationContext jiraAuthenticationContext, IssueService issueService, IssueFactory issueFactory, IssueManager issueManager, AttachmentManager attachmentManager, WorkflowManager workflowManager, WatcherManager watcherManager, CommentManager commentManager, IssueSecurityLevelManager issueSecurityLevelManager, CustomFieldManager customFieldManager, ConstantsManager constantsManager, MailHelper mailHelper, FieldVisibilityManager fieldVisibilityManager, UserHelper userHelper, OfBizDelegator ofBizDelegator) {
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.issueService = issueService;
        this.issueFactory = issueFactory;
        this.issueManager = issueManager;
        this.attachmentManager = attachmentManager;
        this.workflowManager = workflowManager;
        this.watcherManager = watcherManager;
        this.commentManager = commentManager;
        this.issueSecurityLevelManager = issueSecurityLevelManager;
        this.customFieldManager = customFieldManager;
        this.constantsManager = constantsManager;
        this.mailHelper = mailHelper;
        this.fieldVisibilityManager = fieldVisibilityManager;
        this.userHelper = userHelper;
        this.ofBizDelegator = ofBizDelegator;
    }

    @Override
    public MutableIssue create(ApplicationUser author, ApplicationUser assignee, Project project, IssueType issueType, ProjectComponent component, MessageAdapter message, Collection<ApplicationUser> watchers, MessageHandlerErrorCollector monitor) throws MessagingException, CreateException, AttachmentException {
        monitor.info("Creating new issue for an author: " + author.getName());

        Long levelId = issueSecurityLevelManager.getDefaultSecurityLevel(project);

        /*GenericValue value = ofBizDelegator.makeValue("Issue");

        MutableIssue issue = issueFactory.getIssue(value);
        */
        MutableIssue issue = issueFactory.getIssue();

        issue.setProjectObject(project);
        issue.setSummary(message.getSubject());
        issue.setIssueTypeId(issueType.getId());
        issue.setReporter(author);

        if (assignee != null) {
            issue.setAssignee(assignee);
        }

        if (levelId != null) {
            issue.setSecurityLevelId(levelId);
        }

        if (component != null) {
            issue.setComponent(Collections.singleton(component));
        }

        if (isVisibleField(project, IssueFieldConstants.PRIORITY, issueType)) {
            setPriority(issue, message);
        }

        Body body = mailHelper.extract(message, false);
        String bodyText = getBodyText(body);

        issue.setDescription(bodyText);

        for (CustomField customField : customFieldManager.getCustomFieldObjects(issue)) {
            issue.setCustomFieldValue(customField, customField.getDefaultValue(issue));
        }

        Issue issueObject = issueManager.createIssueObject(author, issue);

        attach(issue, author, message.getAttachments(), body.getUsed(), monitor);

        if (userHelper.canManageWatchList(author, project)) {
            watch(issue, watchers);
        } else {
            monitor.warning("ApplicationUser " + author.getName() + " cannot manage watch list in the project " + project.getKey() + ". Ignoring.");
        }

        monitor.info("New issue " + issueObject.getKey() + " has been successfully created");

        return issueManager.getIssueObject(issueObject.getId());
    }

    @Override
    public void comment(MutableIssue issue, Map<Status, String> transitions, MessageAdapter message, Collection<ApplicationUser> watchers, boolean stripQuotes, MessageHandlerErrorCollector monitor) throws MessagingException, CreateException, AttachmentException {
        Project project = issue.getProjectObject();
        ApplicationUser author = jiraAuthenticationContext.getLoggedInUser();

        if (userHelper.canManageWatchList(author, project)) {
            watch(issue, watchers);
        } else {
            monitor.warning("ApplicationUser " + author.getName() + " cannot manage watch list in the project " + project.getKey() + ". Ignoring.");
        }

        Body body = mailHelper.extract(message, stripQuotes);
        String bodyText = getBodyText(body);

        if (!transit(issue, transitions, bodyText, monitor)) {
            commentManager.create(issue, author.getName(), bodyText, true);
        }

        attach(issue, author, message.getAttachments(), body.getUsed(), monitor);
    }

    private void attach(MutableIssue issue, ApplicationUser author, Collection<Attachment> attachments, Collection<Attachment> used, MessageHandlerErrorCollector monitor) throws AttachmentException {
        List<com.atlassian.jira.issue.attachment.Attachment> existentAttachments = attachmentManager.getAttachments(issue);
        Project project = issue.getProjectObject();

        if (!attachmentManager.attachmentsEnabled() || !userHelper.canCreateAttachment(author, project)) {
            monitor.warning("ApplicationUser " + author.getName() + " cannot create attachments in the project " + project.getKey() + ". Ignoring.");
            return;
        }

        for (final Attachment attachment : attachments) {
            boolean required = used.contains(attachment) && !Iterables.any(existentAttachments, new Predicate<com.atlassian.jira.issue.attachment.Attachment>() {
                @Override
                public boolean apply(@Nullable com.atlassian.jira.issue.attachment.Attachment input) {
                    return input != null &&
                            attachment.getFileName().equals(input.getFilename()) &&
                            attachment.getContentType().match(input.getMimetype()) &&
                            attachment.getSize() == input.getFilesize();
                }
            });

            if (required) {
                attachmentManager.createAttachment(attachment.getStoredFile(), attachment.getFileName(), attachment.getContentType().toString(), author, issue);
            }

            attachment.getStoredFile().delete();
        }
    }

    private void watch(Issue issue, Collection<ApplicationUser> watchers) {
        for (ApplicationUser user : watchers) {
            if (!watcherManager.isWatching(user, issue)) {
                watcherManager.startWatching(user, issue);
            }
        }
    }

    private boolean transit(MutableIssue issue, Map<Status, String> transitions, String comment, MessageHandlerErrorCollector monitor) throws CreateException {
        ApplicationUser author = jiraAuthenticationContext.getLoggedInUser();
        ActionDescriptor action = lookupAction(issue, transitions, monitor);

        if (action != null) {
            IssueInputParameters params = issueService.newIssueInputParameters();

            params.setComment(comment);
            params.setAssigneeId(issue.getAssigneeId());
            params.setApplyDefaultValuesWhenParameterNotProvided(true);

            IssueService.TransitionValidationResult validationResult = issueService.validateTransition(author, issue.getId(), action.getId(), params);

            verifyResult(validationResult, monitor);

            IssueService.IssueResult result = issueService.transition(author, validationResult);

            verifyResult(result, monitor);

            issue.setStatusObject(result.getIssue().getStatusObject());
            issue.setResolutionObject(result.getIssue().getResolutionObject());
        }

        return action != null;
    }

    private ActionDescriptor lookupAction(Issue issue, Map<Status, String> transitions, MessageHandlerErrorCollector monitor) {
        if (transitions == null || transitions.isEmpty()) {
            return null;
        }

        Status status = issue.getStatusObject();

        String required = transitions.get(status);

        if (required == null) {
            return null;
        }

        JiraWorkflow workflow = workflowManager.getWorkflow(issue);

        if (workflow == null) {
            monitor.warning("The issue " + issue.getKey() + " does not have assigned workflow.");
            return null;
        }

        StepDescriptor step = workflow.getLinkedStep(status);

        if (step == null) {
            return null;
        }

        List<ActionDescriptor> actions = step.getActions();

        if (actions == null) {
            return null;
        }

        for (ActionDescriptor action : actions) {
            if (required.equalsIgnoreCase(action.getName())) {
                return action;
            }
        }

        return null;
    }

    private static void verifyResult(ServiceResult result, MessageHandlerErrorCollector monitor) throws CreateException {
        if (!result.isValid()) {
            ErrorCollection errors = result.getErrorCollection();

            for (String error : errors.getErrorMessages()) {
                monitor.error(error);
            }

            throw new CreateException("Cannot transit issue from one state to another");
        }
    }

    private void setPriority(MutableIssue issue, MessageAdapter message) {
        List<Priority> priorities = new ArrayList<Priority>(constantsManager.getPriorityObjects());

        Collections.sort(priorities);

        int index = message.getPriority(priorities.size());

        issue.setPriorityObject(priorities.get(index));
    }

    private boolean isVisibleField(Project project, String fieldName, IssueType issueType) {
        return !fieldVisibilityManager.isFieldHiddenInAllSchemes(project.getId(), fieldName, Collections.singletonList(issueType.getId()));
    }

    private String getBodyText(Body body) {
        Collection<Attachment> attachments = body.getUsed();

        if (attachments == null) {
            return body.getBody();
        }

        attachments = Collections2.filter(attachments, e -> e != null && !e.isInline());

        if (attachments.isEmpty()) {
            return body.getBody();
        }

        StringBuilder out = new StringBuilder("*Attached:* ");

        for (Attachment attachment : attachments) {
            if (!attachment.isInline()) {
                out.append("\\[[^").append(attachment.getFileName()).append("]\\] ");
            }
        }

        return out.append("\n\n").append(body.getBody()).toString();
    }
}
