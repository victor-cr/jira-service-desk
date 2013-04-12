package com.bics.jira.mail.converter.html;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.nodes.XmlDeclaration;
import org.jsoup.select.NodeVisitor;

import java.util.Stack;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 12/04/13 12:10
 */
public class WikiNodeVisitor implements NodeVisitor {
    protected final StringBuilder out = new StringBuilder();
    protected final Stack<Entry> stack = new Stack<Entry>();
    protected Node ignored;

    @Override
    public void head(Node node, int depth) {
        if (isIgnored(node)) {
            setIgnored(null, node);
            return;
        }

        if (printIfText(node) || inlineImage(node)) {
            return;
        }

        Wiki wiki = match(node);

        if (wiki == null) {
            return;
        }

        stack.push(new Entry(node, wiki, out.length()));

        if (wiki.isIgnoreOuter() && hasInner(node, node)) {
            return;
        }

        if (wiki.isIgnoreInner() && hasOuter(node)) {
            return;
        }

        out.append(wiki.getStart());
    }

    @Override
    public void tail(Node node, int depth) {
        if (isIgnored(node) || isText(node)) {
            setIgnored(node, null);
            return;
        }

        if (stack.isEmpty() || stack.peek().node != node) {
            return;
        }

        Entry entry = stack.pop();

        Wiki wiki = entry.wiki;

        if (wiki.isOptional() && entry.length + wiki.getStart().length() >= out.length()) {
            out.setLength(entry.length);
        } else {
            out.append(wiki.getStop());

            if (wiki.isOnNewLine()) {
                out.append("\n");
            }
        }
    }

    @Override
    public String toString() {
        return out.toString();
    }

    protected Wiki match(Node node) {
        Tag tag = Tag.valueOf(node);

        switch (tag) {
            case STRONG:
            case B:
                return Wiki.BOLD;
            case BR:
            case P:
                return Wiki.PARAGRAPH;
            case EM:
            case I:
                return Wiki.ITALIC;
            case U:
                return Wiki.UNDERSCORE;
            case STRIKE:
                return Wiki.STRIKE;
            case TR:
                return Wiki.TABLE_ROW;
            case TD:
                return Wiki.TABLE_CELL;
            case TH:
                return Wiki.TABLE_HEADER;
            case LI:
                return Tag.OL.is(node.parent()) ? Wiki.SEQUENCE_ITEM : Wiki.LIST_ITEM;
            default:
                return null;
        }
    }

    protected void setIgnored(Node condition, Node value) {
        if (ignored == condition) {
            ignored = value;
        }
    }

    protected boolean isText(Node node) {
        return node instanceof TextNode;
    }

    protected boolean isComment(Node node) {
        return node instanceof Comment;
    }

    protected boolean isDirective(Node node) {
        return node instanceof XmlDeclaration || node instanceof DocumentType;
    }

    protected boolean isIgnored(Node node) {
        return ignored != null && ignored != node
                || Tag.SCRIPT.is(node) || Tag.STYLE.is(node) || Tag.HEAD.is(node) || isComment(node) || isDirective(node);
    }

    protected boolean printIfText(Node node) {
        if (isText(node)) {
            String text = ((TextNode) node).text();

            if (StringUtils.isNotBlank(text)) {
                out.append(StringUtils.stripToEmpty(text.replace('\u00A0', ' ').replace('\u2007', ' ').replace('\u00A0', ' ')));
            }

            return true;
        }

        return false;
    }

    protected boolean inlineImage(Node node) {
        if (!"img".equalsIgnoreCase(node.nodeName())) {
            return false;
        }

        String src = node.attr("src");

        if (StringUtils.isBlank(src) || !StringUtils.startsWithIgnoreCase(src, "cid:")) {
            return false;
        }

        String width = node.attr("width");
        String height = node.attr("height");

        out.append('!').append(StringUtils.substringBeforeLast(src.substring(4), "@"));

        if (StringUtils.isNotBlank(width) || StringUtils.isNotBlank(height)) {
            out.append('|');

            if (StringUtils.isNotBlank(width)) {
                out.append("width=").append(width).append(',');
            }

            if (StringUtils.isNotBlank(height)) {
                out.append("height=").append(height).append(',');
            }

            out.setLength(out.length() - 1);
        }

        out.append('!');

        return true;
    }

    protected boolean hasInner(Node root, Node node) {
        if (root != node && isSame(root, node)) {
            return true;
        }

        for (Node child : node.childNodes()) {
            if (hasInner(root, child)) {
                return true;
            }
        }

        return false;
    }

    protected boolean hasOuter(Node node) {
        for (Node parent = node.parent(); parent != null; parent = parent.parent()) {
            if (isSame(node, parent)) {
                return true;
            }

        }

        return false;
    }

    protected boolean isSame(Node left, Node right) {
        return left != null && right != null && left.nodeName().equalsIgnoreCase(right.nodeName());
    }

    protected static class Entry {
        public final Node node;
        public final Wiki wiki;
        public final int length;

        public Entry(Node node, Wiki wiki, int length) {
            this.node = node;
            this.wiki = wiki;
            this.length = length;
        }
    }
}
