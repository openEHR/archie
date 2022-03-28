package com.nedap.archie.flattener;

import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.rminfo.ReferenceModels;
import org.junit.Before;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

public class ComplexFlattenerTest {
    private static Archetype report;
    private static Archetype reportResult;

    private static Archetype labTestResult;
    private static Archetype labTestAnalyte;
    private static Archetype bloodSugarObservationTemplate;
    private static Archetype bloodSugarIncludeTemplateWithOverlay;
    private static SimpleArchetypeRepository repository;

    private ReferenceModels models;

    private Flattener flattener;

    @Before
    public void setup() throws Exception {
        models = BuiltinReferenceModels.getAvailableModelInfoLookups();

        report = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-COMPOSITION.report.v1.adls"));
        reportResult = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-COMPOSITION.report-result.v1.adls"));

        labTestResult = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-OBSERVATION.laboratory_test_result.v1.2.2.adls"));
        labTestAnalyte = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-CLUSTER.laboratory_test_analyte.v1.1.5.adls"));
        bloodSugarObservationTemplate = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-OBSERVATION.bloedsuiker.v1.0.0.adls"));
        bloodSugarIncludeTemplateWithOverlay = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-COMPOSITION.bloedglucose_composition.v1.0.0.adls"));

        repository = new InMemoryFullArchetypeRepository();
        repository.addArchetype(report);
        repository.addArchetype(reportResult);

        repository.addArchetype(labTestResult);
        repository.addArchetype(labTestAnalyte);
        repository.addArchetype(bloodSugarObservationTemplate);
        repository.addArchetype(bloodSugarIncludeTemplateWithOverlay);

        flattener = new Flattener(repository, models).createOperationalTemplate(true);
    }

    @Test
    public void testTemplateWithOverlayAsUseArchetype() {
        Archetype flattened = flattener.flatten(bloodSugarIncludeTemplateWithOverlay);
    }
}
