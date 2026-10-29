package com.nedap.archie.definitions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdlCodeDefinitionsTest {

    @Test
    public void isPrimitiveNodeId() {
        assertTrue(AdlCodeDefinitions.isPrimitiveNodeId("id9999"));
        assertTrue(AdlCodeDefinitions.isPrimitiveNodeId("at9999"));
        assertFalse(AdlCodeDefinitions.isPrimitiveNodeId("ac9999"));
        assertFalse(AdlCodeDefinitions.isPrimitiveNodeId("id1"));
        assertFalse(AdlCodeDefinitions.isPrimitiveNodeId(null));
    }

    @Test
    public void rootCodeRegexPatterns() {
        assertTrue("id1".matches(AdlCodeDefinitions.ROOT_CODE_REGEX_PATTERN));
        assertTrue("id1.1.1".matches(AdlCodeDefinitions.ROOT_CODE_REGEX_PATTERN));
        assertTrue("at0000".matches(AdlCodeDefinitions.ROOT_CODE_REGEX_PATTERN));
        assertTrue("at0000.1".matches(AdlCodeDefinitions.ROOT_CODE_REGEX_PATTERN));
        assertFalse("id2".matches(AdlCodeDefinitions.ROOT_CODE_REGEX_PATTERN));
        assertFalse("at0001".matches(AdlCodeDefinitions.ROOT_CODE_REGEX_PATTERN));
        assertFalse("at0000.2".matches(AdlCodeDefinitions.ROOT_CODE_REGEX_PATTERN));

        assertTrue("id1.1".matches(AdlCodeDefinitions.ID_CODED_ROOT_CODE_REGEX_PATTERN));
        assertFalse("at0000".matches(AdlCodeDefinitions.ID_CODED_ROOT_CODE_REGEX_PATTERN));

        assertTrue("at0000.1".matches(AdlCodeDefinitions.AT_CODED_ROOT_CODE_REGEX_PATTERN));
        assertFalse("id1".matches(AdlCodeDefinitions.AT_CODED_ROOT_CODE_REGEX_PATTERN));
    }
}
