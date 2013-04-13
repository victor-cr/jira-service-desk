package com.bics.jira.mail.converter;

import com.bics.jira.mail.converter.html.NodeFormatter;
import com.bics.jira.mail.converter.html.Tag;
import com.bics.jira.mail.converter.html.TreeContext;
import com.bics.jira.mail.converter.html.WikiBasic;
import com.bics.jira.mail.converter.html.WikiCell;
import com.bics.jira.mail.converter.html.WikiHeaderCell;
import com.bics.jira.mail.converter.html.WikiIgnore;
import com.bics.jira.mail.converter.html.WikiInlineImage;
import com.bics.jira.mail.converter.html.WikiLink;
import com.bics.jira.mail.converter.html.WikiList;
import com.bics.jira.mail.converter.html.WikiListContainer;
import com.bics.jira.mail.converter.html.WikiParagraph;
import com.bics.jira.mail.converter.html.WikiTable;
import com.bics.jira.mail.converter.html.WikiTableRow;
import com.bics.jira.mail.converter.html.WikiText;
import com.bics.jira.mail.model.HandlerModel;
import com.bics.jira.mail.model.MessageAdapter;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;

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
//            new WikiList(),          // TODO<victor>: Outlook has its own system
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

        context.appendInner();

        return context.out.toString();
    }

    protected Collection<NodeFormatter> getFormatters() {
        return formatters;
    }

    private static class TreeContextImpl implements TreeContext {
        private final Iterable<NodeFormatter> formatters;
        private final StringBuilder out = new StringBuilder();
        private final Deque<Node> accepted = new LinkedList<Node>();
        private Node current;
        private boolean pendingWhitespace;

        public TreeContextImpl(Iterable<NodeFormatter> formatters, Document document) {
            this.formatters = formatters;
            this.current = document;
        }

        @Override
        public boolean hasChild(Tag tag) {
            return hasChild(tag, current);
        }

        @Override
        public boolean hasParent(final Tag tag) {
            return Iterables.any(accepted, new Predicate<Node>() {
                @Override
                public boolean apply(@Nullable Node node) {
                    return current != node && tag.is(node);
                }
            });
        }

        @Override
        public Iterable<Tag> path() {
            return Iterables.unmodifiableIterable(Iterables.transform(accepted, new Function<Node, Tag>() {
                @Override
                public Tag apply(@Nullable Node input) {
                    return Tag.valueOf(input);
                }
            }));
        }

        @Override
        public TreeContext optional() {
            return new OptionalTreeContextImpl(this);
        }

        @Override
        public TreeContext newLine() {
            return isNewLine() ? this : append("\n");
        }

        @Override
        public TreeContext glue() {
            pendingWhitespace = false;
            return this;
        }

        @Override
        public TreeContext whitespace() {
            pendingWhitespace = true;

            return this;
        }

        @Override
        public TreeContext append(String sequence) {
            if (!StringUtils.isEmpty(sequence)) {
                if (pendingWhitespace && !isWhitespace() && !Character.isWhitespace(sequence.charAt(0))) {
                    out.append(" ");
                }

                out.append(sequence);
                pendingWhitespace = false;
            }

            return this;
        }

        @Override
        public TreeContext appendInner() {
            Node root = current;

            if (current.childNodeSize() != 0) {
                current = current.childNode(0);

                while (current != null) {
                    for (NodeFormatter formatter : formatters) {
                        if (formatter.isSupported(this, current)) {
                            accepted.offerFirst(current);
                            cleanupSpaces();
                            formatter.format(this, current);
                            cleanupSpaces();
                            break;
                        }
                    }

                    if (accepted.peekFirst() != current) {
                        appendInner();
                    } else {
                        accepted.removeFirst();
                    }

                    current = current.nextSibling();
                }

                current = root;
            }
            return this;
        }

        private void cleanupSpaces() {
            int i = out.length() - 1;

            while (i >= 0 && Character.isSpaceChar(out.charAt(i))) {
                i--;
            }

            if (++i != out.length()) {
                out.setLength(i);
            }
        }

        private boolean isNewLine() {
            int length = out.length();

            return length == 0 || out.charAt(length - 1) == '\n' || out.charAt(length - 1) == '\r';
        }

        private boolean isWhitespace() {
            int length = out.length();

            return length == 0 || Character.isWhitespace(out.charAt(length - 1));
        }

        private boolean hasChild(Tag tag, Node node) {
            if (node != current && tag.is(node)) {
                return true;
            }

            for (Node child : node.childNodes()) {
                if (hasChild(tag, child)) {
                    return true;
                }
            }

            return false;
        }
    }

    private static class OptionalTreeContextImpl implements TreeContext {
        private final TreeContextImpl context;
        private final int length;
        private boolean hasContent = true;

        private OptionalTreeContextImpl(TreeContextImpl context) {
            this.context = context;
            this.length = context.out.length();
        }

        @Override
        public boolean hasChild(Tag tag) {
            return context.hasChild(tag);
        }

        @Override
        public boolean hasParent(Tag tag) {
            return context.hasParent(tag);
        }

        @Override
        public Iterable<Tag> path() {
            return context.path();
        }

        @Override
        public TreeContext optional() {
            if (!hasContent) {
                return this;
            }

            return context.optional();
        }

        @Override
        public TreeContext newLine() {
            if (hasContent) {
                context.newLine();
            }

            return this;
        }

        @Override
        public TreeContext whitespace() {
            if (hasContent) {
                context.whitespace();
            }

            return this;
        }

        @Override
        public TreeContext glue() {
            if (hasContent) {
                context.glue();
            }

            return this;
        }

        @Override
        public TreeContext append(String sequence) {
            if (hasContent) {
                context.append(sequence);
            }

            return this;
        }

        @Override
        public TreeContext appendInner() {
            if (hasContent) {
                int initialLength = context.out.length();

                context.appendInner();

                if (context.out.length() <= initialLength) {
                    hasContent = false;

                    context.out.setLength(length);
                }
            }

            return this;
        }
    }
}
