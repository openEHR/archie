package com.nedap.archie.archetypevalidator;

import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rminfo.ReferenceModels;
import org.apache.commons.io.input.BOMInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests validation of ADL 2.4 at-coded archetypes and the node identifier code system validation option.
 */
public class AtCodedArchetypeValidationTest {

    private static final String PATH = "/com/nedap/archie/adl14/";

    private ReferenceModels models;

    @BeforeEach
    public void setup() {
        models = new ReferenceModels();
        models.registerModel(ArchieRMInfoLookup.getInstance());
    }

    @Test
    public void atCodedValidatesInAtCodedMode() throws Exception {
        Archetype archetype = parse("openEHR-EHR-OBSERVATION.demo_adl2_at.v1.0.0.adls");
        ValidationResult result = validate(archetype, NodeIdCodeSystemValidation.AT_CODED);
        assertTrue(result.passes(), result.toString());
    }

    @Test
    public void atCodedValidatesInAutoDetectMode() throws Exception {
        Archetype archetype = parse("openEHR-EHR-OBSERVATION.demo_adl2_at.v1.0.0.adls");
        ValidationResult result = validate(archetype, NodeIdCodeSystemValidation.AUTO_DETECT);
        assertTrue(result.passes(), result.toString());
    }

    @Test
    public void atCodedRejectedInDefaultIdCodedMode() throws Exception {
        Archetype archetype = parse("openEHR-EHR-OBSERVATION.demo_adl2_at.v1.0.0.adls");
        ValidationResult result = validate(archetype, NodeIdCodeSystemValidation.ID_CODED);
        assertFalse(result.passes(), result.toString());
        assertTrue(hasError(result, ErrorType.INVALID_NODE_ID_CODE_SYSTEM), result.toString());
    }

    @Test
    public void idCodedValidatesInAutoDetectMode() throws Exception {
        Archetype archetype = parse("openEHR-EHR-OBSERVATION.demo_adl2_id.v1.0.0.adls");
        ValidationResult result = validate(archetype, NodeIdCodeSystemValidation.AUTO_DETECT);
        assertTrue(result.passes(), result.toString());
    }

    @Test
    public void idCodedValidatesInDefaultIdCodedMode() throws Exception {
        Archetype archetype = parse("openEHR-EHR-OBSERVATION.demo_adl2_id.v1.0.0.adls");
        ValidationResult result = validate(archetype, NodeIdCodeSystemValidation.ID_CODED);
        assertTrue(result.passes(), result.toString());
    }

    @Test
    public void idCodedRejectedInAtCodedMode() throws Exception {
        Archetype archetype = parse("openEHR-EHR-OBSERVATION.demo_adl2_id.v1.0.0.adls");
        ValidationResult result = validate(archetype, NodeIdCodeSystemValidation.AT_CODED);
        assertFalse(result.passes(), result.toString());
        assertTrue(hasError(result, ErrorType.INVALID_NODE_ID_CODE_SYSTEM), result.toString());
    }

    @Test
    public void mixedCodeSystemRejectedInAutoDetect() throws Exception {
        Archetype archetype = parse("openEHR-EHR-OBSERVATION.demo_adl2_id.v1.0.0.adls");
        // turn one id-coded node into an at-coded one, so the archetype mixes both code systems
        CObject firstChild = firstNonRootChild(archetype);
        assertNotNull(firstChild, "expected a child node to mutate");
        firstChild.setNodeId("at" + firstChild.getNodeId().substring(2));
        ValidationResult result = validate(archetype, NodeIdCodeSystemValidation.AUTO_DETECT);
        assertFalse(result.passes(), result.toString());
        assertTrue(hasError(result, ErrorType.INVALID_NODE_ID_CODE_SYSTEM), result.toString());
    }

    @Test
    public void specializedAtCodedValidatesAndFlattens() throws Exception {
        Archetype parent = parse("openEHR-EHR-CLUSTER.exam_adl2_at.v2.1.3.adls");
        Archetype child = parse("openEHR-EHR-CLUSTER.exam-abdomen_adl2_at.v0.0.1-alpha.adls");

        InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
        ArchetypeValidationSettings settings = new ArchetypeValidationSettings();
        settings.setNodeIdCodeSystemValidation(NodeIdCodeSystemValidation.AUTO_DETECT);
        repository.setArchetypeValidationSettings(settings);
        repository.addArchetype(parent);
        repository.addArchetype(child);

        ValidationResult result = new ArchetypeValidator(models).validate(child, repository);
        assertTrue(result.passes(), result.toString());
        assertNotNull(result.getFlattened(), result.toString());
    }

    private CObject firstNonRootChild(Archetype archetype) {
        for (CAttribute attribute : archetype.getDefinition().getAttributes()) {
            for (CObject child : attribute.getChildren()) {
                if (child.getNodeId() != null && child.getNodeId().startsWith("id")) {
                    return child;
                }
            }
        }
        return null;
    }

    private boolean hasError(ValidationResult result, ErrorType errorType) {
        return result.getErrors().stream().anyMatch(message -> message.getType() == errorType);
    }

    private ValidationResult validate(Archetype archetype, NodeIdCodeSystemValidation codeSystem) {
        InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
        ArchetypeValidationSettings settings = new ArchetypeValidationSettings();
        settings.setNodeIdCodeSystemValidation(codeSystem);
        repository.setArchetypeValidationSettings(settings);
        repository.addArchetype(archetype);
        return new ArchetypeValidator(models).validate(archetype, repository);
    }

    private Archetype parse(String fileName) throws Exception {
        ADLParser parser = new ADLParser(BuiltinReferenceModels.getMetaModelProvider());
        try (InputStream stream = new BOMInputStream(AtCodedArchetypeValidationTest.class.getResourceAsStream(PATH + fileName))) {
            Archetype archetype = parser.parse(stream);
            assertTrue(parser.getErrors().hasNoErrors(), parser.getErrors().toString());
            return archetype;
        }
    }
}
