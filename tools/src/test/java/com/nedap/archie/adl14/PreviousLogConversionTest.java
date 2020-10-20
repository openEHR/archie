package com.nedap.archie.adl14;

import com.google.common.collect.Lists;
import com.nedap.archie.adl14.log.ADL2ConversionRunLog;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.archetypevalidator.ArchetypeValidator;
import com.nedap.archie.archetypevalidator.ValidationResult;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.InputStream;

import static junit.framework.TestCase.*;
import static org.junit.Assert.assertTrue;

public class PreviousLogConversionTest {

    @Test
    public void applyConsistentConversion() throws Exception {

        ADL14ConversionConfiguration conversionConfiguration = ConversionConfigForTest.getConfig();
        ADL14Converter converter = new ADL14Converter(BuiltinReferenceModels.getMetaModels(), conversionConfiguration);
        ADL2ConversionRunLog log = null;

        try(InputStream stream = getClass().getResourceAsStream("openehr-EHR-COMPOSITION.review.v1.adl")) {
            ADL2ConversionResultList result = converter.convert(
                    Lists.newArrayList(new ADL14Parser(BuiltinReferenceModels.getMetaModels()).parse(stream, conversionConfiguration)));
            log = result.getConversionLog();
        }

        assertEquals(1, log.getConvertedArchetypes().size());

        try(InputStream stream = getClass().getResourceAsStream("openEHR-EHR-COMPOSITION.review.v1.modified.adl")) {
            ADL2ConversionResultList result = converter.convert(
                    Lists.newArrayList(new ADL14Parser(BuiltinReferenceModels.getMetaModels()).parse(stream, conversionConfiguration)),
                    log);
            CAttribute attribute = result.getConversionResults().get(0).getArchetype().itemAtPath("/category");
            assertEquals(2, attribute.getChildren().size());
            CObject dvText = attribute.getChildren().get(0);
            CObject dvCodedText = attribute.getChildren().get(1);
            assertEquals("DV_CODED_TEXT", dvCodedText.getRmTypeName());
            assertEquals("DV_TEXT", dvText.getRmTypeName());
            assertEquals("id9001", dvCodedText.getNodeId());
            assertEquals("id9002", dvText.getNodeId());

        }
    }

    @Test
    public void testValueSet() throws Exception {
        ADL14ConversionConfiguration conversionConfiguration = ConversionConfigForTest.getConfig();
        ADL14Converter converter = new ADL14Converter(BuiltinReferenceModels.getMetaModels(), conversionConfiguration);
        ADL2ConversionRunLog log = null;

        try(InputStream stream = getClass().getResourceAsStream("openEHR-EHR-OBSERVATION.respiration.v1.adl")) {
            ADL2ConversionResultList result = converter.convert(
                    Lists.newArrayList(new ADL14Parser(BuiltinReferenceModels.getMetaModels()).parse(stream, conversionConfiguration)));
            log = result.getConversionLog();
            Archetype converted = result.getConversionResults().get(0).getArchetype();
            ValidationResult validated = new ArchetypeValidator(BuiltinReferenceModels.getMetaModels()).validate(converted);
            assertTrue(validated.toString(), validated.passes() );
            assertTrue(converted.getTerminology().getTermDefinitions().get("nl").containsKey("ac9000"));
            assertTrue(converted.getTerminology().getTermDefinitions().get("nl").containsKey("ac9001"));
            assertTrue(converted.getTerminology().getTermDefinitions().get("nl").containsKey("ac9002"));
            assertTrue(converted.getTerminology().getTermDefinitions().get("nl").containsKey("ac9003"));
        }

        assertEquals(1, log.getConvertedArchetypes().size());

        try(InputStream stream = getClass().getResourceAsStream("openEHR-EHR-OBSERVATION.respiration.v1.adl")) {
            ADL2ConversionResultList result = converter.convert(
                    Lists.newArrayList(new ADL14Parser(BuiltinReferenceModels.getMetaModels()).parse(stream, conversionConfiguration)),
                    log);
            Archetype converted = result.getConversionResults().get(0).getArchetype();

            ValidationResult validated = new ArchetypeValidator(BuiltinReferenceModels.getMetaModels()).validate(converted);
            assertTrue(validated.toString(), validated.passes() );
            assertTrue(converted.getTerminology().getTermDefinitions().get("nl").containsKey("ac9000"));
            assertTrue(converted.getTerminology().getTermDefinitions().get("nl").containsKey("ac9001"));
            assertTrue(converted.getTerminology().getTermDefinitions().get("nl").containsKey("ac9002"));
            assertTrue(converted.getTerminology().getTermDefinitions().get("nl").containsKey("ac9003"));

        }
    }

    @Test
    public void unusedValuesAreRemoved() throws Exception {
        ADL14ConversionConfiguration conversionConfiguration = ConversionConfigForTest.getConfig();
        ADL14Converter converter = new ADL14Converter(BuiltinReferenceModels.getMetaModels(), conversionConfiguration);
        ADL2ConversionRunLog log = null;
        String createdAtCode = null;
        //apply the first conversion and store the log. It has created an at code to bind to [openehr::124], used in a DV_QUANTITY.property
        try(InputStream stream = getClass().getResourceAsStream("/adl14/openEHR-EHR-CLUSTER.value_binding.v1.0.0.adl")) {
            ADL2ConversionResultList result = converter.convert(
                    Lists.newArrayList(new ADL14Parser(BuiltinReferenceModels.getMetaModels()).parse(stream, conversionConfiguration)));
            log = result.getConversionLog();
            Archetype converted = result.getConversionResults().get(0).getArchetype();
            ValidationResult validated = new ArchetypeValidator(BuiltinReferenceModels.getMetaModels()).validate(converted);
            assertTrue(validated.toString(), validated.passes() );
             createdAtCode = log.getConversionLog("openEHR-EHR-CLUSTER.value_binding.v1").getCreatedCodes().get("[openehr::124]").getGeneratedCode();
            ArchetypeTerm termDefinition = converted.getTerminology().getTermDefinition("en", createdAtCode);
            assertNotNull(termDefinition);
        }

        assertEquals(1, log.getConvertedArchetypes().size());

        //apply the first conversion. The openehr::124 term binding is gone, and should not be present in the result, but should remain in the conversion log for future readdition
        try(InputStream stream = getClass().getResourceAsStream("/adl14/openEHR-EHR-CLUSTER.value_binding.v1.0.1.adl")) {
            ADL2ConversionResultList result = converter.convert(
                    Lists.newArrayList(new ADL14Parser(BuiltinReferenceModels.getMetaModels()).parse(stream, conversionConfiguration)),
                    log);
            Archetype converted = result.getConversionResults().get(0).getArchetype();
            ValidationResult validated = new ArchetypeValidator(BuiltinReferenceModels.getMetaModels()).validate(converted);
            assertTrue(validated.toString(), validated.passes() );

            ArchetypeTerm termDefinition = converted.getTerminology().getTermDefinition("en", "at9000");
            assertNull(termDefinition);
            assertFalse(validated.toString(), validated.hasWarningsOrErrors() );

            ADL2ConversionRunLog log2 = result.getConversionLog();
            //the code should still be present in the conversion log, should it be added later on
            assertNotNull(log2.getConversionLog("openEHR-EHR-CLUSTER.value_binding.v1").getCreatedCodes().get("[openehr::124]"));
        }
    }

}
