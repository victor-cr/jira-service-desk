package com.bics.jira.mail.handler;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.event.user.UserEventType;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.exception.PermissionException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.service.util.handler.MessageHandler;
import com.atlassian.jira.service.util.handler.MessageHandlerContext;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.atlassian.jira.service.util.handler.MessageHandlerExecutionMonitor;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.mail.MailUtils;
import com.bics.jira.mail.IssueBuilder;
import com.bics.jira.mail.IssueLocator;
import com.bics.jira.mail.ModelValidator;
import com.bics.jira.mail.model.HandlerModel;
import com.bics.jira.mail.model.MessageAdapter;
import com.bics.jira.mail.model.ServiceModel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.util.Map;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 03.02.13 12:29
 */
public class ServiceDeskMessageHandler implements MessageHandler {
    private static final Logger LOG = Logger.getLogger(ServiceDeskMessageHandler.class);

    private final ModelValidator modelValidator;
    private final IssueLocator issueLocator;
    private final IssueBuilder issueBuilder;
    private final UserManager userManager;

    private final HandlerModel model = new HandlerModel();
    private boolean valid;

    public ServiceDeskMessageHandler(ModelValidator modelValidator, IssueLocator issueLocator, IssueBuilder issueBuilder, UserManager userManager) {
        this.modelValidator = modelValidator;
        this.issueLocator = issueLocator;
        this.issueBuilder = issueBuilder;
        this.userManager = userManager;
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

        InternetAddress[] addresses = adapter.getFrom();

        User author = null;

        for (InternetAddress address : addresses) {
            author = userManager.getUser(address.getAddress());

            if (author == null) {
                author = model.getReporterUser();

                try {
                    author = context.createUser(address.getAddress(), address.getAddress(), address.getAddress(), address.getPersonal(), UserEventType.USER_CREATED);
                } catch (PermissionException e) {
                    LOG.error("Permission problem: ", e);
                } catch (CreateException e) {
                    LOG.error("Creation problem: ", e);
                }
            }
        }

        MessageHandlerExecutionMonitor monitor = context.getMonitor();

        if (author == null) {
            monitor.error("Message sender(s) '" + StringUtils.join(MailUtils.getSenders(message), ",")
                    + "' do not have corresponding users in JIRA. Message will be ignored");
            return false;
        }

        Issue issue = issueLocator.find(model, adapter, monitor);

        if (issue != null) {
            String body = adapter.getComments().get(0);

            context.createComment(issue, author, body, false);
        } else {
            issue = issueBuilder.build(model, adapter, monitor);

            try {
                context.createIssue(author, issue);
            } catch (CreateException e) {
                throw new MessagingException(e.getMessage(), e);
            }
        }

        return true;
    }
}