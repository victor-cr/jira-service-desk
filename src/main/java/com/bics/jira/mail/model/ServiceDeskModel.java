package com.bics.jira.mail.model;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;

import javax.mail.internet.InternetAddress;
import java.util.regex.Pattern;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 03.02.13 22:29
 */
public class ServiceDeskModel {
    private User reporterUser;
    private int[] transitions;
    private boolean createUsers;
    private boolean notifyUsers;
    private boolean ccWatcher;
    private boolean ccAssignee;
    private boolean stripQuotes;

    public User getReporterUser() {
        return reporterUser;
    }

    public void setReporterUser(User reporterUser) {
        this.reporterUser = reporterUser;
    }

    public int[] getTransitions() {
        return transitions;
    }

    public void setTransitions(int[] transitions) {
        this.transitions = transitions;
    }

    public boolean isCreateUsers() {
        return createUsers;
    }

    public void setCreateUsers(boolean createUsers) {
        this.createUsers = createUsers;
    }

    public boolean isNotifyUsers() {
        return notifyUsers;
    }

    public void setNotifyUsers(boolean notifyUsers) {
        this.notifyUsers = notifyUsers;
    }

    public boolean isCcWatcher() {
        return ccWatcher;
    }

    public void setCcWatcher(boolean ccWatcher) {
        this.ccWatcher = ccWatcher;
    }

    public boolean isCcAssignee() {
        return ccAssignee;
    }

    public void setCcAssignee(boolean ccAssignee) {
        this.ccAssignee = ccAssignee;
    }

    public boolean isStripQuotes() {
        return stripQuotes;
    }

    public void setStripQuotes(boolean stripQuotes) {
        this.stripQuotes = stripQuotes;
    }
}
