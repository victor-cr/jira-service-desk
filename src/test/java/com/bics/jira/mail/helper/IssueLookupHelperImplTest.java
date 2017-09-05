package com.bics.jira.mail.helper;

import org.junit.Assert;
import org.junit.Test;

/**
 * Java Doc here
 *
 * @author Victor Polischuk
 * @since 14/07/13 19:34
 */
public class IssueLookupHelperImplTest extends Assert {
    @Test
    public void testPreparedSummary_WordWithAmpersand() {
        String expectedReturn = "D&T Switch Review Draft Results";
        String actualReturn = IssueLookupHelperImpl.prepareSummary("D&T - Switch Review Draft Results");

        assertEquals(expectedReturn, actualReturn);
    }

    @Test
    public void testPreparedSummary_Ampersand() {
        String expectedReturn = "boys girls";
        String actualReturn = IssueLookupHelperImpl.prepareSummary("boys & girls");

        assertEquals(expectedReturn, actualReturn);
    }

    @Test
    public void testPreparedSummary_Bracket() {
        String expectedReturn = "tag subject";
        String actualReturn = IssueLookupHelperImpl.prepareSummary("[tag] subject");

        assertEquals(expectedReturn, actualReturn);
    }
}
