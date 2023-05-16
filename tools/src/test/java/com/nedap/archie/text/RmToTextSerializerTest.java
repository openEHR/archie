package com.nedap.archie.text;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.nedap.archie.adlparser.ADLParseException;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.creation.ExampleJsonInstanceGenerator;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.json.ArchieJacksonConfiguration;
import com.nedap.archie.json.JacksonUtil;
import com.nedap.archie.rm.composition.Observation;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessage;
import com.nedap.archie.rmobjectvalidator.RMObjectValidator;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class RmToTextSerializerTest {

    private static final String TYPE_PROPERTY_NAME = "_type";
    @Test
    public void bloodPressure() throws Exception {
        OperationalTemplate opt = createOPT("/ckm-mirror/local/archetypes/entry/observation/openEHR-EHR-OBSERVATION.blood_pressure.v1.1.0.adls");
        ExampleJsonInstanceGenerator structureGenerator = createExampleJsonInstanceGenerator();

        Map<String, Object> structure = structureGenerator.generate(opt);
        String s = serializeToJson(structure, true);
        //System.out.println(s);

        Observation observation = JacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant()).readValue(s, Observation.class);
        RmToTextSerializer rmToTextSerializer = new RmToTextSerializer();
        rmToTextSerializer.writeToText(observation);
        System.out.println(rmToTextSerializer.toString());

    }

    private ExampleJsonInstanceGenerator createExampleJsonInstanceGenerator() {
        ExampleJsonInstanceGenerator structureGenerator = new ExampleJsonInstanceGenerator(BuiltinReferenceModels.getMetaModels(), "nl");
        structureGenerator.setTypePropertyName(TYPE_PROPERTY_NAME);
        return structureGenerator;
    }

    private String serializeToJson(Map<String, Object> structure, boolean indent) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        if(indent) {
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        } else {
            objectMapper.disable(SerializationFeature.INDENT_OUTPUT);
        }
        return objectMapper.writeValueAsString(structure);
    }

    private OperationalTemplate createOPT(String s2) throws IOException, ADLParseException {
        Archetype archetype = parse(s2);
        InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
        repository.addArchetype(archetype);
        return (OperationalTemplate) new Flattener(repository, BuiltinReferenceModels.getMetaModels()).createOperationalTemplate(true).flatten(archetype);
    }

    private Archetype parse(String filename) throws IOException, ADLParseException {
        ADLParser parser = new ADLParser();
        Archetype archetype;
        try(InputStream stream =  getClass().getResourceAsStream(filename)) {
            archetype = parser.parse(stream);
            if(parser.getErrors().hasErrors()) {
                throw new RuntimeException(parser.getErrors().toString());
            }
        }
        return archetype;
    }
}
