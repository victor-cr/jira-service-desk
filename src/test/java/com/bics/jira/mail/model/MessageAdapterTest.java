package com.bics.jira.mail.model;

import org.junit.Assert;
import org.junit.Test;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 06.02.13 22:36
 */
public class MessageAdapterTest extends Assert {
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String FILE_MULTIPART_SIMPLE = "MultipartSimple.txt";
    private static final String FILE_MULTIPART_REGULAR = "MultipartRegular.txt";
    private static final String FILE_TORTURE_TEST = "InfamousTortureTest.txt";

    @Test
    public void testGetSubject_MultipartSimple() throws MessagingException {
        MessageAdapter adapter = message(FILE_MULTIPART_SIMPLE);

        assertEquals("[MAILINATOR] Not important", adapter.getSubject());
    }

    @Test
    public void testGetPlainTextBody_MultipartSimple() throws MessagingException {
        MessageAdapter adapter = message(FILE_MULTIPART_SIMPLE);

        assertEquals("Hello World", trim(adapter.getPlainTextBody()));
    }

    @Test
    public void testGetHtmlTextBody_MultipartSimple() throws MessagingException {
        MessageAdapter adapter = message(FILE_MULTIPART_SIMPLE);

        assertEquals("<div dir=\"ltr\"><br><div>Hello World</div><div><br></div></div>", trim(adapter.getHtmlTextBody()));
    }

    @Test
    public void testGetSubject_MultipartRegular() throws MessagingException {
        MessageAdapter adapter = message(FILE_MULTIPART_REGULAR);

        assertEquals("[MAILINATOR] Fw: Confirmation of Thalys Ticketless booking - 837245850", adapter.getSubject());
    }

    @Test
    public void testGetPlainTextBody_MultipartRegular() throws MessagingException {
        MessageAdapter adapter = message(FILE_MULTIPART_REGULAR);

        assertEquals("If this email is not correctly displayed, please view the online" +
                "version.To visualise your barcode ID, you need to allow images to be displayed. " +
                "If images remain blocked, use the link above or print the barcode in the attachment.", trim(adapter.getPlainTextBody()));
    }

    @Test
    public void testGetHtmlTextBody_MultipartRegular() throws MessagingException {
        MessageAdapter adapter = message(FILE_MULTIPART_REGULAR);

        assertEquals("<html><body>" +
                "<span class=\"xfm_1631483529\"><span><br />" +
                "<br /></span></span><blockquote class=\"xfmc0\" style=\"border-left: 1px solid rgb(204, 204, 204); margin: 0px 0px 0px 0.8ex; padding-left: 1ex;\">" +
                "<span class=\"xfm_1631483529\"><span class=\"xfmc1\"> " +
                "</span></span><img alt=\"\" height=\"0\" src=\"http://rt2-t.campaigns.thalys.com/r/?id=hb3f74b1,458c6a22,1\" width=\"0\" /> " +
                "</blockquote></body></html>", trim(adapter.getHtmlTextBody()));
    }

    @Test
    public void testGetAttachments_MultipartRegular() throws MessagingException {
        MessageAdapter adapter = message(FILE_MULTIPART_REGULAR);

        assertEquals(1, adapter.getAttachments().size());
        assertEquals("barcode.jpg", adapter.getAttachments().get(0).getFilename());
        assertEquals("image/jpeg", adapter.getAttachments().get(0).getContentType());
    }

    @Test
    public void testGetSubject_TortureTest() throws MessagingException {
        MessageAdapter adapter = message(FILE_TORTURE_TEST);

        assertEquals("Multi-media mail demonstration", adapter.getSubject());
    }

    @Test
    public void testGetPlainTextBody_TortureTest() throws MessagingException {
        MessageAdapter adapter = message(FILE_TORTURE_TEST);

        assertEquals("This is a demonstration of multi-part mail with encapsulated messages.  This" +
                "is a very complex message whose purpose it is to exercise software using the" +
                "new multi-part message standard.", trim(adapter.getPlainTextBody()));
    }

    @Test
    public void testGetHtmlTextBody_TortureTest() throws MessagingException {
        MessageAdapter adapter = message(FILE_TORTURE_TEST);

        assertEquals("", adapter.getHtmlTextBody());
    }

    @Test
    public void testGetAttachments_TortureTest() throws MessagingException {
        MessageAdapter adapter = message(FILE_TORTURE_TEST);

        assertEquals(11, adapter.getAttachments().size());
    }

    private static String trim(String str) {
        return str.replace("\r", "").replace("\n", "").replace("\t", "");
    }

    private static MessageAdapter message(String fileName) {
        InputStream stream = MessageAdapterTest.class.getClassLoader().getResourceAsStream("mail/" + fileName);

        try {
            return new MessageAdapter(new MimeMessage(null, stream));
        } catch (MessagingException e) {
            throw new AssertionError("Cannot parse given resource: " + fileName + ". " + e.getMessage());
        }
    }
}
