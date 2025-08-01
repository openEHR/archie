package com.nedap.archie.json.flat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.nedap.archie.ArchieLanguageConfiguration;
import com.nedap.archie.adlparser.ADLParseException;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.creation.ExampleJsonInstanceGenerator;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.FlattenerConfiguration;
import com.nedap.archie.flattener.SimpleArchetypeRepository;
import com.nedap.archie.json.JacksonUtil;
import com.nedap.archie.rm.composition.Observation;
import com.nedap.archie.rm.datastructures.Cluster;
import com.nedap.archie.rm.datastructures.Element;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.datavalues.encapsulated.DvMultimedia;
import com.nedap.archie.rm.datavalues.quantity.DvCount;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDuration;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rminfo.MetaModel;
import com.nedap.archie.rminfo.MetaModelProvider;
import org.junit.After;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;
import org.threeten.extra.PeriodDuration;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Period;
import java.util.LinkedHashMap;
import java.util.Map;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class FlatJsonGeneratorTest {

    private static final String BLOOD_PRESSURE_PATH = "/ckm-mirror/local/archetypes/entry/observation/openEHR-EHR-OBSERVATION.blood_pressure.v1.1.0.adls";
    private static final double EPSILON = 0.00000001d;

    @After
    public void tearDown() {
        ArchieLanguageConfiguration.setThreadLocalDescriptiongAndMeaningLanguage(null);
    }

    @Test
    public void testBloodPressureExample() throws Exception {

        OperationalTemplate bloodPressureOpt = parseBloodPressure();
        FlatJsonFormatConfiguration config = FlatJsonFormatConfiguration.standardFormatInDevelopment();
        config.setFilterNames(false);
        config.setFilterTypes(false);
        config.setWritePipesForPrimitiveTypes(false);
        Map<String, Object> stringObjectMap = createExampleInstance(bloodPressureOpt, config);

        System.out.println(JacksonUtil.getObjectMapper().writeValueAsString(stringObjectMap));

        //type property
        assertEquals("OBSERVATION", stringObjectMap.get("/_type"));
        //just a string
        assertEquals("Systolic", stringObjectMap.get("/data[id2]/events[id7]/data[id4]/items[id5]/name/value"));
        //ignored field
        assertFalse(stringObjectMap.containsKey("/data[id2]/archetype_node_id"));
        //ignored field
        assertFalse(stringObjectMap.containsKey("/archetype_details"));
        //date time format
        assertEquals("2018-01-01T12:00:00Z", stringObjectMap.get("/data[id2]/origin/value"));
        //numbers
        assertEquals(0.0d, (Double) stringObjectMap.get("/data[id2]/events[id7]/data[id4]/items[id5]/value/magnitude"), EPSILON);
        assertEquals(0l, ((Long) stringObjectMap.get("/data[id2]/events[id7]/data[id4]/items[id5]/value/precision")).longValue());
        //test indices
        assertEquals("Systolic", stringObjectMap.get("/data[id2]/events[id7]:1/data[id4]/items[id5]/name/value"));

    }

    @Test
    public void testBloodPressureEmpty() throws Exception {
        OperationalTemplate bloodPressureOpt = parseBloodPressure();
        FlatJsonGenerator flatJsonGenerator = new FlatJsonGenerator(ArchieRMInfoLookup.getInstance(), FlatJsonFormatConfiguration.nedapInternalFormat());

        Map<String, Object> pathsAndValues = flatJsonGenerator.buildPathsAndValues(new Observation(), bloodPressureOpt, "en");

        assertTrue(pathsAndValues.isEmpty());
    }

    @Test
    public void testBloodPressureExampleWithPipesForFinalFields() throws Exception {

        OperationalTemplate bloodPressureOpt = parseBloodPressure();
        FlatJsonFormatConfiguration config = FlatJsonFormatConfiguration.standardFormatInDevelopment();
        config.setFilterNames(false);
        config.setFilterTypes(false);
        Map<String, Object> stringObjectMap = createExampleInstance(bloodPressureOpt, config);

        System.out.println(JacksonUtil.getObjectMapper().writeValueAsString(stringObjectMap));

        //type property
        assertEquals("OBSERVATION", stringObjectMap.get("/_type"));
        //just a string
        assertEquals("Systolic", stringObjectMap.get("/data[id2]/events[id7]/data[id4]/items[id5]/name|value"));
        //ignored field
        assertFalse(stringObjectMap.containsKey("/data[id2]|archetype_node_id"));
        //ignored field
        assertFalse(stringObjectMap.containsKey("/archetype_details"));
        //date time format
        assertEquals("2018-01-01T12:00:00Z", stringObjectMap.get("/data[id2]/origin|value"));
        //numbers
        assertEquals(0.0d, (Double) stringObjectMap.get("/data[id2]/events[id7]/data[id4]/items[id5]/value|magnitude"), EPSILON);
        assertEquals(0l, ((Long) stringObjectMap.get("/data[id2]/events[id7]/data[id4]/items[id5]/value|precision")).longValue());
        //test indices
        assertEquals("Systolic", stringObjectMap.get("/data[id2]/events[id7]:1/data[id4]/items[id5]/name|value"));
    }

    @Test
    public void testNedapInternalFormat() throws Exception {
        OperationalTemplate bloodPressureOpt = parseBloodPressure();
        FlatJsonFormatConfiguration config = FlatJsonFormatConfiguration.nedapInternalFormat();
        config.setFilterNames(false);
        config.setFilterTypes(false);
        config.setWritePipesForPrimitiveTypes(false);
        Map<String, Object> stringObjectMap = createExampleInstance(bloodPressureOpt, config);

        System.out.println(JacksonUtil.getObjectMapper().writeValueAsString(stringObjectMap));

        //type property
        assertEquals("OBSERVATION", stringObjectMap.get("/@type"));
        //just a string
        assertEquals("Systolic", stringObjectMap.get("/data[id2]/events[id7,1]/data[id4]/items[id5,1]/name/value"));
        //ignored field
        assertFalse(stringObjectMap.containsKey("/data[id2]/archetype_node_id"));
        //ignored field
        assertFalse(stringObjectMap.containsKey("/archetype_details"));
        //date time format
        assertEquals("2018-01-01T12:00:00Z", stringObjectMap.get("/data[id2]/origin/value"));
        //numbers
        assertEquals(0.0d, (Double) stringObjectMap.get("/data[id2]/events[id7,1]/data[id4]/items[id5,1]/value/magnitude"), EPSILON);
        assertEquals(0l, ((Long) stringObjectMap.get("/data[id2]/events[id7,1]/data[id4]/items[id5,1]/value/precision")).longValue());
        //test indices
        assertEquals("Systolic", stringObjectMap.get("/data[id2]/events[id7,2]/data[id4]/items[id5,1]/name/value"));
    }

    @Test
    public void testDurationFlattening() throws Exception {
        FlatJsonGenerator flatJsonGenerator = new FlatJsonGenerator(ArchieRMInfoLookup.getInstance(), FlatJsonFormatConfiguration.nedapInternalFormat());

        // Test a positive duration, make sure the value is a String and not a Duration object
        DvDuration duration = new DvDuration(PeriodDuration.of(Period.of(1,0,0), Duration.ofHours(13)));
        Map<String, Object> pathsAndValues = flatJsonGenerator.buildPathsAndValues(duration);
        assertEquals("P1YT13H", pathsAndValues.get("/value"));

        // Also test a negative duration
        DvDuration negativeDuration = new DvDuration(PeriodDuration.of(Period.of(-1,-2,-4), Duration.ofSeconds(-5736)));
        Map<String, Object> secondPathsAndValues = flatJsonGenerator.buildPathsAndValues(negativeDuration);
        assertEquals("-P1Y2M4DT1H35M36S", secondPathsAndValues.get("/value"));
    }

    @Test
    public void continuesIndices() throws Exception {
        Cluster cluster = new Cluster();
        cluster.addItem(new Element("id2", null, new DvText("First")));
        cluster.addItem(new Element("id3", null, new DvCount(2L)));
        cluster.addItem(new Element("id2", null, new DvText("Third")));
        cluster.addItem(new Element("id3", null, new DvCount(4L)));

        FlatJsonFormatConfiguration config = FlatJsonFormatConfiguration.nedapInternalFormat();
        Map<String, Object> stringObjectMap = new FlatJsonGenerator(ArchieRMInfoLookup.getInstance(), config).buildPathsAndValues(cluster);

        System.out.println(JacksonUtil.getObjectMapper().writeValueAsString(stringObjectMap));

        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("/@type", "CLUSTER");
        expected.put("/items[id2,1]/@type", "ELEMENT");
        expected.put("/items[id2,1]/value/@type", "DV_TEXT");
        expected.put("/items[id2,1]/value/value", "First");
        expected.put("/items[id3,2]/@type", "ELEMENT");
        expected.put("/items[id3,2]/value/@type", "DV_COUNT");
        expected.put("/items[id3,2]/value/magnitude", 2L);
        expected.put("/items[id2,3]/@type", "ELEMENT");
        expected.put("/items[id2,3]/value/@type", "DV_TEXT");
        expected.put("/items[id2,3]/value/value", "Third");
        expected.put("/items[id3,4]/@type", "ELEMENT");
        expected.put("/items[id3,4]/value/@type", "DV_COUNT");
        expected.put("/items[id3,4]/value/magnitude", 4L);

        assertEquals(expected, stringObjectMap);
    }

    @Test
    public void serializeBytes() throws Exception {
        DvMultimedia dvMultimedia = new DvMultimedia();
        dvMultimedia.setData(new byte[]{42, 83, 120, -128, 127, 30, -80, 15});

        FlatJsonGenerator flatJsonGenerator = new FlatJsonGenerator(ArchieRMInfoLookup.getInstance(), FlatJsonFormatConfiguration.nedapInternalFormat());

        Map<String, Object> pathsAndValues = flatJsonGenerator.buildPathsAndValues(dvMultimedia);
        assertEquals("{\"/@type\":\"DV_MULTIMEDIA\",\"/data\":\"KlN4gH8esA8=\"}", new JsonMapper().writeValueAsString(pathsAndValues));
    }

    @Test
    public void dontSerializeNames() throws  Exception {
        OperationalTemplate bloodPressureOpt = parseBloodPressure();
        FlatJsonFormatConfiguration config = FlatJsonFormatConfiguration.nedapInternalFormat();
        config.setWritePipesForPrimitiveTypes(false);
        config.setFilterNames(true);
        config.setFilterTypes(false);
        //config.getIgnoredAttributes().add(new AttributeReference("LOCATABLE", "name"));
        Map<String, Object> stringObjectMap = new FlatJsonExampleInstanceGenerator().generateExample(bloodPressureOpt, BuiltinReferenceModels.getMetaModelProvider(), "en", config);

        System.out.println(JacksonUtil.getObjectMapper().writeValueAsString(stringObjectMap));

        //type property
        assertEquals("OBSERVATION", stringObjectMap.get("/@type"));
        //no name
        assertNull(stringObjectMap.get("/data[id2]/events[id7]/data[id4]/items[id5]/name/value"));
        //ignored field
        assertFalse(stringObjectMap.containsKey("/data[id2]/archetype_node_id"));
        //ignored field
        assertFalse(stringObjectMap.containsKey("/archetype_details"));
        //date time format
        assertEquals("2018-01-01T12:00:00Z", stringObjectMap.get("/data[id2]/origin/value"));
        //numbers
        assertEquals(0.0d, (Double) stringObjectMap.get("/data[id2]/events[id7,1]/data[id4]/items[id5,1]/value/magnitude"), EPSILON);
        assertEquals(0l, ((Long) stringObjectMap.get("/data[id2]/events[id7,1]/data[id4]/items[id5,1]/value/precision")).longValue());
        //no name
        assertNull(stringObjectMap.get("/data[id2]/events[id7,1]/data[id4]/items[id5]/name/value"));
    }

    @Test
    public void dontSerializeTypes() throws  Exception {
        OperationalTemplate bloodPressureOpt = parseBloodPressure();
        FlatJsonFormatConfiguration config = FlatJsonFormatConfiguration.nedapInternalFormat();
        config.setWritePipesForPrimitiveTypes(false);
        config.setFilterNames(true);
        config.setFilterTypes(true);
        //config.getIgnoredAttributes().add(new AttributeReference("LOCATABLE", "name"));
        MetaModelProvider metaModelProvider = BuiltinReferenceModels.getMetaModelProvider();
        MetaModel metaModel = metaModelProvider.getMetaModel(bloodPressureOpt);

        ExampleJsonInstanceGenerator exampleJsonInstanceGenerator = new ExampleJsonInstanceGenerator(metaModelProvider, "en");
        exampleJsonInstanceGenerator.setTypePropertyName("_type");
        Map<String, Object> generatedExample = exampleJsonInstanceGenerator.generate(bloodPressureOpt);
        ObjectMapper objectMapper = metaModel.getJsonObjectMapper();
        String jsonRmObject = objectMapper.writeValueAsString(generatedExample);
        Observation bloodPressure = objectMapper.readValue(jsonRmObject, Observation.class);
        //set the name to be different from the archetype, so it will be added here
        bloodPressure.setNameAsString("different from archetype");



        Map<String, Object> stringObjectMap = new FlatJsonGenerator(metaModel.getModelInfoLookup(), config).buildPathsAndValues(bloodPressure, bloodPressureOpt, "en");


        System.out.println(JacksonUtil.getObjectMapper().writeValueAsString(stringObjectMap));

        //no type at root
        assertNull(stringObjectMap.get("/@type"));
        //no name when the same as archetype
        assertNull(stringObjectMap.get("/data[id2]/events[id7]/data[id4]/items[id5]/name/value"));
        assertEquals("different from archetype", stringObjectMap.get("/name/value"));
        //type here is different than in archetype: POINT_EVENT in data, EVENT in archetype
        //so it must be included in the flat format
        assertEquals("POINT_EVENT", stringObjectMap.get("/data[id2]/events[id7,1]/@type"));
        //TODO: type when alternatives exist
        //no type at Element
        assertNull(stringObjectMap.get("/data[id2]/events[id7]/data[id4]/items[id5]/@type"));

        //ignored field
        assertFalse(stringObjectMap.containsKey("/data[id2]/archetype_node_id"));
        //ignored field
        assertFalse(stringObjectMap.containsKey("/archetype_details"));
        //date time format
        assertEquals("2018-01-01T12:00:00Z", stringObjectMap.get("/data[id2]/origin/value"));
        //numbers
        assertEquals(0.0d, (Double) stringObjectMap.get("/data[id2]/events[id7,1]/data[id4]/items[id5,1]/value/magnitude"), EPSILON);
        assertEquals(0l, ((Long) stringObjectMap.get("/data[id2]/events[id7,1]/data[id4]/items[id5,1]/value/precision")).longValue());
        //no name
        assertNull(stringObjectMap.get("/data[id2]/events[id7,1]/data[id4]/items[id5]/name/value"));
    }

    @Test
    public void restoresThreadLocalDescriptiongAndMeaningLanguage() throws  Exception {
        OperationalTemplate bloodPressureOpt = parseBloodPressure();
        FlatJsonFormatConfiguration config = FlatJsonFormatConfiguration.nedapInternalFormat();

        MetaModelProvider metaModelProvider = BuiltinReferenceModels.getMetaModelProvider();
        MetaModel metaModel = metaModelProvider.getMetaModel(bloodPressureOpt);

        ExampleJsonInstanceGenerator exampleJsonInstanceGenerator = new ExampleJsonInstanceGenerator(metaModelProvider, "en");
        exampleJsonInstanceGenerator.setTypePropertyName("_type");
        Map<String, Object> generatedExample = exampleJsonInstanceGenerator.generate(bloodPressureOpt);
        ObjectMapper objectMapper = metaModel.getJsonObjectMapper();
        String jsonRmObject = objectMapper.writeValueAsString(generatedExample);
        Observation bloodPressure = objectMapper.readValue(jsonRmObject, Observation.class);

        ArchieLanguageConfiguration.setThreadLocalDescriptiongAndMeaningLanguage("nl");

        new FlatJsonGenerator(metaModel.getModelInfoLookup(), config).buildPathsAndValues(bloodPressure, bloodPressureOpt, "en");

        // The ThreadLocalDescriptiongAndMeaningLanguage should be restored to nl after the buildPathsAndValues call.
        assertEquals("nl", ArchieLanguageConfiguration.getThreadLocalDescriptiongAndMeaningLanguage());
    }

    /**
     * Test filtering two types where alternatives in the archetype are possible, without a node id being persent to separate the different types
     */
    @Test
    public void filterTypesWithAlternatives() throws Exception {
        OperationalTemplate bloodPressureOpt = parseTypeAlternatives();
        FlatJsonFormatConfiguration config = FlatJsonFormatConfiguration.nedapInternalFormat();
        config.setFilterNames(true);
        config.setFilterTypes(true);
        //config.getIgnoredAttributes().add(new AttributeReference("LOCATABLE", "name"));
        Map<String, Object> stringObjectMap = new FlatJsonExampleInstanceGenerator().generateExample(bloodPressureOpt, BuiltinReferenceModels.getMetaModelProvider(), "en", config);

        System.out.println(JacksonUtil.getObjectMapper().writeValueAsString(stringObjectMap));

        //the one data value
        assertEquals("true", stringObjectMap.get("/items[id2,1]/value/value"));
        //the type
        assertEquals("DV_BOOLEAN", stringObjectMap.get("/items[id2,1]/value/@type"));
        //and nothing else!
        assertEquals(2, stringObjectMap.size());

    }


    private OperationalTemplate parseBloodPressure() throws IOException, ADLParseException {
        try (InputStream stream = getClass().getResourceAsStream(BLOOD_PRESSURE_PATH)) {
            Archetype bloodPressure = new ADLParser(BuiltinReferenceModels.getMetaModelProvider()).parse(stream);
            Flattener flattener = new Flattener(new SimpleArchetypeRepository(), BuiltinReferenceModels.getMetaModelProvider(), FlattenerConfiguration.forOperationalTemplate());
            return (OperationalTemplate) flattener.flatten(bloodPressure);
        }
    }

    private OperationalTemplate parseTypeAlternatives() throws IOException, ADLParseException {
        try (InputStream stream = getClass().getResourceAsStream("openEHR-EHR-CLUSTER.element_with_two_dv_types.v1.0.0.adls")) {
            Archetype typeAlternatives = new ADLParser(BuiltinReferenceModels.getMetaModelProvider()).parse(stream);
            Flattener flattener = new Flattener(new SimpleArchetypeRepository(), BuiltinReferenceModels.getMetaModelProvider(), FlattenerConfiguration.forOperationalTemplate());
            return (OperationalTemplate) flattener.flatten(typeAlternatives);
        }
    }

    private Map<String, Object> createExampleInstance(OperationalTemplate bloodPressureOpt, FlatJsonFormatConfiguration config) throws IOException, DuplicateKeyException {
        return new FlatJsonExampleInstanceGenerator().generateExample(bloodPressureOpt, BuiltinReferenceModels.getMetaModelProvider(), "en", config);
    }
}
