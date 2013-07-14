package com.bics.jira.mail.web;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.websudo.WebSudoRequired;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 03.02.13 12:59
 */
@WebSudoRequired
public class CreateOrCommentMailHandlerDetailsWebAction extends ServiceDeskMailHandlerDetailsWebAction {
    public CreateOrCommentMailHandlerDetailsWebAction(PluginAccessor pluginAccessor) {
        super(pluginAccessor);
    }

    public String getProjectKey() {
        return model.getProjectKey();
    }

    public String getIssueTypeId() {
        return model.getIssueTypeId();
    }

    public void setCcAssignee(boolean ccAssignee) {
        model.setCcAssignee(ccAssignee);
    }

    public Long getComponentKey() {
        return model.getComponentId();
    }

    public void setComponentKey(Long componentKey) {
        model.setComponentId(componentKey);
    }

    public void setStripQuotes(boolean stripQuotes) {
        model.setStripQuotes(stripQuotes);
    }

    public void setNotifyUsers(boolean notifyUsers) {
        model.setNotifyUsers(notifyUsers);
    }

    public String getSplitRegex() {
        return model.getSplitRegex();
    }

    public boolean isCreateUsers() {
        return model.isCreateUsers();
    }

    public void setTransitions(String[] transitions) {
        model.setTransitions(transitions);
    }

    public String[] getTransitions() {
        return model.getTransitions();
    }

    public boolean isStripQuotes() {
        return model.isStripQuotes();
    }

    public void setIssueTypeId(String issueTypeId) {
        model.setIssueTypeId(issueTypeId);
    }

    public void setCreateUsers(boolean createUsers) {
        model.setCreateUsers(createUsers);
    }

    public String getReporterUsername() {
        return model.getReporterUsername();
    }

    public void setReporterUsername(String reporterUsername) {
        model.setReporterUsername(reporterUsername);
    }

    public void setProjectKey(String projectKey) {
        model.setProjectKey(projectKey);
    }

    public void setSplitRegex(String splitRegex) {
        model.setSplitRegex(splitRegex);
    }

    public void setCatchEmail(String catchEmail) {
        model.setCatchEmail(catchEmail);
    }

    public String getCatchEmail() {
        return model.getCatchEmail();
    }

    public boolean isCcAssignee() {
        return model.isCcAssignee();
    }

    public boolean isCcWatcher() {
        return model.isCcWatcher();
    }

    public boolean isNotifyUsers() {
        return model.isNotifyUsers();
    }

    public void setCcWatcher(boolean ccWatcher) {
        model.setCcWatcher(ccWatcher);
    }
}
