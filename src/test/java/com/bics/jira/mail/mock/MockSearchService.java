package com.bics.jira.mail.mock;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.issue.search.SearchContext;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.context.QueryContext;
import com.atlassian.jira.util.MessageSet;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 21/07/13 13:36
 */
public class MockSearchService implements SearchService {
    @Override
    public SearchResults search(User user, Query query, PagerFilter pagerFilter) throws SearchException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long searchCount(User user, Query query) throws SearchException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getQueryString(User user, Query query) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ParseResult parseQuery(User user, String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MessageSet validateQuery(User user, Query query) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean doesQueryFitFilterForm(User user, Query query) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public QueryContext getQueryContext(User user, Query query) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public QueryContext getSimpleQueryContext(User user, Query query) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SearchContext getSearchContext(User user, Query query) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getJqlString(Query query) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getGeneratedJqlString(Query query) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Query sanitiseSearchQuery(User user, Query query) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
