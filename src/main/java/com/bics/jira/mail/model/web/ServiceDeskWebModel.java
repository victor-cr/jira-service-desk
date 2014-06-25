package com.bics.jira.mail.model.web;

import com.google.common.collect.Maps;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 03.02.13 22:29
 */
public class ServiceDeskWebModel {
    protected static final String KEY_PROJECT = "project";
    protected static final String KEY_ISSUE_TYPE = "issuetype";
    protected static final String KEY_COMPONENT = "component";
    protected static final String KEY_COMPONENT_REGEX = "componentregex";
    protected static final String KEY_TRANSITIONS = "transitions";
    protected static final String KEY_REPORTER_USERNAME = "reporterusername";
    protected static final String KEY_CATCH_EMAIL = "catchemail";
    protected static final String KEY_CREATE_USERS = "createusers";
    protected static final String KEY_NOTIFY_USERS = "notifyusers";
    protected static final String KEY_CC_WATCHER = "ccwatcher";
    protected static final String KEY_CC_ASSIGNEE = "ccassignee";
    protected static final String KEY_STRIP_QUOTES = "stripquotes";
    protected static final String KEY_RESOLVED_BEFORE = "resolvedbefore";

    private String reporterUsername;
    private Long resolvedBefore;
    private String[] transitions;
    private boolean stripQuotes;
    private boolean createUsers;
    private boolean notifyUsers;
    private boolean ccWatcher;
    private boolean ccAssignee;

    public String[] getTransitions() {
        return transitions;
    }

    public void setTransitions(String[] transitions) {
        this.transitions = transitions;
    }

    public Long getResolvedBefore() {
        return resolvedBefore;
    }

    public void setResolvedBefore(Long resolvedBefore) {
        this.resolvedBefore = resolvedBefore;
    }

    public boolean isStripQuotes() {
        return stripQuotes;
    }

    public void setStripQuotes(boolean stripQuotes) {
        this.stripQuotes = stripQuotes;
    }

    public String getReporterUsername() {
        return reporterUsername;
    }

    public void setReporterUsername(String reporterUsername) {
        this.reporterUsername = reporterUsername;
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

    public Map<String, String> toServiceParams() {
        Map<String, String> map = Maps.newLinkedHashMap();

        safePut(map, KEY_TRANSITIONS, arrayEncode(transitions));
        safePut(map, KEY_REPORTER_USERNAME, valueOf(reporterUsername));
        safePut(map, KEY_CREATE_USERS, valueOf(createUsers));
        safePut(map, KEY_NOTIFY_USERS, valueOf(notifyUsers));
        safePut(map, KEY_CC_WATCHER, valueOf(ccWatcher));
        safePut(map, KEY_CC_ASSIGNEE, valueOf(ccAssignee));
        safePut(map, KEY_STRIP_QUOTES, valueOf(stripQuotes));
        safePut(map, KEY_RESOLVED_BEFORE, valueOf(resolvedBefore));

        return map;
    }

    public ServiceDeskWebModel fromServiceParams(Map<String, String> params) {
        this.transitions = arrayDecode(params.get(KEY_TRANSITIONS));
        this.reporterUsername = params.get(KEY_REPORTER_USERNAME);
        this.createUsers = safeGetB(params, KEY_CREATE_USERS);
        this.notifyUsers = safeGetB(params, KEY_NOTIFY_USERS);
        this.ccWatcher = safeGetB(params, KEY_CC_WATCHER);
        this.ccAssignee = safeGetB(params, KEY_CC_ASSIGNEE);
        this.stripQuotes = safeGetB(params, KEY_STRIP_QUOTES);
        this.resolvedBefore = safeGetL(params, KEY_RESOLVED_BEFORE);

        return this;
    }

    protected static String arrayEncode(String[] data) {
        return data == null || data.length == 0 ? null : StringUtils.join(data, ',');
    }

    protected static String[] arrayDecode(String data) {
        return StringUtils.isBlank(data) ? null : StringUtils.split(data, ';');
    }

    protected static boolean safeGetB(Map<String, String> map, String key) {
        String value = map.get(key);

        return value == null || Boolean.parseBoolean(value);
    }

    protected static Long safeGetL(Map<String, String> map, String key) {
        try {
            return Long.parseLong(map.get(key));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected static <T> void safePut(Map<String, T> map, String key, T obj) {
        if (obj != null) {
            map.put(key, obj);
        }
    }

    protected static <T> String valueOf(T obj) {
        if (obj == null) {
            return null;
        }

        String value = String.valueOf(obj);

        return StringUtils.isBlank(value) ? null : value;
    }
}
