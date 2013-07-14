package com.bics.jira.mail.helper;

import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.bics.jira.mail.IssueKeyHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 04.02.13 21:38
 */
public class IssueKeyHelperImpl implements IssueKeyHelper {
    private final ApplicationProperties applicationProperties;
    private final IssueManager issueManager;

    public IssueKeyHelperImpl(ApplicationProperties applicationProperties, IssueManager issueManager) {
        this.applicationProperties = applicationProperties;
        this.issueManager = issueManager;
    }

    @Override
    public Collection<String> findIssueKeys(String text) {
        String pattern = applicationProperties.getString(APKeys.JIRA_PROJECTKEY_PATTERN);

        if (pattern == null) {
            return Collections.emptySet();
        }

        Pattern projectKey = Pattern.compile("(?<!\\w)" + pattern + "\\-\\d+(?!\\w)");
        Collection<String> collection = new ArrayList<String>();

        Matcher matcher = projectKey.matcher(text);

        while (matcher.find()) {
            int start = matcher.start();
            int offset = matcher.end();

            String ticket = text.substring(start, offset);

            try {
                Issue issue = issueManager.getIssueObject(ticket);

                if (issue != null) {
                    collection.add(ticket);
                }
            } catch (Exception e) {
                //Ignore error
            }
        }

        return collection;
    }
}
