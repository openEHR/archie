package com.nedap.archie.creation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Joiner;
import com.nedap.archie.adlparser.ADLParseException;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.archetypevalidator.ValidationResult;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.FullArchetypeRepository;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.flattener.OperationalTemplateProvider;
import com.nedap.archie.json.ArchieJacksonConfiguration;
import com.nedap.archie.openehr.serialisation.json.OpenEhrRmJacksonUtil;
import com.nedap.archie.json.JsonSchemaValidator;
import com.nedap.archie.base.RMObject;
import com.nedap.archie.test.CkmRepositoryBuilder;
import org.openehr.rm.composition.Observation;
import com.nedap.archie.openehr.rminfo.OpenEhrRmInfoLookup;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessage;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessageType;
import com.nedap.archie.rmobjectvalidator.RMObjectValidator;
import com.nedap.archie.testutil.DummyOperationalTemplateProvider;
import org.junit.Test;
import org.leadpony.justify.api.Problem;
import org.openehr.bmm.core.BmmModel;
import org.openehr.referencemodels.AllMetaModelsInitialiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExampleJsonInstanceGeneratorTest {

    private static Logger logger = LoggerFactory.getLogger(ExampleJsonInstanceGeneratorTest.class);

    private static final String TYPE_PROPERTY_NAME = "_type";

    private static final Pattern REGEX_VALIDATION_ERROR = Pattern.compile("The value \"/.*/\" must be \"/.*/\"");

    private static final OperationalTemplateProvider optProvider = new DummyOperationalTemplateProvider("archetype-slot");

    @Test
    public void bloodPressure() throws Exception {
        OperationalTemplate opt = createOPT("/ckm-mirror/local/archetypes/entry/observation/openEHR-EHR-OBSERVATION.blood_pressure.v1.1.0.adls");
        ExampleJsonInstanceGenerator structureGenerator = createExampleJsonInstanceGenerator();

        Map<String, Object> structure = structureGenerator.generate(opt);
        String s = serializeToJson(structure, true);
        //System.out.println(s);

        Map<String, Object> data = (Map<String, Object>) structure.get("data");
        assertEquals("HISTORY", data.get(TYPE_PROPERTY_NAME));
        Map<String, Object> name = (Map<String, Object>) data.get("name");
        assertEquals("history", name.get("value"));
        assertEquals("id2", data.get("archetype_node_id"));


        //assert that the required encoding property is set, even though not present in archetype
        Map<String, Object> encoding = (Map<String, Object>) structure.get("encoding");
        assertEquals("CODE_PHRASE", encoding.get(TYPE_PROPERTY_NAME));
        Map<String, Object> terminologyId = (Map<String, Object>) encoding.get("terminology_id");
        assertEquals("the default value for a terminology id encoding should be \"IANA_character-sets\"", "IANA_character-sets", terminologyId.get("value"));
        List events = (List) data.get("events");
        assertEquals(3, events.size());
        assertEquals("POINT_EVENT", ((Map) events.get(0)).get(TYPE_PROPERTY_NAME));
        assertEquals("POINT_EVENT", ((Map) events.get(1)).get(TYPE_PROPERTY_NAME));
        assertEquals("INTERVAL_EVENT", ((Map) events.get(2)).get(TYPE_PROPERTY_NAME));

        List<RMObjectValidationMessage> validated = new RMObjectValidator(OpenEhrRmInfoLookup.getInstance(), optProvider)
                .validate(opt, OpenEhrRmJacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant()).readValue(s, Observation.class));
        assertEquals(new ArrayList<>(), validated);

    }


    /**
     * Parse into actual RM instances and see if it parses correctly
     * @throws Exception
     */
    @Test
    public void parseBloodPressure() throws Exception {
        //check that the generated blood pressure can be parsed
        OperationalTemplate opt = createOPT("/ckm-mirror/local/archetypes/entry/observation/openEHR-EHR-OBSERVATION.blood_pressure.v1.1.0.adls");
        ExampleJsonInstanceGenerator structureGenerator = createExampleJsonInstanceGenerator();

        Map<String, Object> structure = structureGenerator.generate(opt);
        String s = serializeToJson(structure, true);
        System.out.println(s);
        RMObject rmObject = getArchieObjectMapper().readValue(s, RMObject.class);
        assertTrue(rmObject instanceof Observation);
    }

    private ObjectMapper getArchieObjectMapper() {
        ArchieJacksonConfiguration configuration = ArchieJacksonConfiguration.createStandardsCompliant();
        configuration.setTypePropertyName("_type");
        configuration.setAddExtraFieldsInArchetypeId(false);
        configuration.setAddPathProperty(false);
        configuration.setAlwaysIncludeTypeProperty(true);
        configuration.setFailOnUnknownProperties(true);
        configuration.setSerializeEmptyCollections(false);
        return OpenEhrRmJacksonUtil.getObjectMapper(configuration);
    }

    @Test
    /**
     * Parse into actual RM instances and see if it parses correctly
     * @throws Exception
     */
    public void parseOrdinal() throws Exception {
        //check that the generated blood pressure can be parsed
        OperationalTemplate opt = createOPT("/adl2-tests/features/aom_structures/tuples/openEHR-EHR-OBSERVATION.ordinal_tuple.v1.0.0.adls");
        ExampleJsonInstanceGenerator structureGenerator = createExampleJsonInstanceGenerator();

        Map<String, Object> structure = structureGenerator.generate(opt);
        String s = serializeToJson(structure, false);
        RMObject rmObject = getArchieObjectMapper().readValue(s, RMObject.class);
        assertTrue(rmObject instanceof Observation);
    }



    @Test
    public void ordinal() throws Exception {
        //ordinal handling in openEHR RM is a bit tricky, since a CTerminologyCode maps directly to a DV_CODED_TEXT
        OperationalTemplate opt = createOPT("/adl2-tests/features/aom_structures/tuples/openEHR-EHR-OBSERVATION.ordinal_tuple.v1.0.0.adls");
        ExampleJsonInstanceGenerator structureGenerator = createExampleJsonInstanceGenerator();

        Map<String, Object> structure = structureGenerator.generate(opt);
        String s = serializeToJson(structure, false);
        //check the ordinal creation, including correct DV_CODED_TEXT and CODE_PHRASE
        assertTrue(s.contains("{\"_type\":\"DV_ORDINAL\",\"value\":0,\"symbol\":{\"_type\":\"DV_CODED_TEXT\",\"value\":\"Absent\",\"defining_code\":{\"_type\":\"CODE_PHRASE\",\"terminology_id\":{\"_type\":\"TERMINOLOGY_ID\",\"value\":\"local\"},\"code_string\":\"at11\"}}}"));
    }

    /**
     * Tests all CKM examples with the Archie JSON Schema, that properly handles polymorphism
     * This probably will go away once we have a proper solution
     * @throws Exception
     */
    @Test
    public void generateAllCKMExamples() throws Exception {
        ExampleJsonInstanceGenerator structureGenerator = createExampleJsonInstanceGenerator();
        FullArchetypeRepository repository = CkmRepositoryBuilder.parseCKM();//add string regex filename fiter param here to filter files
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        int numberCreated = 0, validationFailed = 0, generatedException = 0, jsonSchemaValidationRan = 0, jsonSchemaValidationFailed = 0;
        int secondJsonSchemaValidationRan = 0, reserializedJsonSchemaValidationFailed = 0;
        int rmObjectValidatorRan = 0, rmObjectValidatorFailed = 0;
        repository.compile(AllMetaModelsInitialiser.getMetaModels());
        BmmModel model = AllMetaModelsInitialiser.getBmmRepository().getModel("openehr_rm_1.0.4").getModel();
        JsonSchemaValidator firstValidator = new JsonSchemaValidator(model, true);
        JsonSchemaValidator secondValidator = new JsonSchemaValidator(model,false);

        ObjectMapper archieObjectMapper = getArchieObjectMapper();
        List<String> rmValidationErrors = new ArrayList<>();

        for(ValidationResult result:repository.getAllValidationResults()) {
            if(result.passes()) {
                String json = "";
                try {
                    Flattener flattener = new Flattener(repository, AllMetaModelsInitialiser.getMetaModels()).createOperationalTemplate(true);
                    OperationalTemplate template = (OperationalTemplate) flattener.flatten(result.getSourceArchetype());
                    Map<String, Object> example = structureGenerator.generate(template);
                    json = mapper.writeValueAsString(example);

                    RMObject parsed = archieObjectMapper.readValue(json, RMObject.class);
                    List<RMObjectValidationMessage> validated = new RMObjectValidator(OpenEhrRmInfoLookup.getInstance(), optProvider).validate(template, parsed);

                    // Ignore some validations errors caused by unsupported features in the ExampleJsonInstanceGenerator
                    validated.removeIf(m -> m.getType().equals(RMObjectValidationMessageType.ARCHETYPE_SLOT_ID_MISMATCH)); // Filling the correct archetype in the slot is not supported
                    validated.removeIf(m -> m.getMessage().contains("The value local::term code must be")); // Cannot generate values for non-local value sets
                    validated.removeIf(m -> m.getPath().endsWith("/is_integral[id9999]") && m.getMessage().equals("The value true must be false")); // Calculated value
                    validated.removeIf(m -> m.getPath().endsWith("/offset/value[id9999]")); // Calculated value
                    validated.removeIf(m -> REGEX_VALIDATION_ERROR.matcher(m.getMessage()).matches()); // Cannot fill values with a regular expression as constraint

                    rmObjectValidatorRan++;
                    if(!validated.isEmpty()) {
                        rmValidationErrors.add("error in " + result.getArchetypeId() + ":\n    " + validated.stream().map(RMObjectValidationMessage::toString).collect(Collectors.joining("\n    ")));
                        rmObjectValidatorFailed++;
                    }
                    //assertEquals("error in " + result.getArchetypeId(), new ArrayList<>(), validated);
                    numberCreated++;

                    jsonSchemaValidationRan++;
                    List<Problem> problems = firstValidator.validate(json);
                    if(problems.size() > 0) {
                        logger.error("validation failed for {}", result.getArchetypeId());
                        logger.error(Joiner.on("\n").join(problems));
                        jsonSchemaValidationFailed++;
                        continue;
                    }

                    //logger.info("first validation ok for {}", result.getArchetypeId());

                    String serializedAgain = archieObjectMapper.writeValueAsString(parsed);
                    secondJsonSchemaValidationRan++;
                    List<Problem> secondProblems = secondValidator.validate(serializedAgain);
                    if(secondProblems.size() > 0) {
                        logger.error("second validation failed for {}", result.getArchetypeId());
                        logger.error(Joiner.on("\n").join(secondProblems));
                        reserializedJsonSchemaValidationFailed++;
                        continue;
                    }

                } catch (Exception e) {
                    if(generatedException <= 100) {
                        logger.error("error generating example for " + result.getArchetypeId(), e);
                        //logger.error(json);
                    }
                    generatedException++;
                }
            } else {
                validationFailed++;
            }


        }
        logger.info("error validation: " + Joiner.on("\n").join(rmValidationErrors));
        logger.info("created " + numberCreated + " examples, " + validationFailed + " archetypes failed to validate so were skipped, " + generatedException + " threw exception in test");
        logger.info("failed validation " + jsonSchemaValidationFailed + " of " + jsonSchemaValidationRan);
        logger.info("failed validation of reserialized json " + reserializedJsonSchemaValidationFailed + " of " + secondJsonSchemaValidationRan);
        logger.info("failed validation of RM Objects+invariants " + rmObjectValidatorFailed + " of " + rmObjectValidatorRan);
        assertEquals("Example JSON schema should not fail", 0, jsonSchemaValidationFailed);
        assertEquals("Example JSON schema serialized from RM implementation should not fail", 0, reserializedJsonSchemaValidationFailed);
        assertEquals("RMObjectValidator should not fail", 0, rmObjectValidatorFailed);
        assertEquals("no exceptions should occur during schema validation", 0, generatedException);
        assertEquals("example data from all archetypes should be validated", 403, jsonSchemaValidationRan);
        assertEquals("example data from all archetypes should be validated from the rm", 403, secondJsonSchemaValidationRan);

    }

    private ExampleJsonInstanceGenerator createExampleJsonInstanceGenerator() {
        ExampleJsonInstanceGenerator structureGenerator = new ExampleJsonInstanceGenerator(AllMetaModelsInitialiser.getMetaModels(), "en");
        structureGenerator.setTypePropertyName(TYPE_PROPERTY_NAME);
        return structureGenerator;
    }


    private OperationalTemplate createOPT(String s2) throws IOException, ADLParseException {
        Archetype archetype = parse(s2);
        InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
        repository.addArchetype(archetype);
        return (OperationalTemplate) new Flattener(repository, AllMetaModelsInitialiser.getMetaModels()).createOperationalTemplate(true).flatten(archetype);
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

    private String serializeToJson(Map<String, Object> structure, boolean indent) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        if(indent) {
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        } else {
            objectMapper.disable(SerializationFeature.INDENT_OUTPUT);
        }
        return objectMapper.writeValueAsString(structure);
    }
}
