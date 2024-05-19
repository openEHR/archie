package com.nedap.archie.archetypevalidator;

import com.nedap.archie.flattener.FullArchetypeRepository;
import com.nedap.archie.openehr.rminfo.OpenEhrRmInfoLookup;
import com.nedap.archie.rminfo.MetaModels;
import com.nedap.archie.rminfo.ReferenceModels;
import com.nedap.archie.test.CkmRepositoryBuilder;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Test;
import org.openehr.referencemodels.AllMetaModelsInitialiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

/**
 * False positive test for the archetype validator:
 *
 * Runs the archetype validator with the full CKM as input. The CKM has a few archetypes with errors.
 * Those have been checked with the ADL workbench to also fail validation with the same errors and are ignored
 *
 * If any other errors occurr - warnings do not count - this test fails.
 */
public class CKMArchetypeValidatorTest {

    private static final Logger logger = LoggerFactory.getLogger(CKMArchetypeValidatorTest.class);

    private static Set<String> archetypesWithKnownErrors = new HashSet<>();
    static {
        archetypesWithKnownErrors.add("openEHR-DEMOGRAPHIC-PERSON.person-patient.v1.0.0");
        archetypesWithKnownErrors.add("openEHR-EHR-CLUSTER.move-joint.v1.0.0");
        archetypesWithKnownErrors.add("openEHR-EHR-OBSERVATION.braden_scale-child.v1.0.0");
        archetypesWithKnownErrors.add("openEHR-EHR-CLUSTER.move-spine.v1.0.0");
        archetypesWithKnownErrors.add("openEHR-EHR-INSTRUCTION.request-procedure.v0.0.1-alpha");
        archetypesWithKnownErrors.add("openEHR-EHR-OBSERVATION.visual_acuity.v0.0.1-alpha");
        archetypesWithKnownErrors.add("openEHR-EHR-OBSERVATION.substance_use-caffeine.v1.0.0");
        archetypesWithKnownErrors.add("openEHR-DEMOGRAPHIC-PARTY_IDENTITY.person_name-individual_provider.v1.0.0");
        //archetypesWithKnownErrors.add("openEHR-EHR-OBSERVATION.apgar.v1.0.1-alpha");//not sure if this is wrong, don't have ADL workbench currently running, but it does use_node and in paths does not replace the node id with the new one.
    }


    @Test
    public void fullCKMTest() {

        FullArchetypeRepository repository = CkmRepositoryBuilder.parseCKM();
        ReferenceModels models = new ReferenceModels();
        models.registerModel(OpenEhrRmInfoLookup.getInstance());
        logger.info("archetypes parsed: " + repository.getAllArchetypes().size());
        repository.compile(models);

        runTest(repository);

    }

    @Test
    public void fullCKMTestBmm() {
        MetaModels bmmReferenceModels = new MetaModels(null, AllMetaModelsInitialiser.getBmmRepository(), AllMetaModelsInitialiser.getAomProfiles());

        FullArchetypeRepository repository = CkmRepositoryBuilder.parseCKM();
        logger.info("archetypes parsed: " + repository.getAllArchetypes().size());
        repository.compile(bmmReferenceModels);

        runTest(repository);

    }

    private void runTest(FullArchetypeRepository repository) {
        List<ValidationResult> allValidationResults = repository.getAllValidationResults();
        List<ValidationResult> resultWithErrors = allValidationResults.stream()
                .filter(r -> !r.passes())
                .filter(r -> !archetypesWithKnownErrors.contains(r.getArchetypeId()))
                .collect(Collectors.toList());

        StringBuilder error = new StringBuilder();
        for(ValidationResult result:resultWithErrors) {
            error.append(result);
            error.append("\n\n");
        }

        assertTrue(error.toString(), resultWithErrors.isEmpty());
    }


}
