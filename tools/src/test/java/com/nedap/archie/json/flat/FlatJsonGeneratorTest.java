package com.nedap.archie.json.flat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.creation.ExampleJsonInstanceGenerator;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.FlattenerConfiguration;
import com.nedap.archie.flattener.SimpleArchetypeRepository;
import com.nedap.archie.json.JacksonUtil;
import com.nedap.archie.rm.RMObject;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;

public class FlatJsonGeneratorTest {

    private static final String BLOOD_PRESSURE_PATH = "/ckm-mirror/local/archetypes/entry/observation/openEHR-EHR-OBSERVATION.blood_pressure.v1.1.0.adls";
    private static final double EPSILON = 0.00000001d;

    @Test
    public void testBloodPressureExample() throws Exception {

        OperationalTemplate bloodPressureOpt = parseBloodPressure();
        FlatJsonFormatConfiguration config = FlatJsonFormatConfiguration.standardFormatInDevelopment();
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
    public void testBloodPressureExampleWithPipesForFinalFields() throws Exception {

        OperationalTemplate bloodPressureOpt = parseBloodPressure();
        FlatJsonFormatConfiguration config = FlatJsonFormatConfiguration.standardFormatInDevelopment();
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
        config.setWritePipesForPrimitiveTypes(false);
        Map<String, Object> stringObjectMap = createExampleInstance(bloodPressureOpt, config);

        System.out.println(JacksonUtil.getObjectMapper().writeValueAsString(stringObjectMap));

        //type property
        assertEquals("OBSERVATION", stringObjectMap.get("/@type"));
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
        assertEquals("Systolic", stringObjectMap.get("/data[id2]/events[id7,1]/data[id4]/items[id5]/name/value"));
    }

    private OperationalTemplate parseBloodPressure() throws IOException {
        try (InputStream stream = getClass().getResourceAsStream(BLOOD_PRESSURE_PATH)) {
            Archetype bloodPressure = new ADLParser(BuiltinReferenceModels.getMetaModels()).parse(stream);
            Flattener flattener = new Flattener(new SimpleArchetypeRepository(), BuiltinReferenceModels.getMetaModels(), FlattenerConfiguration.forOperationalTemplate());
            return (OperationalTemplate) flattener.flatten(bloodPressure);
        }
    }

    private Map<String, Object> createExampleInstance(OperationalTemplate bloodPressureOpt, FlatJsonFormatConfiguration config) throws IOException, DuplicateKeyException {
        return new FlatJsonExampleInstanceGenerator().generateExample(bloodPressureOpt, BuiltinReferenceModels.getMetaModels(), "en", config);
    }

}
