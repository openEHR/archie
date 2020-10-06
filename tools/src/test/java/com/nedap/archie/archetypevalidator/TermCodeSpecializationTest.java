package com.nedap.archie.archetypevalidator;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class TermCodeSpecializationTest {

    @Test
    public void requiredBindingStrength() throws IOException {
        Archetype parent = TestUtil.parseFailOnErrors("/com/nedap/archie/archetypevalidator/primitives/openEHR-EHR-CLUSTER.constraint_strength_parent.v1.0.0.adls");
        Archetype child = TestUtil.parseFailOnErrors("/com/nedap/archie/archetypevalidator/primitives/openEHR-EHR-CLUSTER.constraint_strength_valid_child.v1.0.0.adls");
        InMemoryFullArchetypeRepository repo = new InMemoryFullArchetypeRepository();
        repo.addArchetype(parent);
        repo.addArchetype(child);
        ArchetypeValidator archetypeValidator = new ArchetypeValidator(BuiltinReferenceModels.getMetaModels());
        repo.compile(archetypeValidator);
        for(ValidationResult validationResult:repo.getAllValidationResults()) {
            assertTrue(validationResult.toString(), validationResult.passes());
        }

    }
}
