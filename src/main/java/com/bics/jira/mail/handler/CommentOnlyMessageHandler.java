package com.bics.jira.mail.handler;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.exception.PermissionException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.service.util.handler.MessageHandler;
import com.atlassian.jira.service.util.handler.MessageHandlerContext;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.atlassian.jira.service.util.handler.MessageHandlerExecutionMonitor;
import com.atlassian.jira.util.Predicate;
import com.atlassian.jira.util.collect.CollectionUtil;
import com.atlassian.jira.web.util.AttachmentException;
import com.atlassian.mail.MailUtils;
import com.bics.jira.mail.IssueHelper;
import com.bics.jira.mail.IssueKeyHelper;
import com.bics.jira.mail.ModelValidator;
import com.bics.jira.mail.UserHelper;
import com.bics.jira.mail.model.CommentOnlyModel;
import com.bics.jira.mail.model.CreateOrCommentModel;
import com.bics.jira.mail.model.mail.MessageAdapter;
import org.apache.commons.lang.StringUtils;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 03.02.13 12:29
 */
public class CommentOnlyMessageHandler extends ServiceDeskMessageHandler<CommentOnlyModel> {
    private final IssueManager issueManager;
    private final IssueKeyHelper issueKeyHelper;

    public CommentOnlyMessageHandler(JiraAuthenticationContext jiraAuthenticationContext, ModelValidator modelValidator, IssueHelper issueHelper, UserHelper userHelper, IssueManager issueManager, IssueKeyHelper issueKeyHelper) {
        super(jiraAuthenticationContext, modelValidator, issueHelper, userHelper);
        this.issueManager = issueManager;
        this.issueKeyHelper = issueKeyHelper;
    }

    @Override
    protected CommentOnlyModel createModel() {
        return new CommentOnlyModel();
    }

    @Override
    protected Predicate<User> searchPredicate(final MessageAdapter adapter) {
        return new Predicate<User>() {
            @Override
            public boolean evaluate(User user) {
                String subject = adapter.getSubject();

                Collection<String> issueKeys = issueKeyHelper.findIssueKeys(subject);

                for (String issueKey : issueKeys) {
                    Issue issue = issueManager.getIssueObject(issueKey);

                    Project project = issue.getProjectObject();

                    if (userHelper.canCreateIssue(user, project)) {
                        return true;
                    }
                }

                return false;
            }
        };
    }
}