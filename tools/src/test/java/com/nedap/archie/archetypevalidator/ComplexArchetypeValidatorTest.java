package com.nedap.archie.archetypevalidator;

import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.rminfo.ReferenceModels;
import org.junit.Before;
import org.junit.Test;
import org.openehr.referencemodels.AllMetaModelsInitialiser;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ComplexArchetypeValidatorTest {
    private ReferenceModels models;
    private static Archetype simpleTestCluster;
    private static Archetype simpleTestObservation;
    private static Archetype observationTemplateWithOverlay;
    private static Archetype specializedObservationTemplateWithOverlay;
    private static Archetype compositionWithIncludedTemplate;

    private static InMemoryFullArchetypeRepository repository;

    @Before
    public void setup() throws Exception {
        models = AllMetaModelsInitialiser.getNativeRms();

        simpleTestCluster = new ADLParser().parse(ComplexArchetypeValidatorTest.class.getResourceAsStream("openEHR-EHR-CLUSTER.simple_test_cluster.v1.0.0.adls"));
        simpleTestObservation = new ADLParser().parse(ComplexArchetypeValidatorTest.class.getResourceAsStream("openEHR-EHR-OBSERVATION.simple_test_observation.v1.0.0.adls"));
        observationTemplateWithOverlay = new ADLParser().parse(ComplexArchetypeValidatorTest.class.getResourceAsStream("openEHR-EHR-OBSERVATION.specialized_template_observation.v1.0.0.adls"));
        specializedObservationTemplateWithOverlay = new ADLParser().parse(ComplexArchetypeValidatorTest.class.getResourceAsStream("openEHR-EHR-OBSERVATION.specialized_specialized_template_observation.v1.0.0.adls"));
        compositionWithIncludedTemplate = new ADLParser().parse(ComplexArchetypeValidatorTest.class.getResourceAsStream("openEHR-EHR-COMPOSITION.composition_with_included_template.v1.0.0.adls"));

        repository = new InMemoryFullArchetypeRepository();

        repository.addArchetype(simpleTestCluster);
        repository.addArchetype(simpleTestObservation);
        repository.addArchetype(observationTemplateWithOverlay);
        repository.addArchetype(specializedObservationTemplateWithOverlay);
        repository.addArchetype(compositionWithIncludedTemplate);
    }

    @Test
    public void testTemplateWithOverlayAsUseArchetype() {
        ValidationResult validationResult = new ArchetypeValidator(models).validate(compositionWithIncludedTemplate, repository);
        List<ValidationMessage> messages = validationResult.getErrors();
        assertEquals(0, messages.size());
    }

    @Test
    public void testSpecializedTemplateWithOverlay() {
        ValidationResult validationResult = new ArchetypeValidator(models).validate(specializedObservationTemplateWithOverlay, repository);
        List<ValidationMessage> messages = validationResult.getErrors();
        assertEquals(0, messages.size());
    }
}
