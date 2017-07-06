package com.bics.jira.user;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.OperationNotPermittedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.user.UserEvent;
import com.atlassian.jira.event.user.UserEventListener;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 14/11/13 17:16
 */
public class DefaultGroupAssignUserHandler implements UserEventListener {
    private final static Logger LOG = LoggerFactory.getLogger(DefaultGroupAssignUserHandler.class);
    private final static String KEY_GROUP_NAME = "groupName";
    private String groupName;

    @Override
    public void userSignup(UserEvent userEvent) {
    }

    @Override
    public void userCreated(UserEvent userEvent) {
        LOG.info("New user has been created");

        ApplicationUser user = userEvent.getUser();

        UserManager userManager = ComponentAccessor.getUserManager();
        GroupManager groupManager = ComponentAccessor.getGroupManager();

        try {
            if (userManager.hasWritableDirectory()) {
                LOG.info("Will try to assign it to the group: " + groupName);
                Directory directory = userManager.getDirectory(user.getDirectoryId());

                if (directory.isActive() && directory.getType() != DirectoryType.INTERNAL) {
                    Group group = groupManager.getGroup(groupName);

                    if (group != null && !groupManager.isUserInGroup(user, group)) {
                        groupManager.addUserToGroup(user, group);
                    }

                    userManager.updateUser(user);
                }
            }
        } catch (GroupNotFoundException e) {
            LOG.error(e.getMessage(), e);
        } catch (UserNotFoundException e) {
            LOG.error(e.getMessage(), e);
        } catch (OperationNotPermittedException e) {
            LOG.error(e.getMessage(), e);
        } catch (OperationFailedException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public void userForgotPassword(UserEvent userEvent) {
    }

    @Override
    public void userForgotUsername(UserEvent userEvent) {
    }

    @Override
    public void userCannotChangePassword(UserEvent userEvent) {
    }

    @Override
    public void init(Map map) {
        groupName = (String) map.get(KEY_GROUP_NAME);

        if (groupName == null) {
            groupName = "jira-users";
        }
    }

    @Override
    public String[] getAcceptedParams() {
        return new String[]{KEY_GROUP_NAME};
    }

    @Override
    public boolean isInternal() {
        return true;
    }

    @Override
    public boolean isUnique() {
        return false;
    }

    @Override
    public String getDescription() {
        return "Automatic user group assignment for LDAP users";
    }
}
