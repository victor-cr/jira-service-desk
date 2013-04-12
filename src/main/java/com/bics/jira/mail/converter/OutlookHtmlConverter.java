package com.bics.jira.mail.converter;

import com.bics.jira.mail.converter.html.WikiNodeVisitor;
import com.bics.jira.mail.model.HandlerModel;
import com.bics.jira.mail.model.MessageAdapter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 10.02.13 1:54
 */
public class OutlookHtmlConverter implements BodyConverter {
    @Override
    public boolean isSupported(HandlerModel model, MessageAdapter message, boolean stripQuotes) {
        return message.hasThreadTopic();
    }

    @Override
    public String convert(String body) {
        Document document = Jsoup.parse(body);

        WikiNodeVisitor visitor = createNodeVisitor();

        document.traverse(visitor);

        return visitor.toString();
    }

    protected WikiNodeVisitor createNodeVisitor() {
        return new WikiNodeVisitor();
    }
}
