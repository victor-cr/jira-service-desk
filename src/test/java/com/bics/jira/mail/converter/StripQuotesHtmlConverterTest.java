package com.bics.jira.mail.converter;

import com.bics.jira.mail.model.mail.Attachment;
import com.bics.jira.mail.model.mail.Body;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 11/04/13 17:03
 */
public class StripQuotesHtmlConverterTest extends Assert {
    private static final String FILE_INLINE_MESSAGE = "InlineImage";
    private static final String FILE_UNKNOWN_INLINE_MESSAGE = "UnknownInlineImage";
    private static final String FILE_EMPTY_TABLE = "EmptyTable";
    private static final String FILE_TABLE_LAYOUT = "TableLayout";
    private static final String FILE_DATA_TABLE = "DataTable";
    private static final String FILE_OUTLOOK_LIST = "OutlookList";
    private static final String FILE_MULTILINE_TABLE = "MultilineTable";
    private static final String FILE_BOLD_TEXT = "Text";
    private static final String FILE_ESCAPABLE_TEXT = "EscapableText";
    private static final String FILE_LINK = "Link";
    private static final String FILE_PRE = "Pre";

    private final StripQuotesHtmlConverter converter = new StripQuotesHtmlConverter();

    @Test
    public void testConvert_UnknownInlineImage() throws Exception {
        assertFiles(FILE_UNKNOWN_INLINE_MESSAGE);
    }

    @Test
    public void testConvert_InlineImage() throws Exception {
        assertFiles(FILE_INLINE_MESSAGE,
                new Attachment(null, null, "test1.jpg", "image001.png@01CE36AA.108B54F0", true),
                new Attachment(null, null, "test3.jpg", null, false)
        );
    }

    @Test
    public void testConvert_TableLayout() throws Exception {
        assertFiles(FILE_TABLE_LAYOUT,
                new Attachment(null, null, "test1.jpg", "left.jpg", true),
                new Attachment(null, null, "test2.jpg", "leftupper.jpg", true),
                new Attachment(null, null, "test3.jpg", "right.jpg", true),
                new Attachment(null, null, "test4.jpg", "rightupper.jpg", true),
                new Attachment(null, null, "test5.jpg", "leftcorner.jpg", true),
                new Attachment(null, null, "test6.jpg", "rightcorner.jpg", true),
                new Attachment(null, null, "test7.jpg", "logo.jpg", true),
                new Attachment(null, null, "test8.jpg", "shadow.jpg", true)
        );
    }

    @Test
    public void testConvert_DataTable() throws Exception {
        assertFiles(FILE_DATA_TABLE);
    }

    @Test
    public void testConvert_EmptyTable() throws Exception {
        assertFiles(FILE_EMPTY_TABLE);
    }

    @Test
    public void testConvert_OutlookList() throws Exception {
        assertFiles(FILE_OUTLOOK_LIST);
    }

    @Test
    public void testConvert_MultilineTable() throws Exception {
        assertFiles(FILE_MULTILINE_TABLE);
    }

    @Test
    public void testConvert_BoldText() throws Exception {
        assertFiles(FILE_BOLD_TEXT);
    }

    @Test
    public void testConvert_EscapableText() throws Exception {
        assertFiles(FILE_ESCAPABLE_TEXT);
    }

    @Test
    public void testConvert_Link() throws Exception {
        assertFiles(FILE_LINK);
    }

    @Test
    public void testConvert_Pre() throws Exception {
        assertFiles(FILE_PRE);
    }

    private void assertFiles(String testName, Attachment... expectedAttachments) {
        Set<Attachment> attachments = new HashSet<Attachment>(Arrays.asList(expectedAttachments));

        attachments.add(new Attachment(null, null, "not-used1.jpg", "nu.png@zz12CA5AB0.108B54F0", true));
        attachments.add(new Attachment(null, null, "not-used2.jpg", "nu.png@yy12CA5AB0.108B54F0", true));
        attachments.add(new Attachment(null, null, "not-used3.jpg", "nu.png@xx12CA5AB0.108B54F0", true));

        Body expectedReturn = new Body(message(testName + ".txt"), new ArrayList<Attachment>(Arrays.asList(expectedAttachments)));
        Body actualReturn = converter.convert(message(testName + ".html"), attachments);

        assertEquals(testName, expectedReturn.getBody(), actualReturn.getBody());
        assertEquals(testName, new HashSet<Attachment>(expectedReturn.getUsed()), new HashSet<Attachment>(actualReturn.getUsed()));
    }

    private static String message(String fileName) {
        try {
            InputStream stream = StripQuotesHtmlConverterTest.class.getClassLoader().getResourceAsStream("wiki/" + fileName);

            try {
                return IOUtils.toString(stream).replace("\r\n", "\n").replace('\r', '\n');
            } finally {
                stream.close();
            }
        } catch (IOException e) {
            throw new AssertionError("Cannot parse given resource: " + fileName + ". " + e.getMessage());
        }
    }
}