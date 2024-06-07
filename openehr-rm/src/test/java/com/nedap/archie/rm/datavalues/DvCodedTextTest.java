package com.nedap.archie.rm.datavalues;

import org.openehr.rm.datatypes.CodePhrase;
import org.junit.Test;
import org.openehr.rm.datavalues.DvCodedText;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DvCodedTextTest {

    @Test
    public void testEquals() {
        DvCodedText dvCodedTextOne = new DvCodedText("some text", new CodePhrase("icd10:123"));
        DvCodedText dvCodedTextTwo = new DvCodedText("some text", new CodePhrase("icd10:123"));
        DvCodedText dvCodedTextThree = new DvCodedText("some text", new CodePhrase("icd10:234"));

        assertEquals(dvCodedTextOne, dvCodedTextTwo);
        assertNotEquals(dvCodedTextOne, dvCodedTextThree);
    }

    @Test
    public void testToString() {
        DvCodedText dvCodedTextOne = new DvCodedText();
        DvCodedText dvCodedTextTwo = new DvCodedText("some text", new CodePhrase("icd10:123"));

        assertEquals("DvCodedText{defining_code=null, value=null}", dvCodedTextOne.toString());
        assertEquals("DvCodedText{defining_code=UNKNOWN::icd10:123, value=some text}", dvCodedTextTwo.toString());
    }
}
