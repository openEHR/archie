package com.nedap.archie.archetypevalidator;

import com.nedap.archie.adlparser.ADLParseException;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.openehr.rminfo.OpenEhrTestRmInfoLookup;
import com.nedap.archie.openehr.rminfo.OpenEhrRmInfoLookup;
import com.nedap.archie.rminfo.ReferenceModels;
import org.junit.Before;
import org.junit.Test;
import org.openehr.referencemodels.AllMetaModelsInitialiser;

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
        models.registerModel(OpenEhrRmInfoLookup.getInstance());
        models.registerModel(OpenEhrTestRmInfoLookup.getInstance());
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

    @Test
    public void parentWithDifferentRmRelease() throws IOException, ADLParseException {
        //the parent has an older RM Release than the parent, and the child contains a couple of new features
        //not available in the parent. One could say the parent needs to be upgraded to the new version
        //however this has some actual use cases where the parent in the CKM still has the old version, and you want
        //to use new features like DV_SCALE - as in this particular example. So this should just work.
        Archetype child = parse("/com/nedap/archie/archetypevalidator/rm_version/openEHR-EHR-CLUSTER.child.v0.0.1.adls");
        Archetype parent = parse("/com/nedap/archie/archetypevalidator/rm_version/openEHR-EHR-CLUSTER.parent.v1.1.0.adls");
        {
            InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
            repository.addArchetype(child);
            repository.addArchetype(parent);

            ArchetypeValidator archetypeValidator = new ArchetypeValidator(AllMetaModelsInitialiser.getMetaModels());
            ValidationResult validatedChild = archetypeValidator.validate(child, repository);
            ValidationResult validatedParent = archetypeValidator.validate(parent, repository);

            assertTrue(validatedChild.getErrors().toString(), validatedChild.passes());
            assertTrue(validatedParent.getErrors().toString(), validatedParent.passes());
        }
        {
            InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
            repository.addArchetype(child);
            repository.addArchetype(parent);
            
            ArchetypeValidator archetypeValidator = new ArchetypeValidator(AllMetaModelsInitialiser.getMetaModels());
            ValidationResult validatedParent = archetypeValidator.validate(parent, repository);
            ValidationResult validatedChild = archetypeValidator.validate(child, repository);

            assertTrue(validatedChild.getErrors().toString(), validatedChild.passes());
            assertTrue(validatedParent.getErrors().toString(), validatedParent.passes());
        }
    }

    @Test
    public void specialisationWithRedefinedAndExcludedMandatoryMultiNode() throws Exception {
        Archetype parentArchetype = parse("/com/nedap/archie/archetypevalidator/openEHR-EHR-CLUSTER.parent_with_mandatory_elements.v1.0.0.adls");
        Archetype childArchetype = parse("/com/nedap/archie/archetypevalidator/openEHR-EHR-CLUSTER.child_redefines_mandatory_elements.v1.0.0.adls");
        Archetype grandchildArchetype = parse("/com/nedap/archie/archetypevalidator/openEHR-EHR-CLUSTER.grandchild_redefines_mandatory_elements.v1.0.0.adls");

        InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
        repository.addArchetype(parentArchetype);
        repository.addArchetype(childArchetype);
        repository.addArchetype(grandchildArchetype);

        {
            ValidationResult validationResult = new ArchetypeValidator(models).validate(childArchetype, repository);
            assertTrue(validationResult.toString(), validationResult.passes());
        }
        {
            ValidationResult validationResult = new ArchetypeValidator(models).validate(grandchildArchetype, repository);
            assertTrue(validationResult.toString(), validationResult.passes());
        }
    }

    @Test
    public void specialisationWithRedefinedAndExcludedMandatoryMultiNodeFails() throws Exception {
        Archetype parentArchetype = parse("/com/nedap/archie/archetypevalidator/openEHR-EHR-CLUSTER.parent_with_mandatory_elements.v1.0.0.adls");
        Archetype childArchetype = parse("/com/nedap/archie/archetypevalidator/openEHR-EHR-CLUSTER.child_redefines_mandatory_elements_fails.v1.0.0.adls");

        InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
        repository.addArchetype(parentArchetype);
        repository.addArchetype(childArchetype);

        {
            ValidationResult validationResult = new ArchetypeValidator(models).validate(childArchetype, repository);
            assertFalse(validationResult.toString(), validationResult.passes());
            assertEquals("Occurrences 3..5, which is the sum of 2..3, 1..2, 0..0, does not conform to 1..4", validationResult.getErrors().get(0).getMessage());
        }
    }

    @Test
    public void specialisationOfDvTextToDvTextAndDvCodedText() throws Exception {
        Archetype parentArchetype = parse("/com/nedap/archie/archetypevalidator/openEHR-EHR-CLUSTER.parent.v1.2.0.adls");
        Archetype childArchetype = parse("/com/nedap/archie/archetypevalidator/openEHR-EHR-CLUSTER.child_redefines_dvtext.v1.0.0.adls");

        InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
        repository.addArchetype(parentArchetype);
        repository.addArchetype(childArchetype);

        {
            ValidationResult validationResult = new ArchetypeValidator(models).validate(childArchetype, repository);
            assertTrue(validationResult.toString(), validationResult.passes());
        }
    }

    @Test
    public void infiniteSpecialisationTreeLoopTest() throws IOException, ADLParseException {
        Archetype child1 = parse("/com/nedap/archie/archetypevalidator/openEHR-EHR-CLUSTER.infinite_loop_child1.v0.0.1.adls");
        Archetype child2 = parse("/com/nedap/archie/archetypevalidator/openEHR-EHR-CLUSTER.infinite_loop_child2.v0.0.1.adls");

        {
            InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
            repository.addArchetype(child1);
            repository.addArchetype(child2);

            ArchetypeValidator archetypeValidator = new ArchetypeValidator(AllMetaModelsInitialiser.getMetaModels());
            ValidationResult result = archetypeValidator.validate(child1, repository);
            assertFalse(result.passes());
            assertEquals("Infinite loop caused by specialising: openEHR-EHR-CLUSTER.infinite_loop_child1.v0.0.1 in openEHR-EHR-CLUSTER.infinite_loop_child2.v0.0.1", result.getErrors().get(0).getMessage());
        }
    }

    @Test
    public void specializationAfterExclusionTest() throws IOException, ADLParseException {
        Archetype parent = parse("/com/nedap/archie/flattener/siblingorder/openEHR-EHR-CLUSTER.siblingorderparent.v1.0.0.adls");
        Archetype childWithSpecializationAfterExclusion = parse("/com/nedap/archie/archetypevalidator/openEHR-EHR-CLUSTER.specialized_nodes_order.v1.0.0.adls");

        {
            InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
            repository.addArchetype(parent);
            repository.addArchetype(childWithSpecializationAfterExclusion);

            ArchetypeValidator archetypeValidator = new ArchetypeValidator(AllMetaModelsInitialiser.getMetaModels());
            ValidationResult result = archetypeValidator.validate(childWithSpecializationAfterExclusion, repository);
            assertTrue(result.passes());
            assertEquals(2, result.getErrors().size());
            assertEquals("Object with node id id5.1 should be specialized before excluding the parent node", result.getErrors().get(0).getMessage());
            assertEquals("Object with node id id7.1 should be specialized before excluding the parent node", result.getErrors().get(1).getMessage());
        }
    }

    @Test
    public void incompatibleNodeIdValidationTest() throws IOException, ADLParseException {
        Archetype archetypeWithIncompatibleNodeId = parse("/adl2-tests/validity/basics/openEHR-EHR-OBSERVATION.WARN_adl14_incompatible_node_ids.v1.0.0.adls");
        {
            InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
            repository.addArchetype(archetypeWithIncompatibleNodeId);
            ArchetypeValidator archetypeValidator = new ArchetypeValidator(AllMetaModelsInitialiser.getMetaModels());
            ValidationResult result = archetypeValidator.validate(archetypeWithIncompatibleNodeId, repository);
            assertTrue(result.passes());
            assertEquals(6, result.getErrors().size());
            assertEquals("Node id at12 already used in archetype as id12 with a different at, id or ac prefix. The archetype will not be convertible to ADL 1.4", result.getErrors().get(0).getMessage());
            assertEquals("Node id at2 already used in archetype as id2 with a different at, id or ac prefix. The archetype will not be convertible to ADL 1.4", result.getErrors().get(1).getMessage());
            assertEquals("Node id at3 already used in archetype as id3 with a different at, id or ac prefix. The archetype will not be convertible to ADL 1.4", result.getErrors().get(2).getMessage());
            assertEquals("Node id at4 already used in archetype as id4 with a different at, id or ac prefix. The archetype will not be convertible to ADL 1.4", result.getErrors().get(3).getMessage());
            assertEquals("Node id ac4 already used in archetype as at4 with a different at, id or ac prefix. The archetype will not be convertible to ADL 1.4", result.getErrors().get(4).getMessage());
            assertEquals("Node id ac12 already used in archetype as at12 with a different at, id or ac prefix. The archetype will not be convertible to ADL 1.4", result.getErrors().get(5).getMessage());
        }
    }

    private Archetype parse(String filename) throws IOException, ADLParseException {
        archetype = parser.parse(ArchetypeValidatorTest.class.getResourceAsStream(filename));
        assertTrue(parser.getErrors().toString(), parser.getErrors().hasNoErrors());
        return archetype;
    }

}
