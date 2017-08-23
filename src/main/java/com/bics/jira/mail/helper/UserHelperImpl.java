package com.bics.jira.mail.helper;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.OperationNotPermittedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.bc.user.UserService;
import com.atlassian.jira.bc.user.search.UserSearchService;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.exception.PermissionException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.license.LicenseCountService;
import com.atlassian.jira.license.LicenseDetails;
import com.atlassian.jira.permission.ProjectPermissions;
import com.atlassian.jira.project.DefaultAssigneeException;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.util.collect.CollectionUtil;
import com.bics.jira.mail.UserHelper;
import org.apache.commons.lang.StringUtils;

import javax.mail.internet.InternetAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 28.02.13 21:37
 */
public class UserHelperImpl implements UserHelper {
    private final LicenseDetails licenseDetails;
    private final LicenseCountService licenseCountService;
    private final UserSearchService userSearchService;
    private final UserManager userManager;
    private final UserService userService;
    private final GroupManager groupManager;
    private final PermissionManager permissionManager;
    private final ProjectManager projectManager;

    public UserHelperImpl(LicenseDetails licenseDetails, LicenseCountService licenseCountService, UserSearchService userSearchService, UserManager userManager, UserService userService, GroupManager groupManager, PermissionManager permissionManager, ProjectManager projectManager) {
        this.licenseDetails = licenseDetails;
        this.licenseCountService = licenseCountService;
        this.userSearchService = userSearchService;
        this.userManager = userManager;
        this.userService = userService;
        this.groupManager = groupManager;
        this.permissionManager = permissionManager;
        this.projectManager = projectManager;
    }

    @Override
    public ApplicationUser find(String userName) {
        return userManager.getUserByName(userName);
    }

    @Override
    public ApplicationUser find(InternetAddress address) {
        return CollectionUtil.first(find(new InternetAddress[]{address}));
    }

    @Override
    public Collection<ApplicationUser> find(InternetAddress[] addresses) {
        return Arrays.stream(addresses)
                .map(e -> userSearchService.findUsersByEmail(e.getAddress()))
                .flatMap(e -> StreamSupport.stream(e.spliterator(), false))
                .filter(ApplicationUser::isActive)
                .collect(Collectors.toList());
    }

    @Override
    public ApplicationUser create(InternetAddress address, boolean notifyNewUsers, MessageHandlerErrorCollector monitor) {
        String email = address.getAddress();
        String fullName = address.getPersonal();

        try {
            UserService.CreateUserRequest request = UserService.CreateUserRequest
                    .withUserDetails(null, email, email, email, fullName)
                    .sendNotification(notifyNewUsers);

            if (notifyNewUsers) {
                request = request.sendUserSignupEvent();
            }

            UserService.CreateUserValidationResult result = userService.validateCreateUser(request);

            return userService.createUser(result);
        } catch (PermissionException e) {
            monitor.error("Permission problem: " + e.getMessage(), e);
        } catch (CreateException e) {
            monitor.error("ApplicationUser creation problem: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public ApplicationUser ensure(InternetAddress address, boolean createUsers, boolean notifyNewUsers, MessageHandlerErrorCollector monitor) {
        ApplicationUser user = find(address);

        if (!createUsers || user != null) {
            return user;
        }

        return create(address, notifyNewUsers, monitor);
    }

    @Override
    public Collection<ApplicationUser> ensure(InternetAddress[] addresses, boolean createUsers, boolean notifyNewUsers, MessageHandlerErrorCollector monitor) {
        Collection<ApplicationUser> users = find(addresses);

        Group group = groupManager.getGroup("jira-users");

        for (ApplicationUser user : users) {
            try {
                if (userManager.hasWritableDirectory()) {
                    Directory directory = userManager.getDirectory(user.getDirectoryId());

                    if (directory.isActive() && directory.getType() != DirectoryType.INTERNAL
                            && group != null && !groupManager.isUserInGroup(user, group)) {
                        groupManager.addUserToGroup(user, group);
                    }
                }
            } catch (GroupNotFoundException | UserNotFoundException | OperationNotPermittedException | OperationFailedException e) {
                monitor.error(e.getMessage(), e);
            }
        }

        if (!createUsers || users.size() == addresses.length) {
            return users;
        }

        Collection<String> created = CollectionUtil.transform(users, e -> StringUtils.trimToNull(StringUtils.lowerCase(e.getEmailAddress())));
        Map<String, InternetAddress> all = toSortedMap(addresses);

        for (Map.Entry<String, InternetAddress> email : all.entrySet()) {
            if (!created.contains(email.getKey())) {
                ApplicationUser user = create(email.getValue(), notifyNewUsers, monitor);

                users.add(user);
            }
        }

        return users;
    }

    @Override
    public boolean canAssignTo(ApplicationUser user, Project project) {
        return permissionManager.hasPermission(ProjectPermissions.ASSIGNABLE_USER, project, user);
    }

    @Override
    public boolean canCreateIssue(ApplicationUser user, Project project) {
        return permissionManager.hasPermission(ProjectPermissions.CREATE_ISSUES, project, user);
    }

    @Override
    public boolean canCommentIssue(ApplicationUser user, Project project) {
        return permissionManager.hasPermission(ProjectPermissions.ADD_COMMENTS, project, user);
    }

    @Override
    public boolean canCommentIssue(ApplicationUser user, Issue issue) {
        return permissionManager.hasPermission(ProjectPermissions.ADD_COMMENTS, issue, user);
    }

    @Override
    public boolean canCreateAttachment(ApplicationUser user, Project project) {
        return permissionManager.hasPermission(ProjectPermissions.CREATE_ATTACHMENTS, project, user);
    }

    @Override
    public boolean canManageWatchList(ApplicationUser user, Project project) {
        return permissionManager.hasPermission(ProjectPermissions.MANAGE_WATCHERS, project, user);
    }

    @Override
    public boolean canAddUsers() {
        return userManager.hasWritableDirectory() && licenseCountService.totalBillableUsers() < licenseDetails.getJiraLicense().getMaximumNumberOfUsers();
    }

    @Override
    public ApplicationUser getDefaultAssignee(Project project, ProjectComponent... components) {
        Collection<ProjectComponent> projectComponents = Arrays.stream(components).filter(Objects::nonNull).collect(Collectors.toList());

        try {
            ApplicationUser defaultAssignee = projectManager.getDefaultAssignee(project, projectComponents);

            return canAssignTo(defaultAssignee, project) ? defaultAssignee : null;
        } catch (DefaultAssigneeException e) {
            return null;
        }
    }

    private SortedMap<String, InternetAddress> toSortedMap(InternetAddress[] addresses) {
        SortedMap<String, InternetAddress> map = new TreeMap<>();

        for (InternetAddress address : addresses) {
            String key = StringUtils.trimToNull(StringUtils.lowerCase(address.getAddress()));

            if (key != null) {
                map.put(key, address);
            }
        }

        return map;
    }
}
