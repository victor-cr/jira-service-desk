package com.bics.jira.mail.handler;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.util.Predicate;
import com.bics.jira.mail.IssueHelper;
import com.bics.jira.mail.ModelValidator;
import com.bics.jira.mail.UserHelper;
import com.bics.jira.mail.model.CreateOrCommentModel;
import com.bics.jira.mail.model.mail.MessageAdapter;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 03.02.13 12:29
 */
public class CreateOrCommentMessageHandler extends ServiceDeskMessageHandler<CreateOrCommentModel> {
    public CreateOrCommentMessageHandler(JiraAuthenticationContext jiraAuthenticationContext, ModelValidator modelValidator, IssueHelper issueHelper, UserHelper userHelper) {
        super(jiraAuthenticationContext, modelValidator, issueHelper, userHelper);
    }

    @Override
    protected CreateOrCommentModel createModel() {
        return new CreateOrCommentModel();
    }

    @Override
    protected Predicate<User> searchPredicate(MessageAdapter adapter) {
        return new Predicate<User>() {
            @Override
            public boolean evaluate(User user) {
                return userHelper.canCreateIssue(user, model.getProject());
            }
        };
    }
}