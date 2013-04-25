package com.bics.jira.mail.converter.html.context;

import com.bics.jira.mail.converter.html.NodeFormatter;
import com.bics.jira.mail.converter.html.Tag;
import com.bics.jira.mail.converter.html.TreeContext;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

import javax.annotation.Nullable;
import java.util.Deque;
import java.util.LinkedList;

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
    private Node current;
    private boolean pendingWhitespace;
    private boolean active = true;

    public TreeContextImpl(Iterable<NodeFormatter> formatters, Document document) {
        this.formatters = formatters;
        this.current = document;
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
    public TreeContext optional() {
        return new OptionalTreeContext(this);
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
    public TreeContext nowrap() {
        return new NowrapTreeContext(this);
    }

    @Override
    public TreeContext whitespace() {
        pendingWhitespace = true;

        return this;
    }

    @Override
    public TreeContext append(String sequence) {
        if (active && !StringUtils.isEmpty(sequence)) {
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

            while (active && current != null) {
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

    @Override
    public String toString() {
        return out.toString();
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
