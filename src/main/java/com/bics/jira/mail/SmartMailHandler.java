package com.bics.jira.mail;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.service.util.handler.MessageHandler;
import com.atlassian.jira.service.util.handler.MessageHandlerContext;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.atlassian.jira.service.util.handler.MessageUserProcessor;
import com.atlassian.mail.MailUtils;
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
public class SmartMailHandler implements MessageHandler {
    public static final String KEY_ISSUE_KEY = "issueKey";

    private final IssueKeyValidator issueKeyValidator;
    private final MessageUserProcessor messageUserProcessor;
    private String issueKey;

    public SmartMailHandler(MessageUserProcessor messageUserProcessor, IssueKeyValidator issueKeyValidator) {
        this.messageUserProcessor = messageUserProcessor;
        this.issueKeyValidator = issueKeyValidator;
    }

    @Override
    public void init(Map<String, String> params, MessageHandlerErrorCollector monitor) {
        issueKey = params.get(KEY_ISSUE_KEY);

        if (StringUtils.isBlank(issueKey)) {
            monitor.error("Issue key has not been specified ('" + KEY_ISSUE_KEY + "' parameter). This handler will not work correctly.");
        }

        issueKeyValidator.validateIssue(issueKey, monitor);
    }

    @Override
    public boolean handleMessage(Message message, MessageHandlerContext context) throws MessagingException {
        Issue issue = issueKeyValidator.validateIssue(issueKey, context.getMonitor());

        if (issue == null) {
            return false;
        }

        User sender = messageUserProcessor.getAuthorFromSender(message);

        if (sender == null) {
            context.getMonitor().error("Message sender(s) '" + StringUtils.join(MailUtils.getSenders(message), ",")
                    + "' do not have corresponding users in JIRA. Message will be ignored");
            return false;
        }

        String body = MailUtils.getBody(message);
        StringBuilder commentBody = new StringBuilder(message.getSubject());

        if (body != null) {
            commentBody.append("\n").append(StringUtils.abbreviate(body, 100000));
        }

        context.createComment(issue, sender, commentBody.toString(), false);

        return true;
    }
}