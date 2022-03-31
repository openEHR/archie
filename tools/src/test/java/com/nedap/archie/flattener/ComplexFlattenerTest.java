package com.nedap.archie.flattener;

import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.rminfo.ReferenceModels;
import org.junit.Before;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

public class ComplexFlattenerTest {
    private static Archetype simpleTestCluster;
    private static Archetype simpleTestObservation;
    private static Archetype observationTemplateWithOverlay;
    private static Archetype compositionWithIncludedTemplate;

    private static SimpleArchetypeRepository repository;


    private Flattener flattener;

    @Before
    public void setup() throws Exception {
        ReferenceModels models = BuiltinReferenceModels.getAvailableModelInfoLookups();

        simpleTestCluster = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-CLUSTER.simple_test_cluster.v1.0.0.adls"));
        simpleTestObservation = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-OBSERVATION.simple_test_observation.v1.0.0.adls"));
        observationTemplateWithOverlay = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-OBSERVATION.specialized_template_observation.v1.0.0.adls"));
        compositionWithIncludedTemplate = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-COMPOSITION.composition_with_included_template.v1.0.0.adls"));

        repository = new InMemoryFullArchetypeRepository();

        repository.addArchetype(simpleTestCluster);
        repository.addArchetype(simpleTestObservation);
        repository.addArchetype(observationTemplateWithOverlay);
        repository.addArchetype(compositionWithIncludedTemplate);

        flattener = new Flattener(repository, models).createOperationalTemplate(true);
    }

    @Test
    public void testTemplateWithOverlayAsUseArchetype() {
        Archetype flattened = flattener.flatten(compositionWithIncludedTemplate);
    }
}
