package com.bics.jira.mail.mock;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchContext;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.issue.search.searchers.util.RecursiveClauseVisitor;
import com.atlassian.jira.jql.context.QueryContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.MessageSet;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;
import com.atlassian.query.clause.TerminalClause;
import com.atlassian.query.operand.EmptyOperand;
import com.atlassian.query.operand.FunctionOperand;
import com.atlassian.query.operand.MultiValueOperand;
import com.atlassian.query.operand.OperandVisitor;
import com.atlassian.query.operand.SingleValueOperand;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 21/07/13 13:36
 */
public class MockSearchService implements SearchService {
    private final Set<Issue> issues = new HashSet<Issue>();

    public void set(Issue issue) {
        issues.add(issue);
    }

    @Override
    public SearchResults search(ApplicationUser user, Query query, PagerFilter pagerFilter) throws SearchException {
        SummaryClauseVisitor summaryClauseVisitor = new SummaryClauseVisitor();

        for (Issue issue : issues) {
            query.getWhereClause().accept(summaryClauseVisitor);

            if (issue.getSummary().equals(summaryClauseVisitor.summary)) {
                return new SearchResults(Collections.singletonList(issue), pagerFilter);
            }
        }

        return new SearchResults(Collections.emptyList(), pagerFilter);
    }

    @Override
    public SearchResults searchOverrideSecurity(ApplicationUser applicationUser, Query query, PagerFilter pagerFilter) throws SearchException {
        return null;
    }

    @Override
    public long searchCount(ApplicationUser applicationUser, Query query) throws SearchException {
        return 0;
    }

    @Override
    public long searchCountOverrideSecurity(ApplicationUser applicationUser, Query query) throws SearchException {
        return 0;
    }

    @Deprecated
    @Override
    public String getQueryString(ApplicationUser applicationUser, Query query) {
        return null;
    }

    @Nonnull
    @Override
    public String getIssueSearchPath(ApplicationUser applicationUser, @Nonnull IssueSearchParameters issueSearchParameters) {
        return null;
    }

    @Override
    public ParseResult parseQuery(ApplicationUser applicationUser, String s) {
        return null;
    }

    @Nonnull
    @Override
    public MessageSet validateQuery(ApplicationUser applicationUser, @Nonnull Query query) {
        return null;
    }

    @Nonnull
    @Override
    public MessageSet validateQuery(ApplicationUser applicationUser, @Nonnull Query query, Long aLong) {
        return null;
    }

    @Override
    public boolean doesQueryFitFilterForm(ApplicationUser applicationUser, Query query) {
        return false;
    }

    @Override
    public QueryContext getQueryContext(ApplicationUser applicationUser, Query query) {
        return null;
    }

    @Override
    public QueryContext getSimpleQueryContext(ApplicationUser applicationUser, Query query) {
        return null;
    }

    @Override
    public SearchContext getSearchContext(ApplicationUser applicationUser, Query query) {
        return null;
    }

    @Override
    public String getJqlString(Query query) {
        return null;
    }

    @Override
    public String getGeneratedJqlString(Query query) {
        return null;
    }

    @Override
    public Query sanitiseSearchQuery(ApplicationUser applicationUser, Query query) {
        return null;
    }

    private static class SummaryClauseVisitor extends RecursiveClauseVisitor {
        private String summary;

        @Override
        public Void visit(TerminalClause clause) {
            if ("summary".equals(clause.getName())) {
                summary = clause.getOperand().accept(new SummaryOperandVisitor());
            }

            return super.visit(clause);
        }
    }

    private static class SummaryOperandVisitor implements OperandVisitor<String> {
        @Override
        public String visit(EmptyOperand emptyOperand) {
            return null;
        }

        @Override
        public String visit(FunctionOperand functionOperand) {
            return null;
        }

        @Override
        public String visit(MultiValueOperand multiValueOperand) {
            return null;
        }

        @Override
        public String visit(SingleValueOperand singleValueOperand) {
            String value = singleValueOperand.getStringValue();

            return value == null || value.length() < 2 ? "" : value;
        }
    }
}
