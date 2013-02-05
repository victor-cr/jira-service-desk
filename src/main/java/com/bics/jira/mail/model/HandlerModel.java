package com.bics.jira.mail.model;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.project.Project;

import javax.mail.internet.InternetAddress;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 03.02.13 22:29
 */
public class HandlerModel {
    private Project project;
    private IssueType issueType;
    private ProjectComponent projectComponent;
    private Map<Status, Status> transitions;
    private User reporterUser;
    private InternetAddress catchEmail;
    private Pattern splitRegex;
    private boolean stripQuotes;
    private boolean createUsers;
    private boolean notifyUsers;
    private boolean ccWatcher;
    private boolean ccAssignee;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public IssueType getIssueType() {
        return issueType;
    }

    public void setIssueType(IssueType issueType) {
        this.issueType = issueType;
    }

    public ProjectComponent getProjectComponent() {
        return projectComponent;
    }

    public void setProjectComponent(ProjectComponent projectComponent) {
        this.projectComponent = projectComponent;
    }

    public Map<Status, Status> getTransitions() {
        return transitions;
    }

    public void setTransitions(Map<Status, Status> transitions) {
        this.transitions = transitions;
    }

    public User getReporterUser() {
        return reporterUser;
    }

    public void setReporterUser(User reporterUser) {
        this.reporterUser = reporterUser;
    }

    public InternetAddress getCatchEmail() {
        return catchEmail;
    }

    public void setCatchEmail(InternetAddress catchEmail) {
        this.catchEmail = catchEmail;
    }

    public Pattern getSplitRegex() {
        return splitRegex;
    }

    public void setSplitRegex(Pattern splitRegex) {
        this.splitRegex = splitRegex;
    }

    public boolean isStripQuotes() {
        return stripQuotes;
    }

    public void setStripQuotes(boolean stripQuotes) {
        this.stripQuotes = stripQuotes;
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
}
