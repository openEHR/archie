package com.nedap.archie.aom.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AOMUtilsTest {

    @Test
    public void codesConformant() {
        // id-coded
        assertTrue(AOMUtils.codesConformant("id1", "id1"));
        assertTrue(AOMUtils.codesConformant("id1.1", "id1"));
        assertTrue(AOMUtils.codesConformant("id1.1.1", "id1.1"));
        assertFalse(AOMUtils.codesConformant("id2", "id1"));

        // at-coded, zero-padded (ADL 2.4) - the case enabled by using isValidADL14Code instead of isValidCode
        assertTrue(AOMUtils.codesConformant("at0000", "at0000"));
        assertTrue(AOMUtils.codesConformant("at0000.1", "at0000"));
        assertTrue(AOMUtils.codesConformant("at0001.0.1", "at0001"));
        assertFalse(AOMUtils.codesConformant("at0001", "at0000"));
        // shares the parent string as a prefix but is not a specialisation child (no '.' separator)
        assertFalse(AOMUtils.codesConformant("at00001", "at0000"));
    }

    @Test
    public void isValidValueSetCode() {
        // non-padded value set codes (id-coded archetypes and generated at-coded codes)
        assertTrue(AOMUtils.isValidValueSetCode("ac1"));
        assertTrue(AOMUtils.isValidValueSetCode("ac9000"));
        // zero-padded value set code, as produced for at-coded archetypes by the ADL 1.4 converter
        assertTrue(AOMUtils.isValidValueSetCode("ac0001"));
        assertTrue(AOMUtils.isValidValueSetCode("ac0001.1"));
        // not value set codes
        assertFalse(AOMUtils.isValidValueSetCode("at0001"));
        assertFalse(AOMUtils.isValidValueSetCode("id1"));
        assertFalse(AOMUtils.isValidValueSetCode("acabc"));
    }

    @Test
    public void isValidNonZeroPaddedCode() {
        // non-zero-padded (id-coded ADL 2) format: no leading zeros, any id/at/ac prefix
        assertTrue(AOMUtils.isValidNonZeroPaddedCode("id1"));
        assertTrue(AOMUtils.isValidNonZeroPaddedCode("at2"));
        assertTrue(AOMUtils.isValidNonZeroPaddedCode("ac3"));
        assertTrue(AOMUtils.isValidNonZeroPaddedCode("id1.1"));
        // zero-padded (at-coded) codes are not valid in the non-zero-padded (id-coded) system
        assertFalse(AOMUtils.isValidNonZeroPaddedCode("at0000"));
        assertFalse(AOMUtils.isValidNonZeroPaddedCode("ac0001"));
        assertFalse(AOMUtils.isValidNonZeroPaddedCode(null));
    }

    @Test
    public void isValidZeroPaddedCode() {
        // zero-padded (minimum four digit) first segment, specialisation segments without leading zeros
        assertTrue(AOMUtils.isValidZeroPaddedCode("at0000"));
        assertTrue(AOMUtils.isValidZeroPaddedCode("at0001.1"));
        assertTrue(AOMUtils.isValidZeroPaddedCode("ac0002"));
        assertTrue(AOMUtils.isValidZeroPaddedCode("ac0001.1"));
        assertTrue(AOMUtils.isValidZeroPaddedCode("at9088"));
        assertTrue(AOMUtils.isValidZeroPaddedCode("at12345")); // more than four digits is allowed (values > 9999)
        // non-zero-padded codes are not valid zero-padded, even though the grammar and isValidADL14Code accept them
        assertFalse(AOMUtils.isValidZeroPaddedCode("at5"));
        assertFalse(AOMUtils.isValidZeroPaddedCode("at123"));
        assertFalse(AOMUtils.isValidZeroPaddedCode("id1"));
        // leading zeros are only allowed in the first segment, not in specialisation segments
        assertFalse(AOMUtils.isValidZeroPaddedCode("at0000.01"));
        assertFalse(AOMUtils.isValidZeroPaddedCode(null));
    }

    @Test
    public void codeAtLevel() {
        assertEquals("id1", AOMUtils.codeAtLevel("id1", 0));
        assertEquals("id1", AOMUtils.codeAtLevel("id1.1", 0));
        assertEquals("id1.1", AOMUtils.codeAtLevel("id1.1", 1));
        assertEquals("id1.1", AOMUtils.codeAtLevel("id1.1.1", 1));
        assertEquals("id1", AOMUtils.codeAtLevel("id1.0.1", 1));

        assertEquals("at0001", AOMUtils.codeAtLevel("at0001", 0));
        assertEquals("at0001", AOMUtils.codeAtLevel("at0001.1", 0));
        assertEquals("at0001.1", AOMUtils.codeAtLevel("at0001.1", 1));
        assertEquals("at0001.1", AOMUtils.codeAtLevel("at0001.1.1", 1));
        assertEquals("at0001", AOMUtils.codeAtLevel("at0001.0.1", 1));
    }

}
