package com.nedap.archie.adl14;

import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.adlparser.antlr.Adl14Lexer;
import com.nedap.archie.antlr.errors.ANTLRParserErrors;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CAttributeTuple;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.aom.CPrimitiveTuple;
import com.nedap.archie.aom.primitives.CTerminologyCodeADL14;
import com.nedap.archie.archetypevalidator.ValidationResult;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.serializer.adl.ADLArchetypeSerializer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by pieter.bos on 16/10/15.
 */
public class LargeSetOfADL14sTest {

    private static Logger logger = LoggerFactory.getLogger(LargeSetOfADL14sTest.class);
    private ADL14ConversionConfiguration conversionConfiguration;

    @BeforeEach
    public void setup() throws Exception {
        conversionConfiguration = ConversionConfigForTest.getConfig();
    }

    @Test
    public void parseUri() {
//        CodePointCharStream codePointCharStream = CharStreams.fromString("terminology://SNOMED-CT.com/408733002?subset=Diabetic");//%20Retinopathy%20Study%20field");
        CodePointCharStream codePointCharStream = CharStreams.fromString("<http://test.com/bla?test=green>");
        Adl14Lexer adl14Lexer = new Adl14Lexer(codePointCharStream);
        assertEquals(1, adl14Lexer.getAllTokens().size());
    }

    @Test
    public void parseUrn() {
//        CodePointCharStream codePointCharStream = CharStreams.fromString("terminology://SNOMED-CT.com/408733002?subset=Diabetic");//%20Retinopathy%20Study%20field");
        CodePointCharStream codePointCharStream = CharStreams.fromString("< urn:oin:2.3.1.4.4545.22.23 >");
        Adl14Lexer adl14Lexer = new Adl14Lexer(codePointCharStream);
        assertEquals(1, adl14Lexer.getAllTokens().size());
    }

    /**
     * End-to-end check on a demo ADL 1.4 archetype that exercises (almost) every constraint type:
     * <ol>
     *   <li>Parses it and asserts there are no parse errors.</li>
     *   <li>Asserts every {@link CTerminologyCodeADL14} in the tree has a non-empty constraint list, and that
     *       at least two are multi-code. This guards against a regression where multi-code constraints like
     *       {@code [local::at0001, at0002]} and {@code [openehr::271, 272, 273, 253]} silently parse to an empty
     *       constraint — a bug that bulk-parse tests miss because they only count parse exceptions.</li>
     *   <li>Serializes the parsed (pre-conversion) archetype back to ADL 1.4, exercising
     *       {@link com.nedap.archie.serializer.adl.constraints.CTerminologyCodeADL14Serializer} and asserting
     *       the multi-code forms reappear in the output.</li>
     *   <li>Converts it to ADL 2 and asserts that serialize → reparse → serialize-again produces identical
     *       text both times (a stable round-trip).</li>
     * </ol>
     */
    @Test
    public void testDemoArchetype() throws Exception {
        ADL14Parser parser = new ADL14Parser(BuiltinReferenceModels.getMetaModelProvider());
        Archetype archetype;
        try (InputStream stream = getClass().getResourceAsStream(
                "/adl14/entry/observation/openEHR-EHR-OBSERVATION.demo.v1.adl")) {
            assertNotNull(stream, "demo archetype resource not found on classpath");
            archetype = parser.parse(stream, conversionConfiguration);
        }
        assertNotNull(archetype);
        assertTrue(parser.errorListener.getErrors().getErrors().isEmpty(),
                () -> "unexpected parse errors: " + parser.errorListener.getErrors());

        List<CTerminologyCodeADL14> termCodes = new ArrayList<>();
        collectTerminologyCodes(archetype.getDefinition(), termCodes);
        assertFalse(termCodes.isEmpty(), "no CTerminologyCodeADL14 nodes found - demo archetype unexpectedly empty");
        for (CTerminologyCodeADL14 termCode : termCodes) {
            assertFalse(termCode.getConstraint().isEmpty(),
                    () -> "empty constraint at path " + termCode.getPath()
                            + " - multi-code parsing likely regressed");
        }

        // The demo includes both a local multi-code (4 at-codes) and an openehr multi-code (4 codes).
        // Both used to parse to empty lists before the parser fix.
        long multiCodeCount = termCodes.stream().filter(t -> t.getConstraint().size() > 1).count();
        assertTrue(multiCodeCount >= 2,
                "expected at least two multi-code terminology constraints in the demo, found " + multiCodeCount);

        // Serializing the parsed (pre-conversion) ADL 1.4 archetype must work too: this exercises
        // CTerminologyCodeADL14Serializer. Before that serializer existed, ADLDefinitionSerializer threw
        // an AssertionError on CTerminologyCodeADL14. Assert the multi-code forms round-trip into the text.
        String serializedAdl14 = ADLArchetypeSerializer.serialize(archetype);
        assertTrue(serializedAdl14.contains("[local::at0007, at0008, at0009, at0010]"),
                () -> "expected local multi-code constraint in serialized ADL 1.4, got:\n" + serializedAdl14);
        assertTrue(serializedAdl14.contains("[openehr::271, 272, 273, 253]"),
                () -> "expected external multi-code constraint in serialized ADL 1.4, got:\n" + serializedAdl14);

        // Sanity check: end-to-end conversion of the demo also succeeds.
        ADL2ConversionResultList converted = new ADL14Converter(
                BuiltinReferenceModels.getMetaModelProvider(), conversionConfiguration)
                .convert(Collections.singletonList(archetype));
        ADL2ConversionResult result = converted.getConversionResults().get(0);
        assertNotNull(result.getArchetype(), () -> "conversion returned null archetype, exception: " + result.getException());

        // Round-trip the converted ADL 2 archetype: serialize → reparse → serialize-again and assert the two
        // serialized strings are identical (a stable round-trip).
        // If any CTerminologyCodeADL14 slipped through unconverted it would serialize as ADL 1.4 syntax,
        // which the ADL 2 parser would then reject on reparse below.
        String serialized = ADLArchetypeSerializer.serialize(result.getArchetype());
        ADLParser adl2Parser = new ADLParser(BuiltinReferenceModels.getMetaModelProvider());
        Archetype reparsed = adl2Parser.parse(serialized);
        assertTrue(adl2Parser.getErrors().hasNoErrors(),
                () -> "roundtrip parsing of converted ADL 2 archetype produced errors: " + adl2Parser.getErrors());
        String serializedAgain = ADLArchetypeSerializer.serialize(reparsed);
        assertEquals(serialized, serializedAgain, "serializing twice should produce identical text");
    }

