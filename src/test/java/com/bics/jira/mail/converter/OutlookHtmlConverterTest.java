package com.bics.jira.mail.converter;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 11/04/13 17:03
 */
public class OutlookHtmlConverterTest extends Assert {
    private static final String FILE_INLINE_MESSAGE = "InlineImage";
    private static final String FILE_TABLE_LAYOUT = "TableLayout";
    private static final String FILE_DATA_TABLE = "DataTable";
    private static final String FILE_MULTILINE_TABLE = "MultilineTable";
    private static final String FILE_BOLD_TEXT = "Text";
    private static final String FILE_ESCAPABLE_TEXT = "EscapableText";
    private static final String FILE_LINK = "Link";

    private final OutlookHtmlConverter converter = new OutlookHtmlConverter();

    @Test
    public void testConvert_InlineImage() throws Exception {
        assertFiles(FILE_INLINE_MESSAGE);
    }

    @Test
    public void testConvert_TableLayout() throws Exception {
        assertFiles(FILE_TABLE_LAYOUT);
    }

    @Test
    public void testConvert_DataTable() throws Exception {
        assertFiles(FILE_DATA_TABLE);
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

    private void assertFiles(String testName) {
        String expectedReturn = message(testName + ".txt");
        String actualReturn = converter.convert(message(testName + ".html"));

        assertEquals(testName, expectedReturn, actualReturn);
    }

    private static String message(String fileName) {
        InputStream stream = OutlookHtmlConverterTest.class.getClassLoader().getResourceAsStream("wiki/" + fileName);

        try {
            return IOUtils.toString(stream).replace("\r\n", "\n").replace('\r', '\n');
        } catch (IOException e) {
            throw new AssertionError("Cannot parse given resource: " + fileName + ". " + e.getMessage());
        }
    }
}
