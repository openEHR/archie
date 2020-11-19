package com.nedap.archie.rmobjectvalidatortest;

import com.nedap.archie.rm.archetyped.Archetyped;
import com.nedap.archie.rm.archetyped.Locatable;
import com.nedap.archie.rm.composition.Entry;
import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.generic.PartySelf;
import com.nedap.archie.rm.support.identification.ArchetypeID;
import com.nedap.archie.rm.support.identification.TerminologyId;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessage;
import com.nedap.archie.rmobjectvalidator.RMObjectValidator;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InvariantTestUtil {

    public static void assertValid(Object object) {
        RMObjectValidator validator = new RMObjectValidator(ArchieRMInfoLookup.getInstance());
        List<RMObjectValidationMessage> messages = validator.validate(object);
        assertTrue("object should be valid, was not: " + messages, messages.isEmpty());
    }

    public static void assertInvariantInvalid(Object object, String invariantName, String path) {
        assertInvariantInvalid(object, invariantName, ArchieRMInfoLookup.getInstance().getTypeInfo(object.getClass()).getRmName(), path);
    }

    public static void assertInvariantInvalid(Object object, String invariantName, String rmTypeName, String path) {
        RMObjectValidator validator = new RMObjectValidator(ArchieRMInfoLookup.getInstance());
        List<RMObjectValidationMessage> messages = validator.validate(object);
        assertEquals(messages.toString(), 1, messages.size());
        assertEquals("Invariant " + invariantName + " failed on type " + rmTypeName, messages.get(0).getMessage());
        assertEquals(path, messages.get(0).getPath());
    }

    public static void setLocatableBasics(Locatable locatable) {
        locatable.setArchetypeNodeId("id1");
        locatable.setNameAsString("test");
    }

    public static void setArchetypeRootBasics(Locatable locatable) {
        setLocatableBasics(locatable);
        locatable.setArchetypeDetails(new Archetyped(new ArchetypeID("OpenEHR-EHR-" + ArchieRMInfoLookup.getInstance().getTypeInfo(locatable.getClass()).getRmName() + ".example.v1.0.0"), "1.1.0"));
    }

    public static void setEntryBasics(Entry entry) {
        setArchetypeRootBasics(entry);
        entry.setLanguage(new CodePhrase("ISO_639-1::en"));
        entry.setSubject(new PartySelf());
        entry.setEncoding(new CodePhrase(new TerminologyId("IANA_character-sets"), "UTF-8"));
    }
}
