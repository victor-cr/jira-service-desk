package com.bics.jira.mail.converter.html;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 12/04/13 12:15
 */
public enum Wiki {
    BOLD("*", "*", true, false, false, true),
    ITALIC("_", "_", true, false, false, true),
    UNDERSCORE("+", "+", true, false, false, true),
    STRIKE("-", "-", true, false, false, true),
    PARAGRAPH("", "", false, true, false, false),
    TABLE_ROW("", "|", true, true, true, false),
    TABLE_HEADER("||", "", true, false, true, false),
    TABLE_CELL("|", "", true, false, true, false),
    LIST_ITEM("*", "", true, true, false, false),
    SEQUENCE_ITEM("#", "", true, true, false, false);

    private final String start;
    private final String stop;
    private final boolean optional;
    private final boolean onNewLine;
    private final boolean ignoreOuter;
    private final boolean ignoreInner;

    private Wiki(String start, String stop, boolean optional, boolean onNewLine, boolean ignoreOuter, boolean ignoreInner) {
        this.start = start;
        this.stop = stop;
        this.optional = optional;
        this.onNewLine = onNewLine;
        this.ignoreOuter = ignoreOuter;
        this.ignoreInner = ignoreInner;
    }

    public String getStart() {
        return start;
    }

    public String getStop() {
        return stop;
    }

    public boolean isOptional() {
        return optional;
    }

    public boolean isOnNewLine() {
        return onNewLine;
    }

    public boolean isIgnoreOuter() {
        return ignoreOuter;
    }

    public boolean isIgnoreInner() {
        return ignoreInner;
    }
}
