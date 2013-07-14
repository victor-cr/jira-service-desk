package com.bics.jira.mail.mock;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.event.type.EventDispatchOption;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.exception.DataAccessException;
import com.atlassian.jira.exception.RemoveException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.mock.issue.MockIssue;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 14/07/13 19:42
 */
public class MockIssueManager implements IssueManager {
    private Map<String, Issue> issuesByKey = new HashMap<String, Issue>();

    public void set(String key, Issue issue) {
        issuesByKey.put(key, issue);
    }

    @Override
    public GenericValue getIssue(Long aLong) throws DataAccessException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public GenericValue getIssue(String s) throws GenericEntityException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public GenericValue getIssueByWorkflow(Long aLong) throws GenericEntityException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public MutableIssue getIssueObjectByWorkflow(Long aLong) throws GenericEntityException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public MutableIssue getIssueObject(Long aLong) throws DataAccessException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public MutableIssue getIssueObject(String key) throws DataAccessException {
        return (MutableIssue) issuesByKey.get(key);
    }

    @Override
    public List<GenericValue> getIssues(Collection<Long> longs) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<Issue> getIssueObjects(Collection<Long> longs) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<Issue> getVotedIssues(User user) throws GenericEntityException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<Issue> getVotedIssuesOverrideSecurity(User user) throws GenericEntityException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<User> getWatchers(Issue issue) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<Issue> getWatchedIssues(User user) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<Issue> getWatchedIssuesOverrideSecurity(User user) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<GenericValue> getEntitiesByIssue(String s, GenericValue genericValue) throws GenericEntityException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<GenericValue> getEntitiesByIssueObject(String s, Issue issue) throws GenericEntityException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<GenericValue> getIssuesByEntity(String s, GenericValue genericValue) throws GenericEntityException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<Issue> getIssueObjectsByEntity(String s, GenericValue genericValue) throws GenericEntityException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public GenericValue createIssue(String s, Map<String, Object> stringObjectMap) throws CreateException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Issue createIssueObject(String s, Map<String, Object> stringObjectMap) throws CreateException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public GenericValue createIssue(User user, Map<String, Object> stringObjectMap) throws CreateException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Issue createIssueObject(User user, Map<String, Object> stringObjectMap) throws CreateException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public GenericValue createIssue(User user, Issue issue) throws CreateException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Issue createIssueObject(User user, Issue issue) throws CreateException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Issue updateIssue(User user, MutableIssue mutableIssue, EventDispatchOption eventDispatchOption, boolean b) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void deleteIssue(User user, Issue issue, EventDispatchOption eventDispatchOption, boolean b) throws RemoveException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void deleteIssue(User user, MutableIssue mutableIssue, EventDispatchOption eventDispatchOption, boolean b) throws RemoveException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void deleteIssueNoEvent(Issue issue) throws RemoveException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void deleteIssueNoEvent(MutableIssue mutableIssue) throws RemoveException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<GenericValue> getProjectIssues(GenericValue genericValue) throws GenericEntityException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean isEditable(Issue issue) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean isEditable(Issue issue, User user) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Collection<Long> getIssueIdsForProject(Long aLong) throws GenericEntityException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public long getIssueCountForProject(Long aLong) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean hasUnassignedIssues() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public long getUnassignedIssueCount() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
