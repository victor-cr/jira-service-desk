package com.bics.jira.mail.model.service;

import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;

import java.util.regex.Pattern;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 03.02.13 22:29
 */
public class CreateOrCommentModel extends ServiceDeskModel {
    private Project project;
    private IssueType issueType;
    private String componentName;
    private Pattern componentRegex;
    private Pattern splitRegex;

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

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public Pattern getComponentRegex() {
        return componentRegex;
    }

    public void setComponentRegex(Pattern componentRegex) {
        this.componentRegex = componentRegex;
    }

    public Pattern getSplitRegex() {
        return splitRegex;
    }

    public void setSplitRegex(Pattern splitRegex) {
        this.splitRegex = splitRegex;
    }
}
