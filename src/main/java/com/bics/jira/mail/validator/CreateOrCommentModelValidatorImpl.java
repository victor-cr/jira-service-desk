package com.bics.jira.mail.validator;

import com.atlassian.jira.bc.project.component.ProjectComponentManager;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.config.StatusManager;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.bics.jira.mail.CreateOrCommentModelValidator;
import com.bics.jira.mail.UserHelper;
import com.bics.jira.mail.model.service.CreateOrCommentModel;
import com.bics.jira.mail.model.web.CreateOrCommentWebModel;
import com.bics.jira.mail.validator.rule.ComponentRegexValidationRule;
import com.bics.jira.mail.validator.rule.IssueReporterValidationRule;
import com.bics.jira.mail.validator.rule.IssueTypeValidationRule;
import com.bics.jira.mail.validator.rule.ProjectComponentValidationRule;
import com.bics.jira.mail.validator.rule.ProjectValidationRule;
import com.bics.jira.mail.validator.rule.ResolvedBeforeValidationRule;
import com.bics.jira.mail.validator.rule.TransitionValidationRule;
import com.bics.jira.mail.validator.rule.UserAttributeValidationRule;
import com.bics.jira.mail.validator.rule.ValidationRule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 03.02.13 12:57
 */
public class CreateOrCommentModelValidatorImpl extends ServiceDeskModelValidator<CreateOrCommentModel, CreateOrCommentWebModel> implements CreateOrCommentModelValidator {
    private final ProjectManager projectManager;
    private final IssueTypeManager issueTypeManager;
    private final ProjectComponentManager projectComponentManager;
    private final StatusManager statusManager;
    private final UserHelper userHelper;

    public CreateOrCommentModelValidatorImpl(ProjectManager projectManager, IssueTypeManager issueTypeManager, ProjectComponentManager projectComponentManager, StatusManager statusManager, UserHelper userHelper) {
        this.projectManager = projectManager;
        this.issueTypeManager = issueTypeManager;
        this.projectComponentManager = projectComponentManager;
        this.statusManager = statusManager;
        this.userHelper = userHelper;
    }

    @Override
    protected CreateOrCommentWebModel convertWebModel(Map<String, String> params) {
        return new CreateOrCommentWebModel().fromServiceParams(params);
    }

    @Override
    protected Collection<ValidationRule<CreateOrCommentModel, CreateOrCommentWebModel>> createRuleSet(MessageHandlerErrorCollector monitor) {
        Collection<ValidationRule<CreateOrCommentModel, CreateOrCommentWebModel>> rules = new ArrayList<ValidationRule<CreateOrCommentModel, CreateOrCommentWebModel>>(6);

        rules.add(new ResolvedBeforeValidationRule<CreateOrCommentModel, CreateOrCommentWebModel>(monitor));
        rules.add(new ProjectValidationRule(monitor, projectManager));
        rules.add(new IssueTypeValidationRule(monitor, issueTypeManager));
        rules.add(new ProjectComponentValidationRule(monitor, projectComponentManager));
        rules.add(new ComponentRegexValidationRule(monitor));
        rules.add(new TransitionValidationRule<CreateOrCommentModel, CreateOrCommentWebModel>(monitor, statusManager));
        rules.add(new IssueReporterValidationRule(monitor, userHelper));
        rules.add(new UserAttributeValidationRule<CreateOrCommentModel, CreateOrCommentWebModel>(monitor, userHelper));

        return rules;
    }
}