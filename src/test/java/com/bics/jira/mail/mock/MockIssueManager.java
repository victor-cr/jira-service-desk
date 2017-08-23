package com.bics.jira.mail.mock;

import com.atlassian.jira.event.type.EventDispatchOption;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.exception.DataAccessException;
import com.atlassian.jira.exception.RemoveException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.UpdateIssueRequest;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.lang.Pair;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public Issue updateIssue(ApplicationUser applicationUser, MutableIssue mutableIssue, UpdateIssueRequest updateIssueRequest) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public long getIssueCount() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Nonnull
    @Override
    public Set<Pair<Long, String>> getProjectIssueTypePairsByKeys(Set<String> set) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Nonnull
    @Override
    public Set<Pair<Long, String>> getProjectIssueTypePairsByIds(Set<Long> set) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Nonnull
    @Override
    public Set<String> getKeysOfMissingIssues(Set<String> set) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Nonnull
    @Override
    public Set<Long> getIdsOfMissingIssues(Set<Long> set) {
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
    public boolean atLeastOneIssueExists() {
        return !issuesByKey.isEmpty();
    }

    @Override
    public MutableIssue getIssueObject(Long aLong) throws DataAccessException {
        for (Issue issue : issuesByKey.values()) {
            if (issue.getId().equals(aLong)) {
                return (MutableIssue) issue;
            }
        }

        return null;
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
    public List<ApplicationUser> getWatchers(Issue issue) {
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
    public GenericValue createIssue(ApplicationUser applicationUser, Map<String, Object> map) throws CreateException {
        return null;
    }

    @Override
    public Issue createIssueObject(ApplicationUser user, Map<String, Object> stringObjectMap) throws CreateException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public GenericValue createIssue(ApplicationUser user, Issue issue) throws CreateException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Issue createIssueObject(ApplicationUser user, Issue issue) throws CreateException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Issue updateIssue(ApplicationUser user, MutableIssue mutableIssue, EventDispatchOption eventDispatchOption, boolean b) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void deleteIssue(ApplicationUser user, Issue issue, EventDispatchOption eventDispatchOption, boolean b) throws RemoveException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void deleteIssue(ApplicationUser user, MutableIssue mutableIssue, EventDispatchOption eventDispatchOption, boolean b) throws RemoveException {
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
    public boolean isEditable(Issue issue, ApplicationUser user) {
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

    @Override
    public boolean isExistingIssueKey(String s) throws GenericEntityException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public MutableIssue getIssueByKeyIgnoreCase(String s) throws DataAccessException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public MutableIssue getIssueByCurrentKey(String s) throws DataAccessException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<Issue> getVotedIssues(ApplicationUser applicationUser) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<Issue> getVotedIssuesOverrideSecurity(ApplicationUser applicationUser) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<ApplicationUser> getWatchersFor(Issue issue) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<Issue> getWatchedIssues(ApplicationUser applicationUser) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<Issue> getWatchedIssuesOverrideSecurity(ApplicationUser applicationUser) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Set<String> getAllIssueKeys(Long aLong) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Issue findMovedIssue(String s) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void recordMovedIssueKey(Issue issue) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
