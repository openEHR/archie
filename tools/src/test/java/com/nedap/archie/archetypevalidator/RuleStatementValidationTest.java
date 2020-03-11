package com.nedap.archie.archetypevalidator;

import com.nedap.archie.flattener.FullArchetypeRepository;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rminfo.MetaModels;
import com.nedap.archie.rminfo.ReferenceModels;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

public class RuleStatementValidationTest {

    private static final Logger logger = LoggerFactory.getLogger(CKMArchetypeValidatorTest.class);

    private static Set<String> archetypesWithKnownErrors = new HashSet<>();
    static {

    }


    @Test
    public void rulesExamplesValidTest() {

        FullArchetypeRepository repository = TestUtil.parseRuleExamples();
        ReferenceModels models = new ReferenceModels();
        models.registerModel(ArchieRMInfoLookup.getInstance());
        logger.info("archetypes parsed: " + repository.getAllArchetypes().size());
        repository.compile(models);

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
