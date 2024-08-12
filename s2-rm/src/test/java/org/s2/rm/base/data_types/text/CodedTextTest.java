package org.s2.rm.base.data_types.text;

import org.junit.Test;
import org.s2.rm.base.foundation_types.terminology.TerminologyCode;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CodedTextTest {

    @Test
    public void testEquals() {
        CodedText codedTextOne = new CodedText(new TerminologyTerm("icd10::123 preferred term", new TerminologyCode("icd10", "123")), "some screen text");
        CodedText codedTextTwo = new CodedText(new TerminologyTerm("icd10::123 preferred term", new TerminologyCode("icd10", "123")),"some screen text");
        CodedText codedTextThree = new CodedText(new TerminologyTerm("icd10::234 preferred term", new TerminologyCode("icd10", "234")), "some other screen text");

        assertEquals(codedTextOne, codedTextTwo);
        assertNotEquals(codedTextOne, codedTextThree);
    }

    @Test
    public void testToString() {
        CodedText codedTextOne = new CodedText();
        CodedText codedTextTwo = new CodedText(new TerminologyTerm("icd10::123 preferred term", new TerminologyCode("icd10", "123")), "some screen text");

        assertEquals("CodedText{term=null, text=null}", codedTextOne.toString());

        // TODO: dont know what the output of toString should be - possibly standard JSON? So fix the below when we know.
        assertEquals("CodedText{term=TerminologyTerm{description=\"icd10::123 preferred term\", concept=TerminologyTerm{terminologyId=\"icd10\", codeString=\"123\"}}, text=\"some screen text\"}", codedTextTwo.toString());
    }
}
