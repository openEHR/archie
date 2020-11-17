package com.nedap.archie.rmobjectvalidatortest;

import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.support.identification.TerminologyId;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessage;
import com.nedap.archie.rmobjectvalidator.RMObjectValidator;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class DvTextInvariantTest {

    @Test
    public void testDvTextValid() {
        DvText text = new DvText("something");
        text.setLanguage(new CodePhrase(new TerminologyId("ISO_639-1"), "nl"));
        text.setEncoding(new CodePhrase(new TerminologyId("IANA_character-sets"), "UTF-8"));
        text.setFormatting("markdown");

        RMObjectValidator rmObjectValidator = new RMObjectValidator(ArchieRMInfoLookup.getInstance());
        List<RMObjectValidationMessage> messages = rmObjectValidator.validate(text);
        assertEquals(messages.toString(), 0, messages.size());
    }

    @Test
    public void testDvTextWrongLanguage() {
        DvText text = new DvText("something");
        text.setLanguage(new CodePhrase(new TerminologyId("ISO_639-1"), "Pig latin"));

        RMObjectValidator rmObjectValidator = new RMObjectValidator(ArchieRMInfoLookup.getInstance());
        List<RMObjectValidationMessage> messages = rmObjectValidator.validate(text);
        assertEquals(messages.toString(), 1, messages.size());
        assertEquals("Invariant Language_valid failed on type DV_TEXT", messages.get(0).getMessage());
        assertEquals("/", messages.get(0).getPath());
    }

    @Test
    public void testDvTextWrongEncoding() {
        DvText text = new DvText("something");
        text.setEncoding(new CodePhrase(new TerminologyId("ISO_639-1"), "Pig latin"));

        RMObjectValidator rmObjectValidator = new RMObjectValidator(ArchieRMInfoLookup.getInstance());
        List<RMObjectValidationMessage> messages = rmObjectValidator.validate(text);
        assertEquals(messages.toString(), 1, messages.size());
        assertEquals("Invariant Encoding_valid failed on type DV_TEXT", messages.get(0).getMessage());
        assertEquals("/", messages.get(0).getPath());
    }

    @Test
    public void testDvTextWrongTermId() {
        DvText text = new DvText("something");
        text.setEncoding(new CodePhrase(new TerminologyId("wrong term id"), "nl"));

        RMObjectValidator rmObjectValidator = new RMObjectValidator(ArchieRMInfoLookup.getInstance());
        List<RMObjectValidationMessage> messages = rmObjectValidator.validate(text);
        assertEquals(messages.toString(), 1, messages.size());
        assertEquals("Invariant Encoding_valid failed on type DV_TEXT", messages.get(0).getMessage());
        assertEquals("/", messages.get(0).getPath());
    }
}
