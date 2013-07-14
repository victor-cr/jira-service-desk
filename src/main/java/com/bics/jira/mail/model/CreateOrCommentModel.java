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
public class CreateOrCommentModel extends ServiceDeskModel {
    private Project project;
    private IssueType issueType;
    private ProjectComponent projectComponent;
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

    public ProjectComponent getProjectComponent() {
        return projectComponent;
    }

    public void setProjectComponent(ProjectComponent projectComponent) {
        this.projectComponent = projectComponent;
    }

    public Pattern getSplitRegex() {
        return splitRegex;
    }

    public void setSplitRegex(Pattern splitRegex) {
        this.splitRegex = splitRegex;
    }
}
