package com.bics.jira.mail.handler;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.Issue;
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
import com.bics.jira.mail.model.ServiceModel;
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
    private final MessageUserProcessor messageUserProcessor;

    private final HandlerModel model = new HandlerModel();
    private boolean valid;

    public ServiceDeskMessageHandler(ModelValidator modelValidator, IssueLocator issueLocator, IssueBuilder issueBuilder, MessageUserProcessor messageUserProcessor) {
        this.modelValidator = modelValidator;
        this.issueLocator = issueLocator;
        this.issueBuilder = issueBuilder;
        this.messageUserProcessor = messageUserProcessor;
    }

    @Override
    public void init(Map<String, String> params, MessageHandlerErrorCollector monitor) {
        ServiceModel serviceModel = new ServiceModel().fromServiceParams(params);

        valid = modelValidator.populateHandlerModel(model, serviceModel, monitor);

        if (!valid) {
            monitor.warning("ServiceModel did not pass the validation. Emergency exit.");
        }
    }

    @Override
    public boolean handleMessage(Message message, MessageHandlerContext context) throws MessagingException {
        if (!valid) {
            return false;
        }

        MessageAdapter adapter = new MessageAdapter(message);

        User sender = adapter.getReporter(model.getReporterUser());

        MessageHandlerExecutionMonitor monitor = context.getMonitor();

        if (sender == null) {
            monitor.error("Message sender(s) '" + StringUtils.join(MailUtils.getSenders(message), ",")
                    + "' do not have corresponding users in JIRA. Message will be ignored");
            return false;
        }

        Issue issue = issueLocator.find(model.getProject(), adapter, monitor);

        String body = MailUtils.getBody(message);

        if (issue != null) {
            context.createComment(issue, sender, body, false);
        } else {
            try {
                issue = issueBuilder.build(model.getProject(), adapter, monitor);

                context.createIssue(sender, issue);
            } catch (CreateException e) {
                throw new MessagingException(e.getMessage(), e);
            }
        }

        return true;
    }
}