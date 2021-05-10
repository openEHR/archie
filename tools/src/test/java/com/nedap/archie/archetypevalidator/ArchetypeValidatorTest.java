package com.nedap.archie.archetypevalidator;

import com.nedap.archie.adlparser.ADLParseException;
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
        ValidationResult validationResult = new ArchetypeValidator(models).validate(archetype);
        List<ValidationMessage> messages = validationResult.getErrors();
        assertEquals(0, messages.size());
    }

    @Test
    public void validArchetypeConceptWithUnderscore() throws Exception {
        archetype = parse("basic_with_concept_underscore.adls");
        ValidationResult validationResult = new ArchetypeValidator(models).validate(archetype);
        List<ValidationMessage> messages = validationResult.getErrors();
        assertEquals(0, messages.size());
    }

    @Test
    public void VCARMNonExistantType() throws Exception {
        archetype = parse("/adl2-tests/validity/rm_checking/openEHR-EHR-EVALUATION.VCARM_rm_non_existent_attribute.v1.0.0.adls");
        ValidationResult validationResult = new ArchetypeValidator(models).validate(archetype);
        List<ValidationMessage> messages = validationResult.getErrors();
        System.out.println(messages);
        assertEquals(messages.toString(), 1, messages.size());
        assertEquals(ErrorType.VCARM, messages.get(0).getType());
        assertNull(validationResult.getFlattened());
    }

    @Test
    public void VCARMNonExistantTypeAlwaysFlatten() throws Exception {
        archetype = parse("/adl2-tests/validity/rm_checking/openEHR-EHR-EVALUATION.VCARM_rm_non_existent_attribute.v1.0.0.adls");
        InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
        repository.addArchetype(archetype);
        ArchetypeValidationSettings settings = new ArchetypeValidationSettings();
        settings.setAlwaysTryToFlatten(true);
        repository.setArchetypeValidationSettings(settings);
        ValidationResult validationResult = new ArchetypeValidator(models).validate(archetype, repository);
        assertOneError(validationResult, ErrorType.VCARM);
        assertNotNull(validationResult.getFlattened());
    }

    @Test
    public void VCORMTNonConformingType2() throws Exception {
        archetype = parse("/adl2-tests/validity/rm_checking/openEHR-EHR-OBSERVATION.VCORMT_rm_non_conforming_type2.v1.0.0.adls");
        ValidationResult validationResult = new ArchetypeValidator(models).validate(archetype);
        List<ValidationMessage> messages = validationResult.getErrors();
        System.out.println(messages);
        assertEquals(messages.toString(), 1, messages.size());
        assertEquals(ErrorType.VCORMT, messages.get(0).getType());
    }

    @Test
    public void VACDFidCodeNotPresent() throws Exception {
        archetype = parse("/adl2-tests/validity/consistency/openEHR-TEST_PKG-ENTRY.VACDF_ac_code_in_definition_not_in_terminology.v1.0.0.adls");
        ValidationResult validationResult = new ArchetypeValidator(models).validate(archetype);
        assertOneError(validationResult, ErrorType.VACDF);

    }

    private void assertOneError(ValidationResult validationResult, ErrorType vacdf) {
        assertFalse(validationResult.toString(), validationResult.passes());
        List<ValidationMessage> messages = validationResult.getErrors();

        assertEquals(validationResult.toString(), 1, messages.size());
        assertEquals(validationResult.toString(), vacdf, messages.get(0).getType());
    }

    @Test
    public void VATDFatCodeNotPresent() throws Exception {
        archetype = parse("/adl2-tests/validity/consistency/openEHR-TEST_PKG-ENTRY.VATDF_at_code_in_ordinal_not_in_terminology.v1.0.0.adls");
        ValidationResult validationResult = new ArchetypeValidator(models).validate(archetype);
        assertOneError(validationResult, ErrorType.VATDF);

    }

    @Test
    public void tupleMemberSizeMismatch() throws Exception {
        archetype = parse("openEHR-EHR-CLUSTER.invalid_tuple_1.v1.0.0.adls");
        ValidationResult validationResult = new ArchetypeValidator(models).validate(archetype);
        List<ValidationMessage> messages = validationResult.getErrors();
        System.out.println(messages);
        assertEquals(2, messages.size());
        assertEquals(ErrorType.OTHER, messages.get(0).getType());
        assertTrue("message should complain about tuple members being incorrect", messages.get(0).getMessage().contains("In the attribute tuple 3 members were specified, but the primitive tuple has 2 members instead"));
    }

    @Test
    public void tupleMemberTypeMismatch() throws Exception {
        archetype = parse("openEHR-EHR-CLUSTER.invalid_tuple_2.v1.0.0.adls");
        ValidationResult validationResult = new ArchetypeValidator(models).validate(archetype);
        assertOneError(validationResult, ErrorType.VCARM);
    }

    @Test
    public void tuplePrimitiveTypeMismatch() throws Exception {
        archetype = parse("openEHR-EHR-CLUSTER.invalid_tuple_3.v1.0.0.adls");
        ValidationResult validationResult = new ArchetypeValidator(models).validate(archetype);
        assertOneError(validationResult, ErrorType.VCORMT);
    }


    @Test
    public void slotFillerWithIncludes() throws Exception {
        Archetype parent = parse("/adl2-tests/validity/slots/openEHR-EHR-SECTION.slot_parent.v1.0.0.adls");

        Archetype childArchetype = parse("/com/nedap/archie/archetypevalidator/openEHR-EHR-SECTION.slot_id_correct.v1.0.0.adls");
        Archetype invalidChildArchetype = parse("/adl2-tests/validity/slots/openEHR-EHR-SECTION.VARXS_slot_id_mismatch.v1.0.0.adls");
        Archetype included = parse("/adl2-tests/features/specialisation/openEHR-EHR-OBSERVATION.redefine_1_value.v1.0.0.adls");
        Archetype parentOfIncluded = parse("/adl2-tests/features/specialisation/openEHR-EHR-OBSERVATION.spec_test_parent.v1.0.0.adls");
        InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
        repository.addArchetype(parent);
        repository.addArchetype(childArchetype);
        repository.addArchetype(included);
        repository.addArchetype(parentOfIncluded);
        repository.addArchetype(parse("/adl2-tests/features/reference_model/generic_types/openEHR-EHR-OBSERVATION.rm_correct_generic.v1.0.0.adls"));
        {
            ValidationResult validationResult = new ArchetypeValidator(models).validate(invalidChildArchetype, repository);
            assertOneError(validationResult, ErrorType.VARXS);
        }
        {
            ValidationResult validationResult = new ArchetypeValidator(models).validate(childArchetype, repository);
            assertTrue(validationResult.toString(), validationResult.passes());
        }
    }

    @Test
    public void correctSlotFiller() throws Exception {
        Archetype parent = parse("/adl2-tests/validity/slots/openEHR-EHR-SECTION.slot_parent.v1.0.0.adls");
        Archetype childArchetype = parse("/com/nedap/archie/archetypevalidator/openEHR-EHR-SECTION.slot_id_correct.v1.0.0.adls");
        Archetype included = parse("/adl2-tests/features/specialisation/openEHR-EHR-OBSERVATION.redefine_1_value.v1.0.0.adls");
        Archetype parentOfIncluded = parse("/adl2-tests/features/specialisation/openEHR-EHR-OBSERVATION.spec_test_parent.v1.0.0.adls");
        InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
        repository.addArchetype(parent);
        repository.addArchetype(childArchetype);
        repository.addArchetype(included);
        repository.addArchetype(parentOfIncluded);
        repository.addArchetype(parse("/adl2-tests/features/reference_model/generic_types/openEHR-EHR-OBSERVATION.rm_correct_generic.v1.0.0.adls"));

        ValidationResult validationResult = new ArchetypeValidator(models).validate(childArchetype, repository);
        assertTrue(validationResult.toString(), validationResult.passes());
    }

    @Test
    public void slotFillerWithExcludes() throws Exception {
        Archetype parent = parse("/com/nedap/archie/archetypevalidator/openEHR-EHR-SECTION.slot_parent_excludes.v1.0.0.adls");
        Archetype childArchetype = parse("/com/nedap/archie/archetypevalidator/openEHR-EHR-SECTION.slot_excludes_correct.v1.0.0.adls");
        Archetype incorrectChildArchetype = parse("/com/nedap/archie/archetypevalidator/openEHR-EHR-SECTION.slot_excludes_incorrect.v1.0.0.adls");
        Archetype included = parse("/adl2-tests/features/specialisation/openEHR-EHR-OBSERVATION.redefine_1_value.v1.0.0.adls");
        Archetype parentOfIncluded = parse("/adl2-tests/features/specialisation/openEHR-EHR-OBSERVATION.spec_test_parent.v1.0.0.adls");
        InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
        repository.addArchetype(parent);
        repository.addArchetype(childArchetype);
        repository.addArchetype(included);
        repository.addArchetype(parentOfIncluded);
        repository.addArchetype(parse("/adl2-tests/features/reference_model/generic_types/openEHR-EHR-OBSERVATION.rm_correct_generic.v1.0.0.adls"));
        {
            ValidationResult validationResult = new ArchetypeValidator(models).validate(childArchetype, repository);
            assertTrue(validationResult.toString(), validationResult.passes());
        }

        {
            ValidationResult validationResult = new ArchetypeValidator(models).validate(incorrectChildArchetype, repository);
            assertOneError(validationResult, ErrorType.VARXS);
        }
    }


    private Archetype parse(String filename) throws IOException, ADLParseException {
        archetype = parser.parse(ArchetypeValidatorTest.class.getResourceAsStream(filename));
        assertTrue(parser.getErrors().toString(), parser.getErrors().hasNoErrors());
        return archetype;
    }

}
