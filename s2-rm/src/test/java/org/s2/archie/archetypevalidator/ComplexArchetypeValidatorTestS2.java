package org.s2.archie.archetypevalidator;

import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.archetypevalidator.ArchetypeValidator;
import com.nedap.archie.archetypevalidator.ValidationMessage;
import com.nedap.archie.archetypevalidator.ValidationResult;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.rminfo.ReferenceModels;
import org.junit.Before;
import org.junit.Test;
import org.s2.rminfo.S2RmInfoLookup;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ComplexArchetypeValidatorTestS2 {
    private ReferenceModels models;
    private static Archetype archNodeDevice;
    private static Archetype archNodeCitation;
    private static Archetype archNodeLevelOfExertion;

    private static Archetype archNodeAdverseReaction;
    private static Archetype archObsBloodPressure;
    private static Archetype archObsHeartRate;
    private static Archetype archObsHeight;
    private static Archetype archObsBodyTemperature;
    private static Archetype archObsRespiratoryRate;
    private static Archetype archObsBmi;
    private static Archetype archCompEncounter;
    private static Archetype tplCompVitalSigns;

    private static InMemoryFullArchetypeRepository repository;

    @Before
    public void setup() throws Exception {
        models = new ReferenceModels();
        models.registerModel(S2RmInfoLookup.getInstance());

        archNodeDevice = new ADLParser().parse(ComplexArchetypeValidatorTestS2.class.getResourceAsStream("s2-EHR-Node.device.v1.1.2.adls"));
        archNodeAdverseReaction = new ADLParser().parse(ComplexArchetypeValidatorTestS2.class.getResourceAsStream("s2-EHR-Node.adverse_reaction_event.v1.0.0.adls"));
        archObsBloodPressure = new ADLParser().parse(ComplexArchetypeValidatorTestS2.class.getResourceAsStream("s2-EHR-Direct_observation.blood_pressure.v2.0.8.adls"));
        archObsHeartRate = new ADLParser().parse(ComplexArchetypeValidatorTestS2.class.getResourceAsStream("s2-EHR-Direct_observation.heart_rate.v1.0.0.adls"));
        archNodeCitation = new ADLParser().parse(ComplexArchetypeValidatorTestS2.class.getResourceAsStream("s2-EHR-Node.citation.v0.0.1-alpha.adls"));
        archNodeLevelOfExertion = new ADLParser().parse(ComplexArchetypeValidatorTestS2.class.getResourceAsStream("s2-EHR-Node.level_of_exertion.v0.0.1-alpha.adls"));

        archObsHeight = new ADLParser().parse(ComplexArchetypeValidatorTestS2.class.getResourceAsStream("s2-EHR-Direct_observation.height.v2.1.0.adls"));
        archObsBodyTemperature = new ADLParser().parse(ComplexArchetypeValidatorTestS2.class.getResourceAsStream("s2-EHR-Direct_observation.body_temperature.v2.1.5.adls"));
        archObsRespiratoryRate = new ADLParser().parse(ComplexArchetypeValidatorTestS2.class.getResourceAsStream("s2-EHR-Direct_observation.respiratory_rate.v1.0.0.adls"));
        archObsBmi = new ADLParser().parse(ComplexArchetypeValidatorTestS2.class.getResourceAsStream("s2-EHR-Direct_observation.body_mass_index.v2.0.6.adls"));

        archCompEncounter = new ADLParser().parse(ComplexArchetypeValidatorTestS2.class.getResourceAsStream("s2-EHR-Composition.encounter.v1.0.0.adls"));
        tplCompVitalSigns = new ADLParser().parse(ComplexArchetypeValidatorTestS2.class.getResourceAsStream("s2-EHR-Composition.t_encounter-vital_signs-minimal.v1.0.0.adls"));

        repository = new InMemoryFullArchetypeRepository();

        repository.addArchetype(archObsHeight);
        repository.addArchetype(archObsBodyTemperature);
        repository.addArchetype(archObsRespiratoryRate);
        repository.addArchetype(archObsBmi);

        repository.addArchetype(archNodeCitation);
        repository.addArchetype(archNodeLevelOfExertion);
        repository.addArchetype(archNodeDevice);
        repository.addArchetype(archNodeAdverseReaction);
        repository.addArchetype(archObsBloodPressure);
        repository.addArchetype(archObsHeartRate);

        repository.addArchetype(archCompEncounter);
        repository.addArchetype(tplCompVitalSigns);
    }

    @Test
    public void testNodeArchetype() {
        ValidationResult validationResult = new ArchetypeValidator(models).validate(archNodeAdverseReaction, repository);
        List<ValidationMessage> messages = validationResult.getErrors();
        assertEquals(5, messages.size());
    }

    @Test
    public void testTemplateWithOverlay() {
        ValidationResult validationResult = new ArchetypeValidator(models).validate(tplCompVitalSigns, repository);
        List<ValidationMessage> messages = validationResult.getErrors();
        assertEquals(0, messages.size());
    }
}
