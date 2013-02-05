package com.bics.jira.mail.model;

import com.atlassian.jira.service.services.file.AbstractMessageHandlingService;
import com.atlassian.jira.service.util.ServiceUtils;
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
public class ServiceModel {
    private static final String KEY_PROJECT = "project";
    private static final String KEY_ISSUE_TYPE = "issuetype";
    private static final String KEY_COMPONENT = "component";
    private static final String KEY_TRANSITIONS = "transitions";
    private static final String KEY_REPORTER_USERNAME = "reporterusername";
    private static final String KEY_CATCH_EMAIL = "catchemail";
    private static final String KEY_SPLIT_REGEX = "splitregex";
    private static final String KEY_CREATE_USERS = "createusers";
    private static final String KEY_NOTIFY_USERS = "notifyusers";
    private static final String KEY_CC_WATCHER = "ccwatcher";
    private static final String KEY_CC_ASSIGNEE = "ccassignee";
    private static final String KEY_STRIP_QUOTES = "stripquotes";

    private String projectKey;
    private String issueTypeId;
    private Long componentId;
    private String transitions;
    private boolean stripQuotes;
    private String reporterUsername;
    private String catchEmail;
    private boolean createUsers;
    private boolean notifyUsers;
    private boolean ccWatcher;
    private boolean ccAssignee;
    private String splitRegex;

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public String getIssueTypeId() {
        return issueTypeId;
    }

    public void setIssueTypeId(String issueTypeId) {
        this.issueTypeId = issueTypeId;
    }

    public Long getComponentId() {
        return componentId;
    }

    public void setComponentId(Long componentId) {
        this.componentId = componentId;
    }

    public String getTransitions() {
        return transitions;
    }

    public void setTransitions(String transitions) {
        this.transitions = transitions;
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

    public String getCatchEmail() {
        return catchEmail;
    }

    public void setCatchEmail(String catchEmail) {
        this.catchEmail = catchEmail;
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

    public String getSplitRegex() {
        return splitRegex;
    }

    public void setSplitRegex(String splitRegex) {
        this.splitRegex = splitRegex;
    }

    public Map<String, String[]> toServiceParams() {
        Map<String, String> map = Maps.newLinkedHashMap();

        safePut(map, KEY_PROJECT, valueOf(projectKey));
        safePut(map, KEY_ISSUE_TYPE, valueOf(issueTypeId));
        safePut(map, KEY_COMPONENT, valueOf(componentId));
        safePut(map, KEY_TRANSITIONS, encode(transitions));
        safePut(map, KEY_REPORTER_USERNAME, valueOf(reporterUsername));
        safePut(map, KEY_CATCH_EMAIL, valueOf(catchEmail));
        safePut(map, KEY_SPLIT_REGEX, encode(splitRegex));
        safePut(map, KEY_CREATE_USERS, valueOf(createUsers));
        safePut(map, KEY_NOTIFY_USERS, valueOf(notifyUsers));
        safePut(map, KEY_CC_WATCHER, valueOf(ccWatcher));
        safePut(map, KEY_CC_ASSIGNEE, valueOf(ccAssignee));
        safePut(map, KEY_STRIP_QUOTES, valueOf(stripQuotes));

        Map<String, String[]> key = Maps.newHashMap();

        safePut(key, AbstractMessageHandlingService.KEY_HANDLER_PARAMS, new String[]{ServiceUtils.toParameterString(map)});

        return key;
    }

    public ServiceModel fromServiceParams(Map<String, String> params) {
        this.projectKey = params.get(KEY_PROJECT);
        this.issueTypeId = params.get(KEY_ISSUE_TYPE);
        this.componentId = safeGetL(params, KEY_COMPONENT);
        this.transitions = decode(params.get(KEY_TRANSITIONS));
        this.reporterUsername = params.get(KEY_REPORTER_USERNAME);
        this.catchEmail = params.get(KEY_CATCH_EMAIL);
        this.splitRegex = decode(params.get(KEY_SPLIT_REGEX));
        this.createUsers = safeGetB(params, KEY_CREATE_USERS);
        this.notifyUsers = safeGetB(params, KEY_NOTIFY_USERS);
        this.ccWatcher = safeGetB(params, KEY_CC_WATCHER);
        this.ccAssignee = safeGetB(params, KEY_CC_ASSIGNEE);
        this.stripQuotes = safeGetB(params, KEY_STRIP_QUOTES);

        return this;
    }

    private String encode(String value) {
        return StringUtils.isBlank(value) ? null : Base64.encodeBase64String(value.getBytes());
    }

    private String decode(String value) {
        return StringUtils.isBlank(value) ? null : new String(Base64.decodeBase64(value));
    }

    private static boolean safeGetB(Map<String, String> map, String key) {
        String value = map.get(key);

        return value == null || Boolean.parseBoolean(value);
    }

    private static Long safeGetL(Map<String, String> map, String key) {
        try {
            return Long.parseLong(map.get(key));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static <T> void safePut(Map<String, T> map, String key, T obj) {
        if (obj != null) {
            map.put(key, obj);
        }
    }

    private static <T> String valueOf(T obj) {
        if (obj == null) {
            return null;
        }

        String value = String.valueOf(obj);

        return StringUtils.isBlank(value) ? null : value;
    }
}
