package com.nedap.archie.adl14;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.archetypevalidator.ArchetypeValidationSettings;
import com.nedap.archie.archetypevalidator.ArchetypeValidator;
import com.nedap.archie.archetypevalidator.NodeIdCodeSystemValidation;
import com.nedap.archie.archetypevalidator.ValidationResult;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.serializer.adl.ADLArchetypeSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;

class ADL14TerminologyConversionTest {

    @Test
    void twoTermbindingsInOneConstraint() throws Exception {
        ADL14ConversionConfiguration conversionConfiguration = ConversionConfigForTest.getConfig();
        ADL14Converter converter = new ADL14Converter(BuiltinReferenceModels.getMetaModelProvider(), conversionConfiguration);
        //apply the first conversion and store the log. It has created an at code to bind to [openehr::124], used in a DV_QUANTITY.property
        try(InputStream stream = getClass().getResourceAsStream("openEHR-EHR-CLUSTER.termbinding.v1.adl")) {
            ADL14Parser parser = new ADL14Parser(BuiltinReferenceModels.getMetaModelProvider());
            ADL2ConversionResultList result = converter.convert(
                    Lists.newArrayList(parser.parse(stream, conversionConfiguration)));
            Archetype converted = result.getConversionResults().get(0).getArchetype();

            Assertions.assertEquals("extra_value", converted.getTerminology().getTermDefinition("en", "id1").getOtherItems().get("extra_item"));
            Assertions.assertEquals("extra_value", converted.getTerminology().getTermDefinition("en", "id1").getOtherItems().get("Extra_item_2"));
            Assertions.assertEquals("extra_value", converted.getTerminology().getTermDefinition("en", "id1").getOtherItems().get("_Extra_item_2"));
            String serialized = ADLArchetypeSerializer.serialize(converted);
            Assertions.assertTrue(serialized.contains("extra_item = <\"extra_value\">"));

        }
    }

    @Test
    void termBindingKeysKeptWhenAtCoded() throws Exception {
        ADL14ConversionConfiguration conversionConfiguration = ConversionConfigForTest.getConfig();
        conversionConfiguration.setNodeIdCodeSystem(ADL14ConversionConfiguration.NodeIdCodeSystem.AT_CODED);
        ADL14Converter converter = new ADL14Converter(BuiltinReferenceModels.getMetaModelProvider(), conversionConfiguration);
        // contains term bindings for at0137 and at0138, and a constraint binding for ac0002
        try(InputStream stream = getClass().getResourceAsStream("/adl14/entry/observation/openEHR-EHR-OBSERVATION.fundoscopic_examination.v0.adl")) {
            ADL14Parser parser = new ADL14Parser(BuiltinReferenceModels.getMetaModelProvider());
            ADL2ConversionResultList result = converter.convert(
                    Lists.newArrayList(parser.parse(stream, conversionConfiguration)));
            ADL2ConversionResult conversionResult = result.getConversionResults().get(0);
            Archetype converted = conversionResult.getArchetype();

            // at-coded archetypes keep their at and ac codes, so the term binding keys should stay the same
            Map<String, URI> snomedBindings = converted.getTerminology().getTermBindings().get("SNOMED-CT");
            Assertions.assertTrue(snomedBindings.containsKey("at0137"), snomedBindings.toString());
            Assertions.assertTrue(snomedBindings.containsKey("at0138"), snomedBindings.toString());
            Assertions.assertTrue(snomedBindings.containsKey("ac0002"), snomedBindings.toString());
            Assertions.assertFalse(snomedBindings.containsKey("ac3"), snomedBindings.toString());
            Assertions.assertTrue(conversionResult.getLog().getMessageList().stream()
                    .noneMatch(message -> message.getCode() == ADL14ConversionMessageCode.WARNING_UNKNOWN_CODE_TYPE_IN_TERMBINDING),
                    conversionResult.getLog().getMessageList().toString());

            InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
            ArchetypeValidationSettings settings = new ArchetypeValidationSettings();
            settings.setNodeIdCodeSystemValidation(NodeIdCodeSystemValidation.AT_CODED);
            repository.setArchetypeValidationSettings(settings);
            repository.addArchetype(converted);
            ValidationResult validationResult = new ArchetypeValidator(BuiltinReferenceModels.getMetaModelProvider()).validate(converted, repository);
            Assertions.assertTrue(validationResult.passes(), validationResult.toString());
        }
    }
}
