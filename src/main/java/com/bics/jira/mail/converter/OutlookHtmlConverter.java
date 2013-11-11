package com.bics.jira.mail.converter;

import com.bics.jira.mail.converter.html.NodeFormatter;
import com.bics.jira.mail.converter.html.Tag;
import com.bics.jira.mail.converter.html.TreeContext;
import com.bics.jira.mail.converter.html.WikiBasic;
import com.bics.jira.mail.converter.html.WikiCell;
import com.bics.jira.mail.converter.html.WikiDirective;
import com.bics.jira.mail.converter.html.WikiHeaderCell;
import com.bics.jira.mail.converter.html.WikiIgnore;
import com.bics.jira.mail.converter.html.WikiInlineImage;
import com.bics.jira.mail.converter.html.WikiLink;
import com.bics.jira.mail.converter.html.WikiList;
import com.bics.jira.mail.converter.html.WikiListContainer;
import com.bics.jira.mail.converter.html.WikiOutlookList;
import com.bics.jira.mail.converter.html.WikiParagraph;
import com.bics.jira.mail.converter.html.WikiTable;
import com.bics.jira.mail.converter.html.WikiTableRow;
import com.bics.jira.mail.converter.html.WikiText;
import com.bics.jira.mail.converter.html.context.TreeContextImpl;
import com.bics.jira.mail.model.mail.Attachment;
import com.bics.jira.mail.model.mail.Body;
import com.bics.jira.mail.model.mail.MessageAdapter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 10.02.13 1:54
 */
public class OutlookHtmlConverter implements BodyConverter {
    private final Collection<NodeFormatter> formatters = Collections.unmodifiableList(Arrays.asList(
            new WikiDirective(),
            new WikiIgnore(),
            new WikiText(),
            new WikiOutlookList(),
            new WikiParagraph(),
            new WikiBasic("*", Tag.STRONG, Tag.B),
            new WikiBasic("_", Tag.EM, Tag.I),
            new WikiBasic("-", Tag.STRIKE),
            new WikiBasic("+", Tag.U),
            new WikiListContainer(), // TODO<victor>: need to test
            new WikiList(),          // TODO<victor>: Outlook has its own lists
            new WikiCell(),
            new WikiHeaderCell(),
            new WikiTableRow(),
            new WikiTable(),
            new WikiInlineImage(),
            new WikiLink()
    ));

    @Override
    public boolean isSupported(MessageAdapter message, boolean stripQuotes) {
        return message.hasThreadTopic();
    }

    @Override
    public Body convert(String body, Collection<Attachment> attachments) {
        Document document = Jsoup.parse(body);

        TreeContextImpl context = new TreeContextImpl(getFormatters(), attachments, document);

        context.content();

        return new Body(context.toString(), context.getAttachments());
    }

    protected Collection<NodeFormatter> getFormatters() {
        return formatters;
    }

}
