package com.bics.jira.mail.converter;

import com.bics.jira.mail.converter.html.NodeFormatter;
import com.bics.jira.mail.converter.html.Tag;
import com.bics.jira.mail.converter.html.WikiBasic;
import com.bics.jira.mail.converter.html.WikiCell;
import com.bics.jira.mail.converter.html.WikiHeaderCell;
import com.bics.jira.mail.converter.html.WikiIgnore;
import com.bics.jira.mail.converter.html.WikiInlineImage;
import com.bics.jira.mail.converter.html.WikiLink;
import com.bics.jira.mail.converter.html.WikiParagraph;
import com.bics.jira.mail.converter.html.WikiTable;
import com.bics.jira.mail.converter.html.WikiTableRow;
import com.bics.jira.mail.converter.html.WikiText;
import com.bics.jira.mail.converter.html.context.TreeContextImpl;
import com.bics.jira.mail.model.HandlerModel;
import com.bics.jira.mail.model.MessageAdapter;
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
            new WikiIgnore(),
            new WikiText(),
            new WikiParagraph(),
            new WikiBasic("*", Tag.STRONG, Tag.B),
            new WikiBasic("_", Tag.EM, Tag.I),
            new WikiBasic("-", Tag.STRIKE),
            new WikiBasic("+", Tag.U),
//            new WikiListContainer(), // TODO<victor>: need to test
//            new WikiList(),          // TODO<victor>: Outlook has its own lists
            new WikiCell(),
            new WikiHeaderCell(),
            new WikiTableRow(),
            new WikiTable(),
            new WikiInlineImage(),
            new WikiLink()
    ));

    @Override
    public boolean isSupported(HandlerModel model, MessageAdapter message, boolean stripQuotes) {
        return message.hasThreadTopic();
    }

    @Override
    public String convert(String body) {
        Document document = Jsoup.parse(body);

        TreeContextImpl context = new TreeContextImpl(getFormatters(), document);

        context.content();

        return context.toString();
    }

    protected Collection<NodeFormatter> getFormatters() {
        return formatters;
    }

}
