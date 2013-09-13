package com.bics.jira.mail.validator;

import com.atlassian.jira.config.StatusManager;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.bics.jira.mail.CommentOnlyModelValidator;
import com.bics.jira.mail.UserHelper;
import com.bics.jira.mail.model.service.CommentOnlyModel;
import com.bics.jira.mail.model.web.CommentOnlyWebModel;
import com.bics.jira.mail.validator.rule.CommentReporterValidationRule;
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
public class CommentOnlyModelValidatorImpl extends ServiceDeskModelValidator<CommentOnlyModel, CommentOnlyWebModel> implements CommentOnlyModelValidator {
    private final StatusManager statusManager;
    private final UserHelper userHelper;

    public CommentOnlyModelValidatorImpl(StatusManager statusManager, UserHelper userHelper) {
        this.statusManager = statusManager;
        this.userHelper = userHelper;
    }

    @Override
    protected CommentOnlyWebModel convertWebModel(Map<String, String> params) {
        return new CommentOnlyWebModel().fromServiceParams(params);
    }

    @Override
    protected Collection<ValidationRule<CommentOnlyModel, CommentOnlyWebModel>> createRuleSet(MessageHandlerErrorCollector monitor) {
        Collection<ValidationRule<CommentOnlyModel, CommentOnlyWebModel>> rules = new ArrayList<ValidationRule<CommentOnlyModel, CommentOnlyWebModel>>(6);

        rules.add(new ResolvedBeforeValidationRule<CommentOnlyModel, CommentOnlyWebModel>(monitor));
        rules.add(new TransitionValidationRule<CommentOnlyModel, CommentOnlyWebModel>(monitor, statusManager));
        rules.add(new CommentReporterValidationRule(monitor, userHelper));
        rules.add(new UserAttributeValidationRule<CommentOnlyModel, CommentOnlyWebModel>(monitor, userHelper));

        return rules;
    }
}