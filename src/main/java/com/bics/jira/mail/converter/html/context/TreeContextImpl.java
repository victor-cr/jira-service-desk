package com.bics.jira.mail.converter.html.context;

import com.bics.jira.mail.converter.html.NodeFormatter;
import com.bics.jira.mail.converter.html.Tag;
import com.bics.jira.mail.converter.html.TreeContext;
import com.bics.jira.mail.helper.AttachmentPredicate;
import com.bics.jira.mail.model.mail.Attachment;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 17/04/13 13:54
 */
public class TreeContextImpl implements TreeContext {
    final StringBuilder out = new StringBuilder();
    private final Iterable<NodeFormatter> formatters;
    private final Deque<Node> accepted = new LinkedList<Node>();
    private final Map<String, Attachment> inlineAttachments;
    private final Set<Attachment> attachments;
    private Node current;
    private boolean pendingWhitespace;
    private boolean active = true;

    public TreeContextImpl(Iterable<NodeFormatter> formatters, Iterable<Attachment> attachments, Document document) {
        this.formatters = formatters;
        this.current = document;
        this.attachments = Sets.newHashSet(Iterables.filter(attachments, new AttachmentPredicate(false)));
        this.inlineAttachments = Maps.uniqueIndex(Iterables.filter(attachments, new AttachmentPredicate(true)), new Function<Attachment, String>() {
            @Override
            public String apply(@Nullable Attachment input) {
                return input != null && input.isInline() ? input.getContentId() : null;
            }
        });
    }

    public Collection<Attachment> getAttachments() {
        return Lists.newArrayList(attachments);
    }

    @Override
    public void stop() {
        active = false;
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
    public Checkpoint checkpoint() {
        return new SimpleCheckpoint(out.length(), pendingWhitespace);
    }

    @Override
    public String getInlineName(String cid) {
        Attachment attachment = inlineAttachments.get(cid);

        if (attachment == null) {
            return null;
        }

        attachments.add(attachment);

        return attachment.getFileName();
    }

    @Override
    public TreeContext newLine() {
        if (active && !isNewLine()) {
            out.append('\n');
        }

        return this;
    }

    @Override
    public TreeContext whitespace() {
        pendingWhitespace = true;

        return this;
    }

    @Override
    public TreeContext symbol(String sequence) {
        return text(sequence);
    }

    @Override
    public TreeContext text(String sequence) {
        if (active && !StringUtils.isEmpty(sequence)) {
            sequence = StringUtils.replaceChars(sequence, "\n\r\u00A0\u2007\u202F", "     ");
            sequence = StringUtils.trimToEmpty(sequence);

            if (pendingWhitespace && !isWhitespace() && !Character.isWhitespace(sequence.charAt(0))) {
                out.append(" ");
            }

            out.append(sequence);
            pendingWhitespace = false;
        }

        return this;
    }

    @Override
    public TreeContext content() {
        Node root = current;

        if (active && current.childNodeSize() != 0) {
            current = current.childNode(0);

            while (active && current != null) {
                for (NodeFormatter formatter : formatters) {
                    if (formatter.isSupported(this, current)) {
                        accepted.offerFirst(current);
//                        cleanupSpaces();
                        formatter.format(this, current);
//                        cleanupSpaces();
                        break;
                    }
                }

                if (accepted.peekFirst() != current) {
                    this.content();
                } else {
                    accepted.removeFirst();
                }

                current = current.nextSibling();
            }

            current = root;
        }

        return this;
    }

    @Override
    public TreeContext trimContent() {
        if (active) {
            if (pendingWhitespace) {
                out.append(' ');
            }

            int len = out.length();

            pendingWhitespace = false;
            content();
            pendingWhitespace = false;

            String content = out.substring(len);
            String trimmed = StringUtils.trimToEmpty(content);

            if (!content.equals(trimmed)) {
                out.setLength(len);
                out.append(trimmed);
            }
        }

        return this;
    }

    @Override
    public String toString() {
        return out.toString();
    }

    private boolean isNewLine() {
        int length = out.length();

        return length == 0 || length > 1 && out.charAt(length - 1) == '\n' && out.charAt(length - 2) == '\n';
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

    private final class SimpleCheckpoint implements Checkpoint {
        private final int length;
        private final boolean whitespace;

        private SimpleCheckpoint(int length, boolean pendingWhitespace) {
            this.length = length;
            this.whitespace = pendingWhitespace;
        }

        @Override
        public String diff() {
            return length >= out.length() ? "" : out.substring(length);
        }

        @Override
        public void rollback() {
            if (length < out.length()) {
                pendingWhitespace = whitespace;
                out.setLength(length);
            }
        }
    }
}
