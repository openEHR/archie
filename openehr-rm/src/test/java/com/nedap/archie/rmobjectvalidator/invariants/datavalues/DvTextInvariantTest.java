package com.nedap.archie.rmobjectvalidator.invariants.datavalues;

import org.openehr.rm.datatypes.CodePhrase;
import org.openehr.rm.datavalues.DvText;
import org.openehr.rm.support.identification.TerminologyId;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DvTextInvariantTest {

    @Test
    public void testDvTextValid() {
        DvText text = new DvText("something");
        text.setLanguage(new CodePhrase(new TerminologyId("ISO_639-1"), "nl"));
        text.setEncoding(new CodePhrase(new TerminologyId("IANA_character-sets"), "UTF-8"));
        text.setFormatting("markdown");

        InvariantTestUtil.assertValid(text);
    }

    @Test
    public void testDvTextWrongLanguage() {
        DvText text = new DvText("something");
        text.setLanguage(new CodePhrase(new TerminologyId("ISO_639-1"), "Pig latin"));
        InvariantTestUtil.assertInvariantInvalid(text, "Language_valid", "/");
    }

    @Test
    public void testDvTextWrongEncoding() {
        DvText text = new DvText("something");
        text.setEncoding(new CodePhrase(new TerminologyId("ISO_639-1"), "Pig latin"));
        InvariantTestUtil.assertInvariantInvalid(text, "Encoding_valid", "/");
    }

    @Test
    public void testDvTextWrongTermId() {
        DvText text = new DvText("something");
        text.setEncoding(new CodePhrase(new TerminologyId("wrong term id"), "nl"));
        InvariantTestUtil.assertInvariantInvalid(text, "Encoding_valid", "/");
    }
}
