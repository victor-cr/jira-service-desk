package com.bics.jira.mail;

import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.atlassian.jira.user.ApplicationUser;

import javax.mail.internet.InternetAddress;
import java.util.Collection;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 04.02.13 21:57
 */
public interface UserHelper {
    ApplicationUser find(String userName);

    ApplicationUser find(InternetAddress address);

    Collection<ApplicationUser> find(InternetAddress[] addresses);

    ApplicationUser create(InternetAddress address, boolean notifyNewUsers, MessageHandlerErrorCollector monitor);

    ApplicationUser ensure(InternetAddress address, boolean createUsers, boolean notifyNewUsers, MessageHandlerErrorCollector monitor);

    Collection<ApplicationUser> ensure(InternetAddress[] addresses, boolean createUsers, boolean notifyNewUsers, MessageHandlerErrorCollector monitor);

    boolean canAssignTo(ApplicationUser user, Project project);

    boolean canCreateIssue(ApplicationUser user, Project project);

    boolean canCommentIssue(ApplicationUser user, Project project);

    boolean canCommentIssue(ApplicationUser user, Issue issue);

    boolean canCreateAttachment(ApplicationUser user, Project project);

    boolean canManageWatchList(ApplicationUser user, Project project);

    ApplicationUser getDefaultAssignee(Project project, ProjectComponent... components);

    boolean canAddUsers();
}
