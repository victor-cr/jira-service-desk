package com.bics.jira.mail.helper;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.event.user.UserEventType;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.exception.PermissionException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.project.DefaultAssigneeException;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.jira.util.Consumer;
import com.atlassian.jira.util.Function;
import com.atlassian.jira.util.NotNull;
import com.atlassian.jira.util.Predicate;
import com.atlassian.jira.util.collect.CollectionUtil;
import com.bics.jira.mail.UserHelper;
import org.apache.commons.lang.StringUtils;

import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 28.02.13 21:37
 */
public class UserHelperImpl implements UserHelper {
    private final UserUtil userUtil;
    private final UserManager userManager;
    private final PermissionManager permissionManager;
    private final ProjectManager projectManager;

    public UserHelperImpl(UserUtil userUtil, UserManager userManager, PermissionManager permissionManager, ProjectManager projectManager) {
        this.userUtil = userUtil;
        this.userManager = userManager;
        this.permissionManager = permissionManager;
        this.projectManager = projectManager;
    }

    @Override
    public User find(String userName) {
        return userUtil.getUser(userName);
    }

    @Override
    public User find(InternetAddress address) {
        return CollectionUtil.first(find(new InternetAddress[]{address}));
    }

    @Override
    public Collection<User> find(InternetAddress[] addresses) {
        Collection<User> users = userUtil.getUsers();
        FindUserConsumer consumer = new FindUserConsumer(toSortedSet(addresses));

        CollectionUtil.foreach(users, consumer);

        return new ArrayList<User>(consumer.map.values());
    }

    @Override
    public User create(InternetAddress address, boolean notifyNewUsers, MessageHandlerErrorCollector monitor) {
        String email = address.getAddress();
        String fullName = address.getPersonal();

        try {
            return notifyNewUsers
                    ? userUtil.createUserWithNotification(email, email, email, fullName, UserEventType.USER_CREATED)
                    : userUtil.createUserNoNotification(email, email, email, fullName);
        } catch (PermissionException e) {
            monitor.error("Permission problem: " + e.getMessage(), e);
        } catch (CreateException e) {
            monitor.error("User creation problem: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public User ensure(InternetAddress address, boolean createUsers, boolean notifyNewUsers, MessageHandlerErrorCollector monitor) {
        User user = find(address);

        if (!createUsers || user != null) {
            return user;
        }

        return create(address, notifyNewUsers, monitor);
    }

    @Override
    public Collection<User> ensure(InternetAddress[] addresses, boolean createUsers, boolean notifyNewUsers, MessageHandlerErrorCollector monitor) {
        Collection<User> users = find(addresses);

        if (!createUsers || users.size() == addresses.length) {
            return users;
        }

        Collection<String> created = CollectionUtil.transform(users, transformerUser());
        Map<String, InternetAddress> all = toSortedMap(addresses);

        for (Map.Entry<String, InternetAddress> email : all.entrySet()) {
            if (!created.contains(email.getKey())) {
                User user = create(email.getValue(), notifyNewUsers, monitor);

                users.add(user);
            }
        }

        return users;
    }

    @Override
    public boolean canAssignTo(User user, Project project) {
        return permissionManager.hasPermission(Permissions.ASSIGNABLE_USER, project, user);
    }

    @Override
    public boolean canCreateIssue(User user, Project project) {
        return permissionManager.hasPermission(Permissions.CREATE_ISSUE, project, user);
    }

    @Override
    public boolean canCommentIssue(User user, Project project) {
        return permissionManager.hasPermission(Permissions.COMMENT_ISSUE, project, user);
    }

    @Override
    public boolean canCommentIssue(User user, Issue issue) {
        return permissionManager.hasPermission(Permissions.COMMENT_ISSUE, issue, user);
    }

    @Override
    public boolean canCreateAttachment(User user, Project project) {
        return permissionManager.hasPermission(Permissions.CREATE_ATTACHMENT, project, user);
    }

    @Override
    public boolean canManageWatchList(User user, Project project) {
        return permissionManager.hasPermission(Permissions.MANAGE_WATCHER_LIST, project, user);
    }

    @Override
    public boolean canAddUsers() {
        return userManager.hasWritableDirectory() && !userUtil.hasExceededUserLimit();
    }

    @Override
    public User getDefaultAssignee(Project project, ProjectComponent... components) {
        Collection<ProjectComponent> projectComponents = CollectionUtil.filter(Arrays.asList(components), onlyExistent());

        try {
            User defaultAssignee = projectManager.getDefaultAssignee(project, projectComponents);

            return canAssignTo(defaultAssignee, project) ? defaultAssignee : null;
        } catch (DefaultAssigneeException e) {
            return null;
        }
    }

    protected Function<InternetAddress, String> transformerInternetAddress() {
        return new Function<InternetAddress, String>() {
            @Override
            public String get(InternetAddress address) {
                return StringUtils.trimToNull(StringUtils.lowerCase(address.getAddress()));
            }
        };
    }

    protected Function<User, String> transformerUser() {
        return new Function<User, String>() {
            @Override
            public String get(User user) {
                return StringUtils.trimToNull(StringUtils.lowerCase(user.getEmailAddress()));
            }
        };
    }

    protected <T> Predicate<T> onlyExistent() {
        return new Predicate<T>() {
            @Override
            public boolean evaluate(T obj) {
                return obj != null;
            }
        };
    }

    protected SortedSet<String> toSortedSet(InternetAddress[] addresses) {
        return new TreeSet<String>(toSortedMap(addresses).keySet());
    }

    protected SortedMap<String, InternetAddress> toSortedMap(InternetAddress[] addresses) {
        Predicate<String> filter = onlyExistent();
        Function<InternetAddress, String> transformer = transformerInternetAddress();
        SortedMap<String, InternetAddress> map = new TreeMap<String, InternetAddress>();

        for (InternetAddress address : addresses) {
            String key = transformer.get(address);

            if (filter.evaluate(key)) {
                map.put(key, address);
            }
        }

        return map;
    }

    private static class FindUserConsumer implements Consumer<User> {
        private final Map<String, User> map = new HashMap<String, User>();
        private final SortedSet<String> addresses;

        public FindUserConsumer(SortedSet<String> addresses) {
            this.addresses = addresses;
        }

        @Override
        public void consume(@NotNull User user) {
            String email = StringUtils.lowerCase(StringUtils.trimToNull(user.getEmailAddress()));

            if (email == null || !this.addresses.contains(email)) {
                return;
            }

            User oldOne = map.get(email);

            if (oldOne == null || !oldOne.isActive()) {
                map.put(email, user);
            }
        }
    }
}
