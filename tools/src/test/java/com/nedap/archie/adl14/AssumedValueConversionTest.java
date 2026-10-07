package com.nedap.archie.adl14;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.archetypevalidator.ArchetypeValidationSettings;
import com.nedap.archie.archetypevalidator.ArchetypeValidator;
import com.nedap.archie.archetypevalidator.NodeIdCodeSystemValidation;
import com.nedap.archie.archetypevalidator.ValidationResult;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import org.junit.jupiter.api.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class AssumedValueConversionTest {

    @Test
    public void testAssumedValueConversion() throws Exception {
        ADL14ConversionConfiguration conversionConfiguration = ConversionConfigForTest.getConfig();
        ADL14Converter converter = new ADL14Converter(BuiltinReferenceModels.getMetaModelProvider(), conversionConfiguration);

        Archetype adl14archetype;
        try(InputStream stream = getClass().getResourceAsStream("openEHR-EHR-OBSERVATION.height.v2.adl")) {
            adl14archetype = new ADL14Parser(BuiltinReferenceModels.getMetaModelProvider()).parse(stream, conversionConfiguration);
        }

        ADL2ConversionResultList result = converter.convert(
                Lists.newArrayList(adl14archetype));
        Archetype archetype = result.getConversionResults().get(0).getArchetype();

        CAttribute cAttribute = archetype.itemAtPath("/data[id2]/events[id3]/state[id14]/items[id15]/value[id9004]/defining_code");
        CTerminologyCode cTerminologyCode = (CTerminologyCode) cAttribute.getChildren().get(0);

        assertNull(cTerminologyCode.getAssumedValue().getTerminologyId());
        assertEquals("at17", cTerminologyCode.getAssumedValue().getCodeString());

        ValidationResult validationResult = new ArchetypeValidator(BuiltinReferenceModels.getMetaModelProvider()).validate(archetype);

        assertNotNull(validationResult.toString());
        assertTrue(validationResult.passes());
    }

    @Test
    public void testAssumedValueConversionAtCoded() throws Exception {
        ADL14ConversionConfiguration conversionConfiguration = ConversionConfigForTest.getConfig();
        conversionConfiguration.setNodeIdCodeSystem(ADL14ConversionConfiguration.NodeIdCodeSystem.AT_CODED);
        ADL14Converter converter = new ADL14Converter(BuiltinReferenceModels.getMetaModelProvider(), conversionConfiguration);

        Archetype adl14archetype;
        try(InputStream stream = getClass().getResourceAsStream("openEHR-EHR-OBSERVATION.height.v2.adl")) {
            adl14archetype = new ADL14Parser(BuiltinReferenceModels.getMetaModelProvider()).parse(stream, conversionConfiguration);
        }

        ADL2ConversionResultList result = converter.convert(
                Lists.newArrayList(adl14archetype));
        Archetype archetype = result.getConversionResults().get(0).getArchetype();

        CAttribute cAttribute = archetype.itemAtPath("/data[at0001]/events[at0002]/state[at0013]/items[at0014]/value/defining_code");
        CTerminologyCode cTerminologyCode = (CTerminologyCode) cAttribute.getChildren().get(0);

        // in at-coded archetypes the local codes are not converted, so neither is the assumed value
        assertNull(cTerminologyCode.getAssumedValue().getTerminologyId());
        assertEquals("at0016", cTerminologyCode.getAssumedValue().getCodeString());
        assertTrue(archetype.getTerminology().getValueSets().get(cTerminologyCode.getConstraint().get(0)).getMembers().contains("at0016"));

        InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
        ArchetypeValidationSettings settings = new ArchetypeValidationSettings();
        settings.setNodeIdCodeSystemValidation(NodeIdCodeSystemValidation.AT_CODED);
        repository.setArchetypeValidationSettings(settings);
        repository.addArchetype(archetype);
        ValidationResult validationResult = new ArchetypeValidator(BuiltinReferenceModels.getMetaModelProvider()).validate(archetype, repository);

        assertTrue(validationResult.passes(), validationResult.toString());
    }
}
