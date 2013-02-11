package com.bics.jira.mail.web;

import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.websudo.WebSudoRequired;

import java.util.Collection;
import java.util.Map;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 03.02.13 12:59
 */
@WebSudoRequired
public class ServiceDeskMailHandlerDetailsWebAction extends AbstractServiceDeskMailHandlerDetailsWebAction {
    public ServiceDeskMailHandlerDetailsWebAction(PluginAccessor pluginAccessor, IssueTypeManager issueTypeManager, ProjectManager projectManager) {
        super(pluginAccessor, issueTypeManager, projectManager);
    }

    public String getProjectKey() {
        return model.getProjectKey();
    }

    public String getIssueTypeKey() {
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

    public void setTransitions(String transitions) {
        model.setTransitions(transitions);
    }

    public boolean isStripQuotes() {
        return model.isStripQuotes();
    }

    public String getTransitions() {
        return model.getTransitions();
    }

    public void setIssueTypeKey(String issueTypeKey) {
        model.setIssueTypeId(issueTypeKey);
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

    @Override
    @Deprecated
    public Collection<String> getErrorMessages() {
        return super.getErrorMessages();
    }

    @Override
    @Deprecated
    public Map<String, String> getErrors() {
        return super.getErrors();
    }
}
