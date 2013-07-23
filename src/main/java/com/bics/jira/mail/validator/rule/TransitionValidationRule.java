package com.bics.jira.mail.validator.rule;

import com.atlassian.jira.config.StatusManager;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.bics.jira.mail.model.service.ServiceDeskModel;
import com.bics.jira.mail.model.web.ServiceDeskWebModel;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 19/07/13 12:25
 */
public class TransitionValidationRule<M extends ServiceDeskModel, W extends ServiceDeskWebModel> extends ValidationRule<M, W> {
    private final StatusManager statusManager;

    public TransitionValidationRule(MessageHandlerErrorCollector monitor, StatusManager statusManager) {
        super(monitor);
        this.statusManager = statusManager;
    }

    @Override
    public void validate(W webModel, M serviceModel) {
        String[] transitions = webModel.getTransitions();

        if (transitions == null || transitions.length == 0) {
            monitor.info("Transitions are not set.");
            return;
        }

        Collection<Status> statuses = statusManager.getStatuses();
        Map<String, Status> statusMap = new HashMap<String, Status>();
        Map<Status, Status> statusTransition = new HashMap<Status, Status>();

        for (Status status : statuses) {
            statusMap.put(status.getId(), status);
        }

        for (String transition : transitions) {
            String[] parse = StringUtils.split(transition, "->");

            Status left = statusMap.get(StringUtils.strip(parse[0]));
            Status right = statusMap.get(StringUtils.strip(parse[1]));

            assertError(left == null || right == null, "A transition " + transition + " is impossible because there is no such statuses.");

            statusTransition.put(left, right);
        }

        serviceModel.setTransitions(statusTransition);
    }
}
