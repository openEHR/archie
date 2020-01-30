package com.nedap.archie.flattener;

import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.archetypevalidator.ValidationResult;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class InMemoryFullArchetypeRepositoryTest {

    InMemoryFullArchetypeRepository inMemoryFullArchetypeRepository;

    @Before
    public void setup() throws IOException {
        //Create versioned archetypes
        List<Archetype> archetypes = new ArrayList<>();
        archetypes.add(new ADLParser().parse(
                InMemoryFullArchetypeRepositoryTest.class.
                        getResourceAsStream("openEHR-EHR-OBSERVATION.blood_pressure.v1.adls")));
        archetypes.add(new ADLParser().parse(
                InMemoryFullArchetypeRepositoryTest.class.
                        getResourceAsStream("openEHR-EHR-OBSERVATION.blood_pressure.v1.0.1.adls")));
        archetypes.add(new ADLParser().parse(
                InMemoryFullArchetypeRepositoryTest.class.
                        getResourceAsStream("openEHR-EHR-OBSERVATION.blood_pressure.v1.0.11.adls")));
        archetypes.add(new ADLParser().parse(
                InMemoryFullArchetypeRepositoryTest.class.
                        getResourceAsStream("openEHR-EHR-OBSERVATION.blood_pressure.v1.1.0.adls")));
        archetypes.add(new ADLParser().parse(
                InMemoryFullArchetypeRepositoryTest.class.
                        getResourceAsStream("openEHR-EHR-OBSERVATION.blood_pressure.v1.11.0.adls")));
        archetypes.add(new ADLParser().parse(
                InMemoryFullArchetypeRepositoryTest.class.
                        getResourceAsStream("openEHR-EHR-OBSERVATION.blood_pressure.v2.0.0.adls")));

        //Create ValidationResults from versioned archetypes
        List<ValidationResult> validationResults = archetypes.
                stream().
                map(ValidationResult::new).
                collect(Collectors.toList());

        //Create OperationalTemplates
        List<OperationalTemplate> operationalTemplates = archetypes.
                stream().
                map(OperationalTemplateCreator::createOperationalTemplate).
                collect(Collectors.toList());

        //Fill InMemoryFullArchetypeRepository
        inMemoryFullArchetypeRepository = new InMemoryFullArchetypeRepository();
        for (int i = 0; i < archetypes.size(); ++i) {
            inMemoryFullArchetypeRepository.addArchetype(archetypes.get(i));
            inMemoryFullArchetypeRepository.setFlattenedArchetype(archetypes.get(i));
            inMemoryFullArchetypeRepository.setValidationResult(validationResults.get(i));
            inMemoryFullArchetypeRepository.setOperationalTemplate(operationalTemplates.get(i));
        }
    }

    @Test
    public void getArchetype() {
        assertEquals("openEHR-EHR-OBSERVATION.blood_pressure.v2.0.0",
                inMemoryFullArchetypeRepository.getArchetype("openEHR-EHR-OBSERVATION.blood_pressure").getArchetypeId().getFullId());
        assertEquals("openEHR-EHR-OBSERVATION.blood_pressure.v2.0.0",
                inMemoryFullArchetypeRepository.getArchetype("openEHR-EHR-OBSERVATION.blood_pressure.v").getArchetypeId().getFullId());
        assertEquals("openEHR-EHR-OBSERVATION.blood_pressure.v1.11.0",
                inMemoryFullArchetypeRepository.getArchetype("openEHR-EHR-OBSERVATION.blood_pressure.v1").getArchetypeId().getFullId());
        assertEquals("openEHR-EHR-OBSERVATION.blood_pressure.v1.0.11",
                inMemoryFullArchetypeRepository.getArchetype("openEHR-EHR-OBSERVATION.blood_pressure.v1.0").getArchetypeId().getFullId());
        assertEquals("openEHR-EHR-OBSERVATION.blood_pressure.v1.0.11",
                inMemoryFullArchetypeRepository.getArchetype("openEHR-EHR-OBSERVATION.blood_pressure.v1.0").getArchetypeId().getFullId());
        assertEquals("openEHR-EHR-OBSERVATION.blood_pressure.v1.1.0",
                inMemoryFullArchetypeRepository.getArchetype("openEHR-EHR-OBSERVATION.blood_pressure.v1.1.0").getArchetypeId().getFullId());
    }

    @Test
    public void getFlattenedArchetype() {
        assertEquals("openEHR-EHR-OBSERVATION.blood_pressure.v2.0.0",
                inMemoryFullArchetypeRepository.getFlattenedArchetype("openEHR-EHR-OBSERVATION.blood_pressure").getArchetypeId().getFullId());
        assertEquals("openEHR-EHR-OBSERVATION.blood_pressure.v2.0.0",
                inMemoryFullArchetypeRepository.getFlattenedArchetype("openEHR-EHR-OBSERVATION.blood_pressure.v").getArchetypeId().getFullId());
        assertEquals("openEHR-EHR-OBSERVATION.blood_pressure.v1.11.0",
                inMemoryFullArchetypeRepository.getFlattenedArchetype("openEHR-EHR-OBSERVATION.blood_pressure.v1").getArchetypeId().getFullId());
        assertEquals("openEHR-EHR-OBSERVATION.blood_pressure.v1.0.11",
                inMemoryFullArchetypeRepository.getFlattenedArchetype("openEHR-EHR-OBSERVATION.blood_pressure.v1.0").getArchetypeId().getFullId());
        assertEquals("openEHR-EHR-OBSERVATION.blood_pressure.v1.0.11",
                inMemoryFullArchetypeRepository.getFlattenedArchetype("openEHR-EHR-OBSERVATION.blood_pressure.v1.0").getArchetypeId().getFullId());
        assertEquals("openEHR-EHR-OBSERVATION.blood_pressure.v1.1.0",
                inMemoryFullArchetypeRepository.getFlattenedArchetype("openEHR-EHR-OBSERVATION.blood_pressure.v1.1.0").getArchetypeId().getFullId());
    }

    @Test
    public void getOperationalTemplate() {
        assertEquals("openEHR-EHR-OBSERVATION.blood_pressure.v2.0.0",
                inMemoryFullArchetypeRepository.getOperationalTemplate("openEHR-EHR-OBSERVATION.blood_pressure").getArchetypeId().getFullId());
        assertEquals("openEHR-EHR-OBSERVATION.blood_pressure.v2.0.0",
                inMemoryFullArchetypeRepository.getOperationalTemplate("openEHR-EHR-OBSERVATION.blood_pressure.v").getArchetypeId().getFullId());
        assertEquals("openEHR-EHR-OBSERVATION.blood_pressure.v1.11.0",
                inMemoryFullArchetypeRepository.getOperationalTemplate("openEHR-EHR-OBSERVATION.blood_pressure.v1").getArchetypeId().getFullId());
        assertEquals("openEHR-EHR-OBSERVATION.blood_pressure.v1.0.11",
                inMemoryFullArchetypeRepository.getOperationalTemplate("openEHR-EHR-OBSERVATION.blood_pressure.v1.0").getArchetypeId().getFullId());
        assertEquals("openEHR-EHR-OBSERVATION.blood_pressure.v1.0.11",
                inMemoryFullArchetypeRepository.getOperationalTemplate("openEHR-EHR-OBSERVATION.blood_pressure.v1.0").getArchetypeId().getFullId());
        assertEquals("openEHR-EHR-OBSERVATION.blood_pressure.v1.1.0",
                inMemoryFullArchetypeRepository.getOperationalTemplate("openEHR-EHR-OBSERVATION.blood_pressure.v1.1.0").getArchetypeId().getFullId());
    }

    @Test
    public void getValidationResult() {
        assertEquals("openEHR-EHR-OBSERVATION.blood_pressure.v2.0.0",
                inMemoryFullArchetypeRepository.getValidationResult("openEHR-EHR-OBSERVATION.blood_pressure").getArchetypeId());
        assertEquals("openEHR-EHR-OBSERVATION.blood_pressure.v2.0.0",
                inMemoryFullArchetypeRepository.getValidationResult("openEHR-EHR-OBSERVATION.blood_pressure.v").getArchetypeId());
        assertEquals("openEHR-EHR-OBSERVATION.blood_pressure.v1.11.0",
                inMemoryFullArchetypeRepository.getValidationResult("openEHR-EHR-OBSERVATION.blood_pressure.v1").getArchetypeId());
        assertEquals("openEHR-EHR-OBSERVATION.blood_pressure.v1.0.11",
                inMemoryFullArchetypeRepository.getValidationResult("openEHR-EHR-OBSERVATION.blood_pressure.v1.0").getArchetypeId());
        assertEquals("openEHR-EHR-OBSERVATION.blood_pressure.v1.0.11",
                inMemoryFullArchetypeRepository.getValidationResult("openEHR-EHR-OBSERVATION.blood_pressure.v1.0").getArchetypeId());
        assertEquals("openEHR-EHR-OBSERVATION.blood_pressure.v1.1.0",
                inMemoryFullArchetypeRepository.getValidationResult("openEHR-EHR-OBSERVATION.blood_pressure.v1.1.0").getArchetypeId());
    }
}
