package com.nedap.archie.rm.datatypes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class CodePhraseTest {

    @Test
    public void testEquals() {
        CodePhrase codePhraseOne = new CodePhrase("hl7::gender");
        CodePhrase codePhraseTwo = new CodePhrase("hl7::gender");
        CodePhrase codePhraseThree = new CodePhrase("hl2::gender");
        CodePhrase codePhraseFour = new CodePhrase("hl7::color");

        assertEquals(codePhraseOne, codePhraseTwo);

        assertNotEquals(codePhraseOne, codePhraseThree);
        assertNotEquals(codePhraseOne, codePhraseFour);
    }

    @Test
    public void testToString() {
        CodePhrase codePhraseOne = new CodePhrase();
        CodePhrase codePhraseTwo = new CodePhrase("hl7::gender");

        assertEquals("null::null", codePhraseOne.toString());
        assertEquals("hl7::gender", codePhraseTwo.toString());
    }
}
