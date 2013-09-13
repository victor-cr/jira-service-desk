package com.bics.jira.mail.mock;

import com.atlassian.core.AtlassianCoreException;
import com.atlassian.core.user.preferences.Preferences;
import com.atlassian.core.user.preferences.UserPreferences;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.user.preferences.PreferenceKeys;
import com.atlassian.jira.user.preferences.UserPreferencesManager;
import com.opensymphony.module.propertyset.map.MapPropertySet;

import java.util.HashMap;
import java.util.Map;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 13/09/13 11:23
 */
public class MockUserPreferencesManager implements UserPreferencesManager {
    @Override
    public Preferences getPreferences(User user) {
        Preferences preferences = new MockPreferences();

        try {
            preferences.setString(PreferenceKeys.USER_TIMEZONE, "UTC");
        } catch (AtlassianCoreException e) {
            throw new RuntimeException(e);
        }

        return preferences;
    }

    @Override
    public void clearCache(String s) {
    }

    @Override
    public void clearCache() {
    }

    private static class MockPreferences implements Preferences {
        private final Map<String, Object> map = new HashMap<String, Object>();

        @Override
        public long getLong(String key) {
            return (Long) map.get(key);
        }

        @Override
        public void setLong(String key, long i) throws AtlassianCoreException {
            map.put(key, i);
        }

        @Override
        public String getString(String key) {
            return (String) map.get(key);
        }

        @Override
        public void setString(String key, String value) throws AtlassianCoreException {
            map.put(key, value);
        }

        @Override
        public boolean getBoolean(String key) {
            return (Boolean) map.get(key);
        }

        @Override
        public void setBoolean(String key, boolean b) throws AtlassianCoreException {
            map.put(key, b);
        }

        @Override
        public void remove(String key) throws AtlassianCoreException {
            map.remove(key);
        }
    }
}
