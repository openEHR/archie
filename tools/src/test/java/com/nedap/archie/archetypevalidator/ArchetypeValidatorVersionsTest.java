package com.nedap.archie.archetypevalidator;

import com.nedap.archie.adlparser.ADLParseException;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Test;
import org.openehr.referencemodels.AllMetaModelsInitialiser;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class ArchetypeValidatorVersionsTest {

    @Test
    public void testMultipleVersions() throws IOException, ADLParseException {

        Archetype parentv1 = TestUtil.parseFailOnErrors(this.getClass(),"/com/nedap/archie/archetypevalidator/openEHR-EHR-CLUSTER.parent.v1.0.0.adls");
        Archetype parentv11 = TestUtil.parseFailOnErrors(this.getClass(),"/com/nedap/archie/archetypevalidator/openEHR-EHR-CLUSTER.parent.v1.1.0.adls");
        Archetype child = TestUtil.parseFailOnErrors(this.getClass(),"/com/nedap/archie/archetypevalidator/openEHR-EHR-CLUSTER.child.v0.0.1.adls");
        InMemoryFullArchetypeRepository repo = new InMemoryFullArchetypeRepository();
        repo.addArchetype(parentv1);
        repo.addArchetype(parentv11);
        repo.addArchetype(child);
        ArchetypeValidator archetypeValidator = new ArchetypeValidator(AllMetaModelsInitialiser.getMetaModels());
        //the order of validation is important for this test!
        //first validate the first parent, which is valid
        assertTrue(archetypeValidator.validate(parentv1, repo).passes());
        //now validate the child, which refers to parent.v1 , and is valid against parentv11, but not against parentv1.
        //the validator should automatically detect that v1.1 is newer, and should be used as the parent, not not parentv1
        //and create a new ValidationResult instead of reusing parent v1
        ValidationResult childValidation = archetypeValidator.validate(child, repo);
        assertTrue(childValidation.getErrors().toString(), childValidation.passes());
        //and of course this should be valid as well.
        assertTrue(archetypeValidator.validate(parentv11, repo).passes());

    }

    @Test
    public void testMultipleVersionsFullyCompile() throws Exception {
        //same test as above, but with the 'compile this repo at once' method
        Archetype parentv1 = TestUtil.parseFailOnErrors(this.getClass(),"/com/nedap/archie/archetypevalidator/openEHR-EHR-CLUSTER.parent.v1.0.0.adls");
        Archetype parentv11 = TestUtil.parseFailOnErrors(this.getClass(),"/com/nedap/archie/archetypevalidator/openEHR-EHR-CLUSTER.parent.v1.1.0.adls");
        Archetype child = TestUtil.parseFailOnErrors(this.getClass(),"/com/nedap/archie/archetypevalidator/openEHR-EHR-CLUSTER.child.v0.0.1.adls");
        InMemoryFullArchetypeRepository repo = new InMemoryFullArchetypeRepository();
        repo.addArchetype(parentv1);
        repo.addArchetype(parentv11);
        repo.addArchetype(child);
        repo.compile(new ArchetypeValidator(AllMetaModelsInitialiser.getMetaModels()));
        for(ValidationResult result:repo.getAllValidationResults()) {
            assertTrue(result.getErrors().toString(), result.passes());
        }
    }
}