    private void collectTerminologyCodes(CObject cObject, List<CTerminologyCodeADL14> out) {
        if (cObject instanceof CTerminologyCodeADL14) {
            out.add((CTerminologyCodeADL14) cObject);
        }
        for (CAttribute attribute : cObject.getAttributes()) {
            for (CObject child : attribute.getChildren()) {
                collectTerminologyCodes(child, out);
            }
        }
        if (cObject instanceof CComplexObject) {
            for (CAttributeTuple tuple : ((CComplexObject) cObject).getAttributeTuples()) {
                for (CPrimitiveTuple primitiveTuple : tuple.getTuples()) {
                    for (CPrimitiveObject<?, ?> member : primitiveTuple.getMembers()) {
                        if (member instanceof CTerminologyCodeADL14) {
                            out.add((CTerminologyCodeADL14) member);
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testRiskFamilyhistory() throws Exception {

        ADL14Parser parser = new ADL14Parser(BuiltinReferenceModels.getMetaModelProvider());

        Archetype riskParent = parser.parse(getClass().getResourceAsStream("/adl14/risk_parent.adl"), conversionConfiguration);
        Archetype riskFamilyHistory = parser.parse(getClass().getResourceAsStream("/adl14/risk_history.adl"), conversionConfiguration);

        List<Archetype> archetypes = Arrays.asList(riskParent, riskFamilyHistory);

        ADL2ConversionResultList converted = new ADL14Converter(BuiltinReferenceModels.getMetaModelProvider(), conversionConfiguration)
                .convert(archetypes);

        for(ADL2ConversionResult conversionResult:converted.getConversionResults()) {
            if(conversionResult.getException() != null) {
                logger.error("exception in converter for archetype id " + conversionResult.getArchetypeId(), conversionResult.getException());
            }
            if (conversionResult.getArchetype() != null) {
//                System.out.println(ADLArchetypeSerializer.serialize(conversionResult.getArchetype()));
            } else {
                logger.warn("archetype null: " + conversionResult.getArchetypeId());
            }
        }

        InMemoryFullArchetypeRepository adl2Repository = new InMemoryFullArchetypeRepository();
        for(ADL2ConversionResult conversionResult:converted.getConversionResults()) {
            if(conversionResult.getException() == null && conversionResult.getArchetype() != null) {
                adl2Repository.addArchetype(conversionResult.getArchetype());
            }
        }
        adl2Repository.compile(BuiltinReferenceModels.getMetaModelProvider());

        for(ValidationResult validationResult:adl2Repository.getAllValidationResults()) {
            if(!validationResult.passes()) {
                logger.error("error validating {}: {}", validationResult.getArchetypeId(), validationResult.getErrors());
            }
        }
    }

    @Test
    public void parseLots() throws Exception {
        Reflections reflections = new Reflections("adl14", Scanners.Resources);
        List<String> adlFiles = new ArrayList<>(reflections.getResources(Pattern.compile(".*\\.adl")));

        Map<String, Exception> exceptions = new LinkedHashMap<>();
        Map<String, ANTLRParserErrors> parseErrors = new LinkedHashMap<>();

        List<Archetype> archetypes = new ArrayList<>();
        for(String file:adlFiles) {
//            if(!file.contains("avpu")) {
//                continue;
//            }
            Archetype archetype = parse(exceptions, parseErrors, file);
            if(archetype != null) {
                archetypes.add(archetype);
            }

        }
        ADL2ConversionResultList converted = new ADL14Converter(BuiltinReferenceModels.getMetaModelProvider(), conversionConfiguration)
                .convert(archetypes);
        for(ADL2ConversionResult result:converted.getConversionResults()) {
            if(result.getArchetype() != null) {// && result.getArchetype().getParentArchetypeId() != null) {
//              System.out.println(ADLArchetypeSerializer.serialize(result.getArchetype()));
            } else {
                logger.warn("archetype null: " + result.getArchetypeId());
            }
        }

        for(String file:adlFiles) {
            if(parseErrors.containsKey(file)) {
                logger.error("parse error found in " + file);
                logger.error(parseErrors.get(file).toString());
            }
            if(exceptions.containsKey(file)) {
                logger.error("exception found in " + file, exceptions.get(file));
            }

        }
        for(String file:exceptions.keySet()) {
            if(!adlFiles.contains(file)) {
                logger.error("exception found in " + file, exceptions.get(file));
            }
        }

        int convertedArchetypes = 0;
        for(ADL2ConversionResult conversionResult:converted.getConversionResults()) {
            if(conversionResult.getException() != null) {
                logger.error("exception in converter for archetype id " + conversionResult.getArchetypeId(), conversionResult.getException());
            } else if (conversionResult.getArchetype() != null) {
                convertedArchetypes++;
            }
        }

        logger.info("parsed adls: " + adlFiles.size());
        logger.info("number of archetypes: " + archetypes.size());
        logger.info("number of adl 2 archetypes: " + convertedArchetypes);
        logger.info("parsed adls with ANTLR parse errors: " + parseErrors.size());
        logger.info("parsed adls with Exceptions: " + exceptions.size());

        InMemoryFullArchetypeRepository adl2Repository = new InMemoryFullArchetypeRepository();
        for(ADL2ConversionResult conversionResult:converted.getConversionResults()) {
            if(conversionResult.getException() == null && conversionResult.getArchetype() != null) {
                adl2Repository.addArchetype(conversionResult.getArchetype());
            }
        }
        adl2Repository.compile(BuiltinReferenceModels.getMetaModelProvider());
        int passingValidations = 0;
        for(ValidationResult validationResult:adl2Repository.getAllValidationResults()) {
            if(validationResult.passes()) {
                //may still have warnings, so print if that's the case, so you can at least see in the log
                //assertions would be even better, but these are actual warnings due to ADL 1.4 archetype content
                if(validationResult.hasWarningsOrErrors()) {
                    logger.error(String.format("archetype %s has warning %s: ", validationResult.getArchetypeId(), validationResult.getErrors()));
                }
                passingValidations++;
            } else {
                logger.error("error validating {}: {}", validationResult.getArchetypeId(), validationResult.getErrors());
            }
        }

        logger.info("passing validation: " + passingValidations);

        //TODO: this is rather ugly, but I just want not more failing tests, that's all :)
        //this now contains regexp matching errors, version 1.5 (arguably, should not fail on that at all!)
        //and some other problems

        //some errors in the terminology sections caused by some property called 'items'
        //6 errors in annotations section caused by some property called 'items'
        //some errors due to test cases for wrong syntax
        //some errors due to incompatible ADL 1.5-syntax
        assertTrue(exceptions.size() <= 2);


    }

    private Archetype parse(Map<String, Exception> exceptions, Map<String, ANTLRParserErrors> parseErrors, String file) {
        try (InputStream stream = getClass().getResourceAsStream("/" + file)) {
            logger.info("trying to parse " + file);
            ADL14Parser parser = new ADL14Parser(BuiltinReferenceModels.getMetaModelProvider());

            Archetype archetype = parser.parse(stream, conversionConfiguration);
            //logger.info(JacksonUtil.getObjectMapper().writeValueAsString(conversionResult.getConversionLog()));
           // System.out.println(ADLArchetypeSerializer.serialize(archetype));
            if(!parser.errorListener.getErrors().getErrors().isEmpty()) {
                parseErrors.put(file, parser.errorListener.getErrors());
            }
            if(parser.getTree().exception != null) {
                exceptions.put(file, parser.getTree().exception);
            }
            return archetype;

        } catch (Exception e) {
            exceptions.put(file, e);
        }
        return null;
    }


}
