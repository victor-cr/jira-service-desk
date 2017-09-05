package com.bics.jira.mail.helper;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.service.util.handler.MessageHandlerErrorCollector;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;
import com.bics.jira.mail.IssueLookupHelper;
import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 04.02.13 21:38
 */
public class IssueLookupHelperImpl implements IssueLookupHelper {
    private final JiraAuthenticationContext jiraAuthenticationContext;
    private final ApplicationProperties applicationProperties;
    private final IssueManager issueManager;
    private final SearchService searchService;

    public IssueLookupHelperImpl(JiraAuthenticationContext jiraAuthenticationContext, ApplicationProperties applicationProperties, IssueManager issueManager, SearchService searchService) {
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.applicationProperties = applicationProperties;
        this.issueManager = issueManager;
        this.searchService = searchService;
    }

    @Override
    public MutableIssue lookupByKey(String text, long resolvedBefore, MessageHandlerErrorCollector monitor) {
        String pattern = applicationProperties.getString(APKeys.JIRA_PROJECTKEY_PATTERN);
        Timestamp bound = new Timestamp(System.currentTimeMillis() - resolvedBefore);

        if (pattern == null) {
            pattern = applicationProperties.getDefaultString(APKeys.JIRA_PROJECTKEY_PATTERN);
        }

        if (pattern == null) {
            pattern = "[A-Z][A-Z0-9]+";
        }

        Pattern projectKey = Pattern.compile("\\(" + pattern + "-\\d+\\)");

        Matcher matcher = projectKey.matcher(text);

        while (matcher.find()) {
            int start = matcher.start();
            int offset = matcher.end();

            String ticket = text.substring(start + 1, offset - 1);

            try {
                MutableIssue issue = issueManager.getIssueObject(ticket);

                if (issue != null && (resolvedBefore == 0 || issue.getResolutionDate() == null || bound.before(issue.getResolutionDate()))) {
                    return issue;
                }
            } catch (Exception e) {
                monitor.error("Unexpected error", e);
            }
        }

        return null;
    }

    @Override
    public MutableIssue lookupBySubject(Project project, String subject, long resolvedBefore, MessageHandlerErrorCollector monitor) {
        String summary = StringUtils.strip(subject);

        ApplicationUser author = jiraAuthenticationContext.getLoggedInUser();
        String preparedSubject = prepareSummary(summary);
        Predicate<Issue> predicate = e -> summary.equals(StringUtils.strip(e.getSummary()));

        try {
            Query unresolvedQuery = JqlQueryBuilder.newClauseBuilder()
                    .project(project.getId()).and().summary(preparedSubject).and()
                    .unresolved().buildQuery();

            Issue issue = findIssue(author, unresolvedQuery, predicate);

            if (issue == null) {
                JqlClauseBuilder builder = JqlQueryBuilder.newClauseBuilder().project(project.getId()).and().summary(preparedSubject);

                if (resolvedBefore > 0) {
                    builder = builder.and().resolutionDateBetween(new Date(System.currentTimeMillis() - resolvedBefore), new Date());
                }

                Query recentlyResolvedQuery = builder.buildQuery();

                issue = findIssue(author, recentlyResolvedQuery, predicate);

                if (issue == null) {
                    Query movedQuery = JqlQueryBuilder.newClauseBuilder().summary(preparedSubject).buildQuery();

                    issue = findIssue(author, movedQuery, predicate.and(e -> issueManager.getAllIssueKeys(e.getId()).stream().anyMatch(x -> x.startsWith(project.getKey() + "-"))));
                }
            }

            if (issue != null) {
                return issueManager.getIssueObject(issue.getId());
            }
        } catch (SearchException e) {
            monitor.error("Cannot search for an issue.", e);
        }

        return null;
    }

    private Issue findIssue(ApplicationUser user, Query query, Predicate<Issue> predicate) throws SearchException {
        List<Issue> issues = searchService.search(user, query, PagerFilter.getUnlimitedFilter()).getIssues();

        if (issues == null || issues.isEmpty()) {
            return null;
        }

        return issues.stream().filter(predicate).findFirst().orElse(null);
    }

    protected static String prepareSummary(String subject) {
        if (StringUtils.isBlank(subject)) {
            return "";
        }

        boolean whitespace = true;
        StringBuilder out = new StringBuilder(subject.length());

        char[] a = subject.toCharArray();

        for (int i = 0; i < a.length; i++) {
            char ch = a[i];

            if (isSpecial(ch)) {
                if (out.length() != 0) {
                    out.append(' ');
                }
                whitespace = true;
            } else if (isVerySpecial(ch)) {
                int j = i + 1;

                whitespace = whitespace || i == 0 || j == a.length || isWhitespace(a[j]);

                if (whitespace) {
                    if (out.length() != 0 && out.charAt(out.length() - 1) != ' ') {
                        out.append(' ');
                    }
                } else {
                    out.append(ch);
                }
            } else {
                whitespace = isWhitespace(ch);

                if (whitespace) {
                    if (out.length() != 0 && out.charAt(out.length() - 1) != ' ') {
                        out.append(' ');
                    }
                } else {
                    out.append(ch);
                }
            }
        }

        return out.toString();
    }

    private static boolean isVerySpecial(char c) {
        switch (c) {
            case '-':
            case '&':
                return true;
            default:
                return false;
        }
    }

    private static boolean isSpecial(char c) {
        switch (c) {
            case '+':
            case '|':
            case '!':
            case '(':
            case ')':
            case '{':
            case '}':
            case '[':
            case ']':
            case '^':
            case '"':
            case '~':
            case '*':
            case '?':
            case ':':
            case '\\':
                return true;
            default:
                return false;
        }
    }

    private static boolean isWhitespace(char c) {
        return Character.isWhitespace(c) || c == '\u00A0' || c == '\u2007' || c == '\u202F';
    }
}
