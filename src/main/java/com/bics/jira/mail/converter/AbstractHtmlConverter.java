package com.bics.jira.mail.converter;

import com.bics.jira.mail.model.MimeType;
import net.htmlparser.jericho.Renderer;
import net.htmlparser.jericho.Source;

import javax.mail.Message;
import java.util.Collection;
import java.util.Collections;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 10.02.13 1:54
 */
public abstract class AbstractHtmlConverter implements BodyConverter {
    @Override
    public boolean isSupported(Message message, MimeType mimeType) {
        return mimeType == MimeType.HTML;
    }

    @Override
    public Collection<String> convert(String body) {
        Source source = new Source(body);

        Renderer renderer = source.getRenderer();

        renderer.setConvertNonBreakingSpaces(true).setDecorateFontStyles(true).setIncludeHyperlinkURLs(true);

        return Collections.singleton(renderer.toString());
    }
}
