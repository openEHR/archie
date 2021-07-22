package com.nedap.archie.opt_marand;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.nedap.archie.adl14.ADL14ConversionConfiguration;
import com.nedap.archie.adl14.ADL14ConversionUtil;
import com.nedap.archie.adl14.ADL14Converter;
import com.nedap.archie.adl14.ADL14Parser;
import com.nedap.archie.adl14.ADL2ConversionResult;
import com.nedap.archie.adl14.ADL2ConversionResultList;
import com.nedap.archie.adl14.ConversionConfigForTest;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.AuthoredResource;
import com.nedap.archie.aom.CArchetypeRoot;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CDefinedObject;
import com.nedap.archie.aom.Template;
import com.nedap.archie.aom.TemplateOverlay;
import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.archetypevalidator.ValidationResult;
import com.nedap.archie.base.terminology.TerminologyCode;
import com.nedap.archie.flattener.ArchetypeHRIDMap;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.FlattenerConfiguration;
import com.nedap.archie.template.betterjson.ArchetypeTermFixer;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.json.JacksonUtil;
import com.nedap.archie.json.RMJacksonConfiguration;
import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rminfo.MetaModels;
import com.nedap.archie.serializer.adl.ADLArchetypeSerializer;
import com.nedap.archie.template.betterjson.ExternalTermBindingTranslationFixer;
import com.nedap.archie.template.betterjson.FlatArchetypeProvider;
import com.nedap.archie.template.betterjson.LanguageConsistencyFixer;
import com.nedap.archie.template.betterjson.MultiplicityFixer;
import com.nedap.archie.template.betterjson.NodeIdFixer;
import com.nedap.archie.template.betterjson.SpecializedTerminologyCodeFixer;
import com.nedap.archie.template.betterjson.ValueSetFixer;
import com.nedap.archie.template.betterjson.parser.*;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class ParseBetterSystemsOptTest {

    public static final String OPT_JSON_ARCHETYPES = "/opt_json/Archetypes/";
    private String[] adl2Archetypes = {
            "openEHR-EHR-CLUSTER.symptom_sign.v1.0.0.adls"
    };

    private String[] archetypeFiles = {
            "openEHR-EHR-CLUSTER.address_cc.v0.adl",
            "openEHR-EHR-CLUSTER.dwelling.v0.adl",
            "openEHR-EHR-CLUSTER.employment_covid.v0.adl",
            "openEHR-EHR-CLUSTER.occupation_record.v1.adl",
            "openEHR-EHR-CLUSTER.organisation_cc.v0.adl",
            "openEHR-EHR-CLUSTER.outbreak_exposure.v0.adl",
            "openEHR-EHR-CLUSTER.overcrowding_screening.v0.adl",
            "openEHR-EHR-CLUSTER.problem_qualifier.v1.adl",
           // "openEHR-EHR-CLUSTER.symptom_sign.v1.adl",
            "openEHR-EHR-CLUSTER.symptom_sign-cvid.v0.adl",
            "openEHR-EHR-COMPOSITION.encounter.v1.adl",
            "openEHR-EHR-EVALUATION.health_risk.v1.adl",
            "openEHR-EHR-EVALUATION.health_risk-covid.v0.adl",
            "openEHR-EHR-EVALUATION.living_arrangement.v0.adl",
            "openEHR-EHR-EVALUATION.occupation_summary.v1.adl",
            "openEHR-EHR-EVALUATION.problem_diagnosis.v1.adl",
            "openEHR-EHR-INSTRUCTION.service_request.v1.adl",
            "openEHR-EHR-OBSERVATION.body_temperature.v2.adl",
            "openEHR-EHR-OBSERVATION.story.v1.adl",
            "openEHR-EHR-OBSERVATION.travel_history.v0.adl"
    };

    @Test
    public void parseOpt() throws IOException {
        try(InputStream stream = getClass().getResourceAsStream("/opt_json/COVID-19-Screening_t.json")) {
            RMJacksonConfiguration config = new RMJacksonConfiguration();
            config.setFailOnUnknownProperties(true);
            ObjectMapper objectMapper = getObjectMapper(config);


            Archetype archetype = objectMapper.readValue(stream, Archetype.class);
            System.out.println(ADLArchetypeSerializer.serialize(archetype));
        }
    }

    private ObjectMapper getObjectMapper(RMJacksonConfiguration config) {
        ObjectMapper objectMapper = new ObjectMapper();
        JacksonUtil.configureObjectMapper(objectMapper, config);

        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);

        objectMapper.addMixIn(TerminologyCode.class, TerminologyIdParsingTerminologyCodeMixin.class);
        objectMapper.addMixIn(AuthoredResource.class, AuthoredResourceMixin.class);
        objectMapper.addMixIn(Archetype.class, ArchetypeMixin.class);
        objectMapper.addMixIn(Template.class, ArchetypeMixin.class);
        objectMapper.addMixIn(CArchetypeRoot.class, CArchetypeRootMixin.class);
        objectMapper.addMixIn(CComplexObject.class, CComplexObjectMixin.class);
        objectMapper.addMixIn(CDefinedObject.class, CDefinedObjectMixin.class);


        objectMapper.addMixIn(CTerminologyCode.class, CTerminologyCodeMixin.class);


        objectMapper.addMixIn(DvCodedText.class, DvCodedTextMixin.class);
        objectMapper.addMixIn(CodePhrase.class, CodePhraseMixin.class);
        return objectMapper;
    }

    private void fixtermBindings(Archetype archetype, ADL14ConversionConfiguration adl14ConversionConfiguration) {
        ADL14ConversionUtil adl14ConversionUtil = new ADL14ConversionUtil(adl14ConversionConfiguration);
        if(archetype.getTerminology() != null && archetype.getTerminology().getTermBindings() != null) {
            Map<String, Map<String, URI>> termBindings = archetype.getTerminology().getTermBindings();
            for(String terminologyId: termBindings.keySet()) {
                for(String key:termBindings.get(terminologyId).keySet()) {
                    URI uri = termBindings.get(terminologyId).get(key);
                    if(uri.toString().startsWith("term:")) {
                        String termCode = "[" + uri.toString().substring(5) + "]";
                        try {
                            termBindings.get(terminologyId).put(key, adl14ConversionUtil.convertToUri(TerminologyCode.createFromString(termCode)));
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }
    }

    @Test
    public void parseCovidAssessment() throws Exception {
        MetaModels metaModels = BuiltinReferenceModels.getMetaModels();
        ADL14ConversionConfiguration adl14ConversionConfiguration = ConversionConfigForTest.getConfig();
        List<Archetype> archetypes = new ArrayList<>();
        parseADL14Archetypes(metaModels, adl14ConversionConfiguration, archetypes);
        ADLParser adl2Parser = new ADLParser(BuiltinReferenceModels.getMetaModels());
        Archetype adl2Symptom = null;
        try(InputStream stream = getClass().getResourceAsStream("/opt_json/adl2/" + adl2Archetypes[0])){
            adl2Symptom = adl2Parser.parse(stream);
        }

        ADL14Converter adl14ArchetypeConverter = new ADL14Converter(BuiltinReferenceModels.getMetaModels(), adl14ConversionConfiguration);
        {
            InMemoryFullArchetypeRepository tmpRepository = new InMemoryFullArchetypeRepository();
            tmpRepository.addArchetype(adl2Symptom);
            adl14ArchetypeConverter.setExistingRepository(tmpRepository);
        }
        ADL2ConversionResultList converted = adl14ArchetypeConverter.convert(archetypes);

        output(converted);
        output(adl2Symptom);

        checkConversions(converted);

        InMemoryFullArchetypeRepository adl2Repository = createRepository(converted, adl2Symptom);

        RMJacksonConfiguration config = new RMJacksonConfiguration();
        config.setFailOnUnknownProperties(true);
        ObjectMapper mapper = getObjectMapper(config);
        try(InputStream stream = getClass().getResourceAsStream("/opt_json/Templates/Suspected Covid-19 Assessment.v0.1.t.json")) {
            Archetype archetype = mapper.readValue(stream, Archetype.class);
            new CTerminologyCodeConverter(false).fixCTerminologyCodes(archetype);
            ADL14ConversionConfiguration templateconfig = ConversionConfigForTest.getConfig();
            templateconfig.setApplyDiff(false);
            ADL14Converter adl14Converter = new ADL14Converter(BuiltinReferenceModels.getMetaModels(), templateconfig);
            adl14Converter.setExistingRepository(adl2Repository);

            new ValueSetFixer().convertValueSets(archetype);

            ADL2ConversionResultList convert = adl14Converter.convert(Lists.newArrayList(archetype));

            Template foundTemplate = null;

            for(ADL2ConversionResult result:convert.getConversionResults()) {
                if(result.getArchetype() instanceof Template) {
                    foundTemplate = (Template) result.getArchetype();
                    //TODO: move to converter!
                    foundTemplate.setTemplateOverlays(new ArrayList<>());//remove the template overlays for now
                }
                fixtermBindings(result.getArchetype(), templateconfig);
                if(result.getException() != null) {
                    throw result.getException();
                }
            }

            for(ADL2ConversionResult result:convert.getConversionResults()) {
                if (result.getArchetype() instanceof TemplateOverlay) {
                    foundTemplate.addTemplateOverlay((TemplateOverlay) result.getArchetype());
                }
            }

            //FIRST remove node ids that shouldn't have been created
            //THEN add terms

            //TODO: creating the flatArchetypeProvider again and again is very slow
            //but as we are fixing on the fly, it's probably the only way since the fixes can impact the fixing itself
            //TODO: even the template over lays should actually be fixed in dependency order. very minor for most templates, major for others
            new NodeIdFixer().fixNodeIds(foundTemplate, new FlatArchetypeProvider(adl2Repository));
            new ArchetypeTermFixer().fixTerms(foundTemplate, new FlatArchetypeProvider(adl2Repository));
            new LanguageConsistencyFixer().fixLanguageConsistency(foundTemplate);
            new SpecializedTerminologyCodeFixer().fixTerminologyCodes(foundTemplate, new FlatArchetypeProvider(adl2Repository));
            new ExternalTermBindingTranslationFixer().fixTranslations(templateconfig, foundTemplate);
            new CTerminologyCodeConverter(true).fixCTerminologyCodes(foundTemplate);
            new MultiplicityFixer().fixMultiplicity(BuiltinReferenceModels.getMetaModels(), foundTemplate, new FlatArchetypeProvider(adl2Repository));



            adl2Repository.compile(BuiltinReferenceModels.getMetaModels());
            output(foundTemplate);

            FlatArchetypeProvider provider = new FlatArchetypeProvider(adl2Repository);

            System.out.println(ADLArchetypeSerializer.serialize(foundTemplate, provider::getFlatArchetype));
            FlattenerConfiguration flattenerConfiguration = FlattenerConfiguration.forOperationalTemplate();
            flattenerConfiguration.setRemoveLanguagesFromMetaData(true);
            String[] langaugesToKeep = {"en"};
            flattenerConfiguration.setLanguagesToKeep(langaugesToKeep);
            Flattener optCreator = new Flattener(adl2Repository, BuiltinReferenceModels.getMetaModels(), flattenerConfiguration);

            //System.out.println("\n\n\n==========================================\nOPT 2\n==================\n\n");
            output(optCreator.flatten(foundTemplate), "opt_thingy");

            ValidationResult validationResult = adl2Repository.getValidationResult("openEHR-EHR-COMPOSITION.t_encounter.v1.0.0");
            if(!validationResult.passes()) {
                throw new RuntimeException(MessageFormat.format("error validating {0}: {1}", validationResult.getArchetypeId(), validationResult));
            }
        }
    }

    class FlatArchetypeProvider implements com.nedap.archie.template.betterjson.FlatArchetypeProvider {
        private InMemoryFullArchetypeRepository repo;
        private ArchetypeHRIDMap<Archetype> flatArchetypes = new ArchetypeHRIDMap<>();
        private MetaModels metaModels = BuiltinReferenceModels.getMetaModels();

        public FlatArchetypeProvider(InMemoryFullArchetypeRepository repo) {
            this.repo = repo;
        }

        public Archetype getFlatArchetype(String id) {
            Archetype flattenedArchetype = repo.getFlattenedArchetype(id);
            if(flattenedArchetype != null) {
                return flattenedArchetype;
            }
            flattenedArchetype = flatArchetypes.get(id);
            if(flattenedArchetype != null) {
                return flattenedArchetype;
            }
            Archetype archetype = repo.getArchetype(id);
            try {
                flattenedArchetype = new Flattener(repo, metaModels).flatten(archetype);
                flatArchetypes.put(flattenedArchetype.getArchetypeId(), flattenedArchetype);
                return flattenedArchetype;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

    }

    private void output(ADL2ConversionResultList converted) {
        String outputDir = "/Users/pieter.bos/projects/openehr/covid/";
        for(ADL2ConversionResult result:converted.getConversionResults()) {
            String fileName = outputDir + result.getArchetypeId() + ".adls";

            try(FileOutputStream stream = new FileOutputStream(fileName)) {
                stream.write(ADLArchetypeSerializer.serialize(result.getArchetype()).getBytes(Charsets.UTF_8));
                stream.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void output(Archetype template) {
        output(template, "");

    }

    private void output(Archetype template, String suffix) {
        String outputDir = "/Users/pieter.bos/projects/openehr/covid/";

        String fileName = outputDir + template.getArchetypeId().toString() + suffix + ".adls";

        try(FileOutputStream stream = new FileOutputStream(fileName)) {
            stream.write(ADLArchetypeSerializer.serialize(template).getBytes(Charsets.UTF_8));
            stream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private InMemoryFullArchetypeRepository createRepository(ADL2ConversionResultList converted, Archetype extraArchetype) {
        InMemoryFullArchetypeRepository adl2Repository = new InMemoryFullArchetypeRepository();
        for(ADL2ConversionResult conversionResult:converted.getConversionResults()) {
            if(conversionResult.getException() == null && conversionResult.getArchetype() != null) {
                adl2Repository.addArchetype(conversionResult.getArchetype());
            }
        }
        if(extraArchetype != null) {
            adl2Repository.addArchetype(extraArchetype);
        }
        adl2Repository.compile(BuiltinReferenceModels.getMetaModels());

        for(ValidationResult validationResult:adl2Repository.getAllValidationResults()) {
            if(!validationResult.passes()) {
                throw new RuntimeException(MessageFormat.format("error validating {0}: {1}", validationResult.getArchetypeId(), validationResult.getErrors()));
            }
        }
        return adl2Repository;
    }

    private void parseADL14Archetypes(MetaModels metaModels, ADL14ConversionConfiguration adl14ConversionConfiguration, List<Archetype> archetypes) {
        for(String fileName:archetypeFiles) {
            try(InputStream stream = getClass().getResourceAsStream(OPT_JSON_ARCHETYPES + fileName)) {

                ADL14Parser parser = new ADL14Parser(metaModels);
                Archetype archetype = parser.parse(stream, adl14ConversionConfiguration);
                archetypes.add(archetype);
                assertTrue(fileName + " should not contain errors", parser.getErrors().hasNoErrors());
            } catch (Exception e) {
                throw new RuntimeException(fileName + " did not parse", e);
            }
        }
    }

    private void checkConversions(ADL2ConversionResultList converted) {
        for(ADL2ConversionResult conversionResult:converted.getConversionResults()) {
            if(conversionResult.getException() != null) {
                throw new RuntimeException("problem converting archetype " + conversionResult.getArchetypeId(), conversionResult.getException());
            }
        }
    }
}
