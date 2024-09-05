package org.s2.archie.archetypevalidator;

import com.nedap.archie.archetypevalidator.ValidationResult;
import com.nedap.archie.flattener.FullArchetypeRepository;
import com.nedap.archie.rminfo.MetaModels;
import com.nedap.archie.rminfo.ReferenceModels;
import com.nedap.archie.testutil.ArchetypeRepositoryBuilder;
import org.junit.Test;
import org.openehr.referencemodels.AllMetaModelsInitialiser;
import org.s2.rminfo.S2RmInfoLookup;
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
public class S2ModelsArchetypeValidatorTest {

    private static final Logger logger = LoggerFactory.getLogger(S2ModelsArchetypeValidatorTest.class);

    private static Set<String> archetypesWithKnownErrors = new HashSet<>();
    static {
        archetypesWithKnownErrors.add("s2-EHR-Direct_observation.demo.v1.0.0");
        archetypesWithKnownErrors.add("s2-EHR-Direct_observation.infant_feeding.v0.0.1-alpha");
        archetypesWithKnownErrors.add("s2-EHR-Direct_observation.visual_acuity.v0.0.1-alpha");
        archetypesWithKnownErrors.add("s2-EHR-Score.apgar.v2.0.2");
        archetypesWithKnownErrors.add("s2-EHR-Section.diagnostic_reports.v0.0.1-alpha");
        archetypesWithKnownErrors.add("s2-EHR-Node.exclusion_symptom_sign.v0.0.1-alpha");

        // plus more
    }


    @Test
    public void fullRepoTestRm() {

        FullArchetypeRepository repository = ArchetypeRepositoryBuilder.parseRepository(this.getClass(), "s2-models");
        ReferenceModels models = new ReferenceModels();
        models.registerModel(S2RmInfoLookup.getInstance());
        logger.info("archetypes parsed: " + repository.getAllArchetypes().size());
        repository.compile(models);

        runTest(repository);
    }

    @Test
    public void fullRepoTestBmm() {
        MetaModels bmmReferenceModels = new MetaModels(null, AllMetaModelsInitialiser.getBmmRepository(), AllMetaModelsInitialiser.getAomProfiles());

        FullArchetypeRepository repository = ArchetypeRepositoryBuilder.parseRepository(this.getClass(), "s2-models");
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
