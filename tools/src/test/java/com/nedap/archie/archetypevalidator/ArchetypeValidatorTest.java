package com.nedap.archie.archetypevalidator;

import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.openehrtestrm.TestRMInfoLookup;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rminfo.ReferenceModels;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by pieter.bos on 05/04/2017.
 */
public class ArchetypeValidatorTest {

    private ADLParser parser;
    private Archetype archetype;

    private ReferenceModels models;

    @Before
    public void setup() {
        parser = new ADLParser();
        models = new ReferenceModels();
        models.registerModel(ArchieRMInfoLookup.getInstance());
        models.registerModel(TestRMInfoLookup.getInstance());
    }

    @Test
    public void validArchetype() throws Exception {
        archetype = parse("/basic.adl");
        ArchetypeValidationResult archetypeValidationResult = new ArchetypeValidator(models).validate(archetype);
        List<ArchetypeValidationMessage> messages = archetypeValidationResult.getErrors();
        assertEquals(0, messages.size());
    }

    @Test
    public void VCARMNonExistantType() throws Exception {
        archetype = parse("/adl2-tests/validity/rm_checking/openEHR-EHR-EVALUATION.VCARM_rm_non_existent_attribute.v1.0.0.adls");
        ArchetypeValidationResult archetypeValidationResult = new ArchetypeValidator(models).validate(archetype);
        List<ArchetypeValidationMessage> messages = archetypeValidationResult.getErrors();
        System.out.println(messages);
        assertEquals(1, messages.size());
        assertEquals(ErrorType.VCARM, messages.get(0).getType());
        assertNull(archetypeValidationResult.getFlattened());
    }

    @Test
    public void VCARMNonExistantTypeAlwaysFlatten() throws Exception {
        archetype = parse("/adl2-tests/validity/rm_checking/openEHR-EHR-EVALUATION.VCARM_rm_non_existent_attribute.v1.0.0.adls");
        InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
        repository.addArchetype(archetype);
        ArchetypeValidationSettings settings = new ArchetypeValidationSettings();
        settings.setAlwaysTryToFlatten(true);
        repository.setArchetypeValidationSettings(settings);
        ArchetypeValidationResult archetypeValidationResult = new ArchetypeValidator(models).validate(archetype, repository);
        List<ArchetypeValidationMessage> messages = archetypeValidationResult.getErrors();
        System.out.println(messages);
        assertEquals(1, messages.size());
        assertEquals(ErrorType.VCARM, messages.get(0).getType());
        assertNotNull(archetypeValidationResult.getFlattened());
    }

    @Test
    public void VCORMTNonConformingType2() throws Exception {
        archetype = parse("/adl2-tests/validity/rm_checking/openEHR-EHR-OBSERVATION.VCORMT_rm_non_conforming_type2.v1.0.0.adls");
        ArchetypeValidationResult archetypeValidationResult = new ArchetypeValidator(models).validate(archetype);
        List<ArchetypeValidationMessage> messages = archetypeValidationResult.getErrors();
        System.out.println(messages);
        assertEquals(messages.toString(), 1, messages.size());
        assertEquals(ErrorType.VCORMT, messages.get(0).getType());
    }

    @Test
    public void VACDFidCodeNotPresent() throws Exception {
        archetype = parse("/adl2-tests/validity/consistency/openEHR-TEST_PKG-ENTRY.VACDF_ac_code_in_definition_not_in_terminology.v1.0.0.adls");
        ArchetypeValidationResult archetypeValidationResult = new ArchetypeValidator(models).validate(archetype);
        List<ArchetypeValidationMessage> messages = archetypeValidationResult.getErrors();
        System.out.println(messages);
        assertEquals(1, messages.size());
        assertEquals(ErrorType.VACDF, messages.get(0).getType());

    }

    @Test
    public void VATDFatCodeNotPresent() throws Exception {
        archetype = parse("/adl2-tests/validity/consistency/openEHR-TEST_PKG-ENTRY.VATDF_at_code_in_ordinal_not_in_terminology.v1.0.0.adls");
        ArchetypeValidationResult archetypeValidationResult = new ArchetypeValidator(models).validate(archetype);
        List<ArchetypeValidationMessage> messages = archetypeValidationResult.getErrors();
        System.out.println(messages);
        assertEquals(1, messages.size());
        assertEquals(ErrorType.VATDF, messages.get(0).getType());

    }

    @Test
    public void tupleMemberSizeMismatch() throws Exception {
        archetype = parse("openEHR-EHR-CLUSTER.invalid_tuple_1.v1.0.0.adls");
        ArchetypeValidationResult archetypeValidationResult = new ArchetypeValidator(models).validate(archetype);
        List<ArchetypeValidationMessage> messages = archetypeValidationResult.getErrors();
        System.out.println(messages);
        assertEquals(2, messages.size());
        assertEquals(ErrorType.OTHER, messages.get(0).getType());
        assertTrue("message should complain about tuple members being incorrect", messages.get(0).getMessage().contains("There should be 3 tuple members"));
    }

    @Test
    public void tupleMemberTypeMismatch() throws Exception {
        archetype = parse("openEHR-EHR-CLUSTER.invalid_tuple_2.v1.0.0.adls");
        ArchetypeValidationResult archetypeValidationResult = new ArchetypeValidator(models).validate(archetype);
        List<ArchetypeValidationMessage> messages = archetypeValidationResult.getErrors();
        System.out.println(messages);
        assertEquals(1, messages.size());
        assertEquals(ErrorType.VCARM, messages.get(0).getType());
    }

    @Test
    public void tuplePrimitiveTypeMismatch() throws Exception {
        archetype = parse("openEHR-EHR-CLUSTER.invalid_tuple_3.v1.0.0.adls");
        ArchetypeValidationResult archetypeValidationResult = new ArchetypeValidator(models).validate(archetype);
        List<ArchetypeValidationMessage> messages = archetypeValidationResult.getErrors();
        System.out.println(messages);
        assertEquals(1, messages.size());
        assertEquals(ErrorType.VCORMT, messages.get(0).getType());
    }


    private Archetype parse(String filename) throws IOException {
        archetype = parser.parse(ArchetypeValidatorTest.class.getResourceAsStream(filename));
        assertTrue(parser.getErrors().toString(), parser.getErrors().hasNoErrors());
        return archetype;
    }

}
