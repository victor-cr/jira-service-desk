package com.bics.jira.mail.web;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.bics.jira.mail.model.web.CreateOrCommentWebModel;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 03.02.13 12:59
 */
@WebSudoRequired
public class CreateOrCommentMailHandlerDetailsWebAction extends ServiceDeskMailHandlerDetailsWebAction<CreateOrCommentWebModel> {
    private final CreateOrCommentWebModel model = new CreateOrCommentWebModel();

    public CreateOrCommentMailHandlerDetailsWebAction(PluginAccessor pluginAccessor) {
        super(pluginAccessor);
    }

    @Override
    public CreateOrCommentWebModel getModel() {
        return model;
    }

    public String getProjectKey() {
        return model.getProjectKey();
    }

    public void setProjectKey(String projectKey) {
        model.setProjectKey(projectKey);
    }

    public String getIssueTypeId() {
        return model.getIssueTypeId();
    }

    public void setIssueTypeId(String issueTypeId) {
        model.setIssueTypeId(issueTypeId);
    }

    public Long getComponentKey() {
        return model.getComponentId();
    }

    public void setComponentKey(Long componentKey) {
        model.setComponentId(componentKey);
    }

    public boolean isStripQuotes() {
        return model.isStripQuotes();
    }

    public void setStripQuotes(boolean stripQuotes) {
        model.setStripQuotes(stripQuotes);
    }

    public Long getResolvedBefore() {
        return model.getResolvedBefore();
    }

    public void setResolvedBefore(Long resolvedBefore) {
        model.setResolvedBefore(resolvedBefore);
    }

    public boolean isCreateUsers() {
        return model.isCreateUsers();
    }

    public void setCreateUsers(boolean createUsers) {
        model.setCreateUsers(createUsers);
    }

    public void setTransitions(String[] transitions) {
        model.setTransitions(transitions);
    }

    public String[] getTransitions() {
        return model.getTransitions();
    }

    public String getReporterUsername() {
        return model.getReporterUsername();
    }

    public void setReporterUsername(String reporterUsername) {
        model.setReporterUsername(reporterUsername);
    }

    public String getCatchEmail() {
        return model.getCatchEmail();
    }

    public void setCatchEmail(String catchEmail) {
        model.setCatchEmail(catchEmail);
    }

    public boolean isCcAssignee() {
        return model.isCcAssignee();
    }

    public void setCcAssignee(boolean ccAssignee) {
        model.setCcAssignee(ccAssignee);
    }

    public boolean isCcWatcher() {
        return model.isCcWatcher();
    }

    public void setCcWatcher(boolean ccWatcher) {
        model.setCcWatcher(ccWatcher);
    }

    public boolean isNotifyUsers() {
        return model.isNotifyUsers();
    }

    public void setNotifyUsers(boolean notifyUsers) {
        model.setNotifyUsers(notifyUsers);
    }
}
