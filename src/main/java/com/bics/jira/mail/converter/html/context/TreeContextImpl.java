package com.bics.jira.mail.converter.html.context;

import com.bics.jira.mail.converter.html.NodeFormatter;
import com.bics.jira.mail.converter.html.Tag;
import com.bics.jira.mail.converter.html.TreeContext;
import com.bics.jira.mail.model.mail.Attachment;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 17/04/13 13:54
 */
public class TreeContextImpl implements TreeContext {
    private final StringBuilder out = new StringBuilder();

    private final Iterable<NodeFormatter> formatters;
    private final Deque<Node> accepted = new LinkedList<>();
    private final Map<String, Attachment> inlineAttachments;
    private final Set<Attachment> attachments;
    private Node current;
    private boolean pendingWhitespace = false;
    private boolean ignore = false;
    private boolean formatted = false;
    private boolean active = true;

    public TreeContextImpl(Iterable<NodeFormatter> formatters, Iterable<Attachment> attachments, Document document) {
        this.formatters = formatters;
        this.current = document;
        this.attachments = StreamSupport.stream(attachments.spliterator(), false).filter(e -> e != null && !e.isInline()).collect(Collectors.toSet());
        this.inlineAttachments = StreamSupport.stream(attachments.spliterator(), false).filter(e -> e != null && e.isInline()).collect(Collectors.toMap(Attachment::getContentId, e -> e, (x, y) -> x));
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
    public boolean hasParent(Tag tag) {
        return accepted.stream().anyMatch(e -> current != e && tag.is(e));
    }

    @Override
    public Iterable<Tag> path() {
        return Iterables.unmodifiableIterable(accepted.stream().map(Tag::valueOf).collect(Collectors.toList()));
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
    public TreeContext ignore(boolean ignore) {
        this.ignore = ignore;

        return this;
    }

    @Override
    public TreeContext formatted(boolean formatted) {
        this.formatted = formatted;

        return this;
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
        if (active && !ignore && StringUtils.isNotEmpty(sequence)) {
            sequence = StringUtils.replaceChars(sequence, "\u00A0\u2007\u202F", "   ");
            sequence = StringUtils.replaceChars(sequence, "\u0085\u2028\u2029", "\n\n\n");
            sequence = StringUtils.replace(sequence, "\r\n", "\n");

            if (!formatted) {
                sequence = StringUtils.replaceChars(sequence, "\n\r", "  ");
                sequence = StringUtils.stripToEmpty(sequence);
            }

            if (pendingWhitespace && !isWhitespace() && StringUtils.isNotEmpty(sequence) && !Character.isWhitespace(sequence.charAt(0))) {
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
                        formatter.format(this, current);
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
            String trimmed = StringUtils.stripToEmpty(content);

            if (!ignore && !content.equals(trimmed)) {
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
