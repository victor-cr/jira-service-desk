package com.bics.jira.mail;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.OperationNotPermittedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;

import javax.mail.internet.InternetAddress;
import java.util.Collection;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 04.02.13 21:57
 */
public interface UserHelper {
    User find(String userName);

    User find(InternetAddress address);

    Collection<User> find(InternetAddress[] addresses);

    User create(InternetAddress address, boolean notifyNewUsers, MessageHandlerErrorCollector monitor);

    User ensure(InternetAddress address, boolean createUsers, boolean notifyNewUsers, MessageHandlerErrorCollector monitor);

    Collection<User> ensure(InternetAddress[] addresses, boolean createUsers, boolean notifyNewUsers, MessageHandlerErrorCollector monitor);

    boolean canAssignTo(User user, Project project);

    boolean canCreateIssue(User user, Project project);

    boolean canCommentIssue(User user, Project project);

    boolean canCommentIssue(User user, Issue issue);

    boolean canCreateAttachment(User user, Project project);

    boolean canManageWatchList(User user, Project project);

    User getDefaultAssignee(Project project, ProjectComponent... components);

    boolean canAddUsers();
}
