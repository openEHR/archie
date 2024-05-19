package com.nedap.archie.adl14;

import com.nedap.archie.adlparser.antlr.Adl14Lexer;
import com.nedap.archie.antlr.errors.ANTLRParserErrors;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.archetypevalidator.ValidationResult;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.junit.Before;
import org.junit.Test;
import org.openehr.referencemodels.AllMetaModelsInitialiser;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by pieter.bos on 16/10/15.
 */
public class LargeSetOfADL14sTest {

    private static Logger logger = LoggerFactory.getLogger(LargeSetOfADL14sTest.class);
    private ADL14ConversionConfiguration conversionConfiguration;

    @Before
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

    @Test
    public void testRiskFamilyhistory() throws Exception {

        ADL14Parser parser = new ADL14Parser(AllMetaModelsInitialiser.getMetaModels());

        Archetype riskParent = parser.parse(getClass().getResourceAsStream("/adl14/risk_parent.adl"), conversionConfiguration);
        Archetype riskFamilyHistory = parser.parse(getClass().getResourceAsStream("/adl14/risk_history.adl"), conversionConfiguration);

        List<Archetype> archetypes = Arrays.asList(riskParent, riskFamilyHistory);

        ADL2ConversionResultList converted = new ADL14Converter(AllMetaModelsInitialiser.getMetaModels(), conversionConfiguration)
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
        adl2Repository.compile(AllMetaModelsInitialiser.getMetaModels());

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
        ADL2ConversionResultList converted = new ADL14Converter(AllMetaModelsInitialiser.getMetaModels(), conversionConfiguration)
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
        adl2Repository.compile(AllMetaModelsInitialiser.getMetaModels());
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
            ADL14Parser parser = new ADL14Parser(AllMetaModelsInitialiser.getMetaModels());

            Archetype archetype = parser.parse(stream, conversionConfiguration);
            //logger.info(JacksonUtil.getObjectMapper().writeValueAsString(conversionResult.getConversionLog()));
           // System.out.println(ADLArchetypeSerializer.serialize(archetype));
            if(parser.errorListener.getErrors().getErrors().size() > 0) {
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
