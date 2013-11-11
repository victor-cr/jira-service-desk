package com.bics.jira.mail.model.mail;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Collection;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 11/11/13 11:39
 */
public class Body {
    private final String body;
    private final Collection<Attachment> used;

    public Body(String body, Collection<Attachment> used) {
        this.body = body;
        this.used = used;
    }

    public String getBody() {
        return body;
    }

    public Collection<Attachment> getUsed() {
        return used;
    }

    @Override
    public boolean equals(Object that) {
        return this == that || that != null && that.getClass() == this.getClass() && equals((Body) that);
    }

    public boolean equals(Body that) {
        return this == that || that != null && new EqualsBuilder().append(body, that.body).append(used, that.used).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(body).append(used).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("body", body).append("used", used).toString();
    }
}
