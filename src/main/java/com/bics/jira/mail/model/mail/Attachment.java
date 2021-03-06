package com.bics.jira.mail.model.mail;

import javax.mail.internet.ContentType;
import java.io.File;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 13/02/13 07:27
 */
public class Attachment {
    private final File storedFile;
    private final ContentType contentType;
    private final String fileName;
    private final String contentId;
    private final boolean inline;

    public Attachment(File storedFile, ContentType contentType, String fileName, String contentId, boolean inline) {
        this.storedFile = storedFile;
        this.contentType = contentType;
        this.fileName = fileName;
        this.contentId = contentId;
        this.inline = inline;
    }

    public long getSize() {
        return storedFile.length();
    }

    public File getStoredFile() {
        return storedFile;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentId() {
        return contentId;
    }

    public boolean isInline() {
        return inline;
    }
}
