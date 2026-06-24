package com.nedap.archie.aom;

import com.nedap.archie.ArchieLanguageConfiguration;
import com.nedap.archie.adlparser.ADLParseException;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.FlattenerConfiguration;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.flattener.specexamples.FlattenerTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ArchetypeTerminologyTest {

    @AfterEach
    public void tearDown() {
        ArchieLanguageConfiguration.setThreadLocalDescriptiongAndMeaningLanguage(null);
    }
    
    /**
     * Gap #4 probe (root cause): terminology auto-generation for at-coded archetypes.
     *
     * <p>{@code TerminologyContentGenerator} itself lives in the {@code adlchecker} module, which is not part of the
     * Gradle build (absent from settings.gradle) and compiles against a published {@code archie-all} artifact, so it
     * cannot be unit-tested against the current code. Its core gating predicate — "does this node id already have a
     * term in every language?" — is {@code ArchetypeTerminology.hasIdCodeInAllLanguages}, which lives here and is
     * buildable. That method is {@code AOMUtils.isIdCode(code) && ...}, so it returns false for an at-coded node id
     * even when the term is present, and {@code idCodes()} cannot enumerate at-coded node ids at all.
     *
     * <p>If this test fails, term auto-generation is genuinely broken for at-coded archetypes (the generator treats
     * every present at-coded term as missing and cannot list node ids), so the code-system-aware fix is necessary.
     */
    @Test
    public void atCodedNodeIdsRecognisedByTerminology() throws Exception {
        Archetype archetype = parseAtCoded("/com/nedap/archie/adl14/openEHR-EHR-OBSERVATION.demo_adl2_at.v1.0.0.adls");
        ArchetypeTerminology terminology = archetype.getTerminology();

        // the at-coded root term is present in the source, so the generator must NOT consider it missing
        assertTrue(terminology.hasCode("at0000"), "expected the at0000 root term to be present");
        assertTrue(terminology.hasIdCodeInAllLanguages("at0000"),
                "at-coded node-id term is present but hasIdCodeInAllLanguages does not recognise it");

        // the generator enumerates node ids via idCodes(); for an at-coded archetype these are the at-prefixed node ids
        assertThat("idCodes() should enumerate the at-coded node ids", terminology.idCodes().contains("at0000"));
    }

    /**
     * Gap #3 probe (behavioural): term lookup for a slot-filled nested root in an at-coded template/OPT.
     *
     * <p>{@code OperationalTemplate.getTerm} gates {@code stripLastPartOfPath} on {@code AOMUtils.isIdCode(code)}.
     * For an at-coded use_archetype root the node id is at-coded, so the gate is false and the term-resolution path
     * differs from the id-coded case. This mirrors {@link #termForUseArchetype()} (which uses id-coded fixtures)
     * with at-coded fixtures. At-coded templates are in scope, so if this fails the {@code getTerm} fix is necessary.
     */
    @Test
    public void atCodedTermForUseArchetype() throws Exception {
        InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
        repository.addArchetype(parseAtCoded("/com/nedap/archie/aom/openEHR-EHR-COMPOSITION.atparent.v1.0.0.adls"));
        repository.addArchetype(parseAtCoded("/com/nedap/archie/aom/openEHR-EHR-GENERIC_ENTRY.atincluded.v1.0.0.adls"));

        Flattener flattener = new Flattener(repository, BuiltinReferenceModels.getMetaModelProvider(), FlattenerConfiguration.forOperationalTemplate());
        OperationalTemplate opt = (OperationalTemplate) flattener.flatten(repository.getArchetype("openEHR-EHR-COMPOSITION.atparent.v1.0.0"));

        CArchetypeRoot useArchetype = opt.itemAtPath("/content[at0001]");
        assertNotNull(useArchetype, "expected the use_archetype root at /content[at0001]");
        ArchieLanguageConfiguration.setThreadLocalDescriptiongAndMeaningLanguage("en");
        assertEquals("included archetype en", useArchetype.getTerm().getText());
        ArchieLanguageConfiguration.setThreadLocalDescriptiongAndMeaningLanguage("nl");
        assertEquals("included archetype nl", useArchetype.getTerm().getText());
    }

    private Archetype parseAtCoded(String resourcePath) throws Exception {
        // parse directly (tolerating grammar ambiguity warnings); FlattenerTestUtil.parse rejects the FULL AMBIGUITY
        // warnings the CKM-derived at-coded fixtures emit
        ADLParser parser = new ADLParser(BuiltinReferenceModels.getMetaModelProvider());
        try (InputStream stream = ArchetypeTerminologyTest.class.getResourceAsStream(resourcePath)) {
            assertNotNull(stream, "cannot find fixture: " + resourcePath);
            Archetype archetype = parser.parse(stream);
            assertTrue(parser.getErrors().hasNoErrors(), parser.getErrors().toString());
            return archetype;
        }
    }

    @Test
    public void termForUseArchetype() throws IOException, ADLParseException {
        //getting the term for a use archetype is a bit of a tricky situation - it's not for the 'id1' code,
        //it's for the code in the parent. So do some specific test here
        InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
        repository.addArchetype(FlattenerTestUtil.parse("/com/nedap/archie/aom/openEHR-EHR-COMPOSITION.parent.v1.0.0.adls"));
        repository.addArchetype(FlattenerTestUtil.parse("/com/nedap/archie/aom/openEHR-EHR-GENERIC_ENTRY.included.v1.0.0.adls"));

        //check that they are valid, just to be sure
        repository.compile(BuiltinReferenceModels.getMetaModelProvider());
        repository.getAllValidationResults().forEach(s -> assertThat(s.getErrors().toString(), s.getErrors().isEmpty()));

        //create operational template
        Flattener flattener = new Flattener(repository, BuiltinReferenceModels.getMetaModelProvider(), FlattenerConfiguration.forOperationalTemplate());
        OperationalTemplate opt = (OperationalTemplate) flattener.flatten(repository.getArchetype("openEHR-EHR-COMPOSITION.parent.v1.0.0"));

        //and check the getTerm() functionality
        CArchetypeRoot useArchetype = opt.itemAtPath("/content[id2]");
        ArchieLanguageConfiguration.setThreadLocalDescriptiongAndMeaningLanguage("nl");
        assertEquals("included archetype nl", useArchetype.getTerm().getText());
        ArchieLanguageConfiguration.setThreadLocalDescriptiongAndMeaningLanguage("en");
        assertEquals("included archetype en", useArchetype.getTerm().getText());

        //now to check if it uses the component terminology where possible
        CComplexObject element = opt.itemAtPath("/content[id2]/data[id3]/items[id2]");
        ArchieLanguageConfiguration.setThreadLocalDescriptiongAndMeaningLanguage("nl");
        assertEquals("an element", element.getTerm().getText()); //no dutch translation, should fallback to English
        ArchieLanguageConfiguration.setThreadLocalDescriptiongAndMeaningLanguage("en");
        assertEquals("an element", element.getTerm().getText());


    }
    
    
}
