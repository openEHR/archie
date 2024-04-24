package com.nedap.archie.adlparser;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.primitives.CString;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Test CString parsing. Does not test regexpes, see @RegexTest for that
 */
public class CStringParserTest {

    @Test
    public void testStringWithQuotes() throws Exception {
        ADLParser parser = new ADLParser();
        Archetype archetype = parser.parse(this.getClass().getResourceAsStream("openehr-TEST_PKG-WHOLE.escaped_strings.v1.0.0.adls"));

        assertFalse(parser.getErrors().hasErrors());
        CAttribute attributeWithoutQuotes = archetype.getDefinition().getAttribute("string_attr1");
        CAttribute attributeWithQuotes = archetype.getDefinition().getAttribute("string_attr2");
        CAttribute attributeWithBackslash = archetype.getDefinition().getAttribute("string_attr3");

        CString cStringWithoutQuotes = (CString) attributeWithoutQuotes.getChildren().get(0);
        CString cStringWithQuotes = (CString) attributeWithQuotes.getChildren().get(0);
        CString cStringWithBlackslash = (CString) attributeWithBackslash.getChildren().get(0);

        assertEquals(Lists.newArrayList("something"), cStringWithoutQuotes.getConstraint());
        assertEquals(Lists.newArrayList("something with a \"-mark"), cStringWithQuotes.getConstraint());
        assertEquals(Lists.newArrayList("something with a \\-mark"), cStringWithBlackslash.getConstraint());
    }

}
