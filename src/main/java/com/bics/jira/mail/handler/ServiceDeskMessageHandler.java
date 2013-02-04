package com.bics.jira.mail.handler;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.service.util.handler.MessageHandler;
import com.atlassian.jira.service.util.handler.MessageHandlerContext;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.atlassian.jira.service.util.handler.MessageHandlerExecutionMonitor;
import com.atlassian.jira.service.util.handler.MessageUserProcessor;
import com.atlassian.mail.MailUtils;
import com.bics.jira.mail.IssueBuilder;
import com.bics.jira.mail.IssueLocator;
import com.bics.jira.mail.ModelValidator;
import com.bics.jira.mail.model.HandlerModel;
import com.bics.jira.mail.model.MessageAdapter;
import org.apache.commons.lang.StringUtils;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.util.Map;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 03.02.13 12:29
 */
public class ServiceDeskMessageHandler implements MessageHandler {
    private final ModelValidator modelValidator;
    private final IssueLocator issueLocator;
    private final IssueBuilder issueBuilder;
    private final ProjectManager projectManager;
    private final FieldManager fieldManager;
    private final MessageUserProcessor messageUserProcessor;

    private final HandlerModel model = new HandlerModel();
    private Project project;
    private CustomField customField;

    public ServiceDeskMessageHandler(ModelValidator modelValidator, IssueLocator issueLocator, IssueBuilder issueBuilder, ProjectManager projectManager, FieldManager fieldManager, MessageUserProcessor messageUserProcessor) {
        this.modelValidator = modelValidator;
        this.issueLocator = issueLocator;
        this.issueBuilder = issueBuilder;
        this.projectManager = projectManager;
        this.fieldManager = fieldManager;
        this.messageUserProcessor = messageUserProcessor;
    }

    @Override
    public void init(Map<String, String> params, MessageHandlerErrorCollector monitor) {
        model.fromServiceParams(params);

        if (modelValidator.validateModel(model, monitor)) {
            monitor.warning("HandlerModel did not pass the validation. Emergency exit.");
            return;
        }

        project = projectManager.getProjectObjByKey(model.getProjectKey());
        customField = fieldManager.getCustomField(model.getMailIdField());
    }

    @Override
    public boolean handleMessage(Message message, MessageHandlerContext context) throws MessagingException {
        MessageAdapter adapter = new MessageAdapter(message);

        User sender = adapter.getReporter();

        MessageHandlerExecutionMonitor monitor = context.getMonitor();

        if (sender == null) {
            monitor.error("Message sender(s) '" + StringUtils.join(MailUtils.getSenders(message), ",")
                    + "' do not have corresponding users in JIRA. Message will be ignored");
            return false;
        }

        Issue issue = issueLocator.find(project, customField, adapter, monitor);

        String body = MailUtils.getBody(message);

        if (issue != null) {
            context.createComment(issue, sender, body, false);
        } else {
            try {
                issue = issueBuilder.build(project, customField, adapter, monitor);

                context.createIssue(sender, issue);
            } catch (CreateException e) {
                throw new MessagingException(e.getMessage(), e);
            }
        }

        return true;
    }
}