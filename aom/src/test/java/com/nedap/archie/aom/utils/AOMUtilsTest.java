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

        // at-coded
        assertTrue(AOMUtils.codesConformant("at0000", "at0000"));
        assertTrue(AOMUtils.codesConformant("at0000.1", "at0000"));
        assertTrue(AOMUtils.codesConformant("at0001.0.1", "at0001"));
        assertFalse(AOMUtils.codesConformant("at0001", "at0000"));
        // shares the parent string as a prefix but is not a specialisation child (no '.' separator)
        assertFalse(AOMUtils.codesConformant("at00001", "at0000"));
    }

    @Test
    public void isValidValueSetCode() {
        // id-coded
        assertTrue(AOMUtils.isValidValueSetCode("ac1"));
        assertTrue(AOMUtils.isValidValueSetCode("ac9000"));
        // at-coded
        assertTrue(AOMUtils.isValidValueSetCode("ac0001"));
        assertTrue(AOMUtils.isValidValueSetCode("ac0001.1"));
        // not value set codes
        assertFalse(AOMUtils.isValidValueSetCode("at0001"));
        assertFalse(AOMUtils.isValidValueSetCode("id1"));
        assertFalse(AOMUtils.isValidValueSetCode("acabc"));
    }

    @Test
    public void isAtCode() {
        assertTrue(AOMUtils.isAtCode("at0000"));
        assertTrue(AOMUtils.isAtCode("at0.1"));
        assertFalse(AOMUtils.isAtCode("id1"));
        assertFalse(AOMUtils.isAtCode("ac0001"));
    }

    @Test
    public void isValidAdlCode() {
        assertTrue(AOMUtils.isValidAdlCode("id1"));
        assertTrue(AOMUtils.isValidAdlCode("id1.1"));
        assertTrue(AOMUtils.isValidAdlCode("at2"));
        assertTrue(AOMUtils.isValidAdlCode("at0000"));
        assertTrue(AOMUtils.isValidAdlCode("at0000.1"));
        assertTrue(AOMUtils.isValidAdlCode("at0.1"));
        assertTrue(AOMUtils.isValidAdlCode("ac0001"));
        assertFalse(AOMUtils.isValidAdlCode("at0000.01"));
        assertFalse(AOMUtils.isValidAdlCode("xx1"));
        assertFalse(AOMUtils.isValidAdlCode("attribute"));
        assertFalse(AOMUtils.isValidAdlCode(null));
    }

    @Test
    public void isValidIdCodedCode() {
        assertTrue(AOMUtils.isValidIdCodedCode("id1"));
        assertTrue(AOMUtils.isValidIdCodedCode("at2"));
        assertTrue(AOMUtils.isValidIdCodedCode("ac3"));
        assertTrue(AOMUtils.isValidIdCodedCode("id1.1"));
        assertTrue(AOMUtils.isValidIdCodedCode("id0.1"));
        // id-coded archetypes have no leading zeros
        assertFalse(AOMUtils.isValidIdCodedCode("at0000"));
        assertFalse(AOMUtils.isValidIdCodedCode("ac0001"));
        assertFalse(AOMUtils.isValidIdCodedCode(null));
    }

    @Test
    public void isValidAtCodedCode() {
        assertTrue(AOMUtils.isValidAtCodedCode("at0000"));
        assertTrue(AOMUtils.isValidAtCodedCode("at0001.1"));
        assertTrue(AOMUtils.isValidAtCodedCode("at0001.0.1"));
        assertTrue(AOMUtils.isValidAtCodedCode("at9088"));
        assertTrue(AOMUtils.isValidAtCodedCode("at12345"));
        // codes introduced in a specialised archetype
        assertTrue(AOMUtils.isValidAtCodedCode("at0.1"));
        assertTrue(AOMUtils.isValidAtCodedCode("at0.0.1"));
        // ac-codes are padded the same way
        assertTrue(AOMUtils.isValidAtCodedCode("ac0001"));
        assertTrue(AOMUtils.isValidAtCodedCode("ac0001.1"));
        assertTrue(AOMUtils.isValidAtCodedCode("ac0.1"));

        assertFalse(AOMUtils.isValidAtCodedCode("at5"));
        assertFalse(AOMUtils.isValidAtCodedCode("at123"));
        assertFalse(AOMUtils.isValidAtCodedCode("at0"));
        assertFalse(AOMUtils.isValidAtCodedCode("ac1"));
        assertFalse(AOMUtils.isValidAtCodedCode("ac0"));
        assertFalse(AOMUtils.isValidAtCodedCode("at0000.01"));
        // at-coded archetypes have no id-codes
        assertFalse(AOMUtils.isValidAtCodedCode("id1"));
        assertFalse(AOMUtils.isValidAtCodedCode("id0001"));
        assertFalse(AOMUtils.isValidAtCodedCode(null));
    }

    @Test
    public void stripPrefix() {
        assertEquals("1", AOMUtils.stripPrefix("id1"));
        assertEquals("0000", AOMUtils.stripPrefix("at0000"));
        assertEquals("not a code", AOMUtils.stripPrefix("not a code"));
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
