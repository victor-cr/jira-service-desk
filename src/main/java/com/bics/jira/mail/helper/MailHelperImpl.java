package com.bics.jira.mail.helper;

import com.bics.jira.mail.MailHelper;
import com.bics.jira.mail.converter.BodyConverter;
import com.bics.jira.mail.converter.DefaultHtmlConverter;
import com.bics.jira.mail.converter.DefaultTextConverter;
import com.bics.jira.mail.converter.OutlookHtmlConverter;
import com.bics.jira.mail.converter.StripQuotesOutlookHtmlConverter;
import com.bics.jira.mail.converter.StripQuotesTextConverter;
import com.bics.jira.mail.model.mail.MessageAdapter;
import org.apache.commons.lang.StringUtils;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 13/02/13 01:32
 */
public class MailHelperImpl implements MailHelper {
    private final Collection<? extends BodyConverter> htmlConverters;
    private final Collection<? extends BodyConverter> textConverters;

    public MailHelperImpl() throws IOException {
        htmlConverters = Arrays.asList(
                new StripQuotesOutlookHtmlConverter(),
                new OutlookHtmlConverter(),
                new DefaultHtmlConverter()
        );
        textConverters = Arrays.asList(
                new StripQuotesTextConverter(),
                new DefaultTextConverter()
        );
    }

    @Override
    public String extract(MessageAdapter message, boolean stripQuotes) throws MessagingException {
        String text = message.getHtmlTextBody();

        if (StringUtils.isBlank(text)) {
            text = message.getPlainTextBody();

            return StringUtils.isBlank(text) ? "" : get(message, stripQuotes, textConverters).convert(text);
        }

        return get(message, stripQuotes, htmlConverters).convert(text);
    }

    private BodyConverter get(MessageAdapter message, boolean stripQuotes, Collection<? extends BodyConverter> converters) {
        for (BodyConverter converter : converters) {
            if (converter.isSupported(message, stripQuotes)) {
                return converter;
            }
        }

        return null;
    }
}
