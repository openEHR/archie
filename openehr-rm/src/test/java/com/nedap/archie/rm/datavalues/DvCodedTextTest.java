package com.nedap.archie.rm.datavalues;

import com.nedap.archie.rm.datatypes.CodePhrase;
import org.junit.Test;

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
}