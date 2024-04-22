package com.nedap.archie.rmobjectvalidator.invariants;

import com.nedap.archie.flattener.OperationalTemplateProvider;
import org.openehr.rm.archetyped.Archetyped;
import org.openehr.rm.archetyped.Locatable;
import org.openehr.rm.composition.Entry;
import org.openehr.rm.datatypes.CodePhrase;
import org.openehr.rm.generic.PartySelf;
import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.rm.support.identification.LocatableRef;
import org.openehr.rm.support.identification.ObjectVersionId;
import org.openehr.rm.support.identification.TerminologyId;
import com.nedap.archie.openehr.rminfo.OpenEhrRmInfoLookup;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessage;
import com.nedap.archie.rmobjectvalidator.RMObjectValidator;
import com.nedap.archie.testutil.DummyOperationalTemplateProvider;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InvariantTestUtil {
    private static final OperationalTemplateProvider optProvider = new DummyOperationalTemplateProvider("example");

    public static void assertValid(Object object) {
        RMObjectValidator validator = new RMObjectValidator(OpenEhrRmInfoLookup.getInstance(), optProvider);
        List<RMObjectValidationMessage> messages = validator.validate(object);
        assertTrue("object should be valid, was not: " + messages, messages.isEmpty());
    }

    public static void assertInvariantInvalid(Object object, String invariantName, String path) {
        assertInvariantInvalid(object, invariantName, OpenEhrRmInfoLookup.getInstance().getTypeInfo(object.getClass()).getRmName(), path);
    }

    public static void assertInvariantInvalid(Object object, String invariantName, String rmTypeName, String path) {
        RMObjectValidator validator = new RMObjectValidator(OpenEhrRmInfoLookup.getInstance(), optProvider);
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
        locatable.setArchetypeDetails(new Archetyped(new ArchetypeID("OpenEHR-EHR-" + OpenEhrRmInfoLookup.getInstance().getTypeInfo(locatable.getClass()).getRmName() + ".example.v1.0.0"), "1.1.0"));
    }

    public static void setEntryBasics(Entry entry) {
        setArchetypeRootBasics(entry);
        entry.setLanguage(new CodePhrase("ISO_639-1::en"));
        entry.setSubject(new PartySelf());
        entry.setEncoding(new CodePhrase(new TerminologyId("IANA_character-sets"), "UTF-8"));
    }

    public static LocatableRef createExampleRef(String type) {
        LocatableRef ref = new LocatableRef();
        ref.setNamespace("local");
        ref.setPath("/");
        ref.setType(type);
        ObjectVersionId id = new ObjectVersionId();
        id.setValue("some-id2234");
        ref.setId(id);
        return ref;
    }
}
