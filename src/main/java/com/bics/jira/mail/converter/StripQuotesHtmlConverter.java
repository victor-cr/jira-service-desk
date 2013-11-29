package com.bics.jira.mail.converter;

import com.bics.jira.mail.converter.html.IgnoreIPhoneQuote;
import com.bics.jira.mail.converter.html.IgnoreOutlookQuote;
import com.bics.jira.mail.converter.html.IgnoreWindowsPhoneQuote;
import com.bics.jira.mail.converter.html.NodeFormatter;
import com.bics.jira.mail.converter.html.PlainAnswerIgnore;
import com.bics.jira.mail.converter.html.WikiText;
import com.bics.jira.mail.model.mail.MessageAdapter;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 10.02.13 1:54
 */
public class StripQuotesHtmlConverter extends OutlookHtmlConverter implements BodyConverter {
    private final Collection<NodeFormatter> formatters;

    public StripQuotesHtmlConverter() {
        List<NodeFormatter> formatters = new LinkedList<NodeFormatter>(super.getFormatters());

        Iterables.removeIf(formatters, new Predicate<NodeFormatter>() {
            @Override
            public boolean apply(@Nullable NodeFormatter input) {
                return input instanceof WikiText;
            }
        });

        formatters.add(new PlainAnswerIgnore());
        formatters.add(new IgnoreOutlookQuote());
        formatters.add(new IgnoreIPhoneQuote());
        formatters.add(new IgnoreWindowsPhoneQuote());

        this.formatters = formatters;
    }

    @Override
    public boolean isSupported(MessageAdapter message, boolean stripQuotes) {
        return stripQuotes && super.isSupported(message, stripQuotes);
    }

    @Override
    protected Collection<NodeFormatter> getFormatters() {
        return formatters;
    }
}
