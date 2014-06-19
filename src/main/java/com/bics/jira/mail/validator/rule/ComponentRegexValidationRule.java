package com.bics.jira.mail.validator.rule;

import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.bics.jira.mail.model.service.CreateOrCommentModel;
import com.bics.jira.mail.model.web.CreateOrCommentWebModel;
import org.apache.commons.lang.StringUtils;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 19/07/13 12:25
 */
public class ComponentRegexValidationRule extends ValidationRule<CreateOrCommentModel, CreateOrCommentWebModel> {
    public ComponentRegexValidationRule(MessageHandlerErrorCollector monitor) {
        super(monitor);
    }

    @Override
    public void validate(CreateOrCommentWebModel webModel, CreateOrCommentModel serviceModel) {
        String componentRegex = webModel.getComponentRegex();

        if (StringUtils.isBlank(componentRegex)) {
            monitor.info("Project component regexp is not set.");
            return;
        }

        try {
            serviceModel.setComponentRegex(Pattern.compile(componentRegex));
        } catch (PatternSyntaxException e) {
            assertError(true, "Project component regexp %s is invalid.", componentRegex);
        }
    }
}
