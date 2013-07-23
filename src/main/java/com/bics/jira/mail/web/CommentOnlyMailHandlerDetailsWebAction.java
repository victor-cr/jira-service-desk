package com.bics.jira.mail.web;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.bics.jira.mail.model.web.CommentOnlyWebModel;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 03.02.13 12:59
 */
@WebSudoRequired
public class CommentOnlyMailHandlerDetailsWebAction extends ServiceDeskMailHandlerDetailsWebAction<CommentOnlyWebModel> {
    private final CommentOnlyWebModel model = new CommentOnlyWebModel();

    public CommentOnlyMailHandlerDetailsWebAction(PluginAccessor pluginAccessor) {
        super(pluginAccessor);
    }

    @Override
    public CommentOnlyWebModel getModel() {
        return model;
    }

    public void setCcAssignee(boolean ccAssignee) {
        model.setCcAssignee(ccAssignee);
    }

    public void setStripQuotes(boolean stripQuotes) {
        model.setStripQuotes(stripQuotes);
    }

    public void setNotifyUsers(boolean notifyUsers) {
        model.setNotifyUsers(notifyUsers);
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

    public void setCreateUsers(boolean createUsers) {
        model.setCreateUsers(createUsers);
    }

    public String getReporterUsername() {
        return model.getReporterUsername();
    }

    public void setReporterUsername(String reporterUsername) {
        model.setReporterUsername(reporterUsername);
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
