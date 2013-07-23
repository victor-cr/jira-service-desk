package com.bics.jira.mail.model.web;

import java.util.Map;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 03.02.13 22:29
 */
public class CreateOrCommentWebModel extends ServiceDeskWebModel {
    private String projectKey;
    private String issueTypeId;
    private Long componentId;
    private String catchEmail;

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

    public String getCatchEmail() {
        return catchEmail;
    }

    public void setCatchEmail(String catchEmail) {
        this.catchEmail = catchEmail;
    }

    public Map<String, String> toServiceParams() {
        Map<String, String> map = super.toServiceParams();

        safePut(map, KEY_PROJECT, valueOf(projectKey));
        safePut(map, KEY_ISSUE_TYPE, valueOf(issueTypeId));
        safePut(map, KEY_COMPONENT, valueOf(componentId));
        safePut(map, KEY_CATCH_EMAIL, valueOf(catchEmail));

        return map;
    }

    public CreateOrCommentWebModel fromServiceParams(Map<String, String> params) {
        super.fromServiceParams(params);

        this.projectKey = params.get(KEY_PROJECT);
        this.issueTypeId = params.get(KEY_ISSUE_TYPE);
        this.componentId = safeGetL(params, KEY_COMPONENT);
        this.catchEmail = params.get(KEY_CATCH_EMAIL);

        return this;
    }
}
