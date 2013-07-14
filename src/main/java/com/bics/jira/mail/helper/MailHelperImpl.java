package com.bics.jira.mail.helper;

import com.bics.jira.mail.MailHelper;
import com.bics.jira.mail.converter.BodyConverter;
import com.bics.jira.mail.converter.DefaultHtmlConverter;
import com.bics.jira.mail.converter.DefaultTextConverter;
import com.bics.jira.mail.converter.OutlookHtmlConverter;
import com.bics.jira.mail.converter.StripQuotesOutlookHtmlConverter;
import com.bics.jira.mail.converter.StripQuotesTextConverter;
import com.bics.jira.mail.model.CreateOrCommentModel;
import com.bics.jira.mail.model.ServiceDeskModel;
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
    public String extractComment(ServiceDeskModel model, MessageAdapter message) throws MessagingException {
        return extract(model, message, model.isStripQuotes());
    }

    @Override
    public String extractBody(ServiceDeskModel model, MessageAdapter message) throws MessagingException {
        return extract(model, message, false);
    }

    private String extract(ServiceDeskModel model, MessageAdapter message, boolean stripQuotes) throws MessagingException {
        String text = message.getHtmlTextBody();

        if (StringUtils.isBlank(text)) {
            text = message.getPlainTextBody();

            return StringUtils.isBlank(text) ? "" : get(model, message, stripQuotes, textConverters).convert(text);
        }

        return get(model, message, stripQuotes, htmlConverters).convert(text);
    }

    private BodyConverter get(ServiceDeskModel model, MessageAdapter message, boolean stripQuotes, Collection<? extends BodyConverter> converters) {
        for (BodyConverter converter : converters) {
            if (converter.isSupported(model, message, stripQuotes)) {
                return converter;
            }
        }

        return null;
    }
}
