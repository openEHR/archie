package com.nedap.archie.rm.composition;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nedap.archie.json.JacksonUtil;
import com.nedap.archie.json.RMJacksonConfiguration;
import com.nedap.archie.xml.JAXBUtil;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

public class CompositionTest {

    @Test
    public void testEqual() throws JAXBException {
        Unmarshaller unmarshaller = JAXBUtil.getArchieJAXBContext().createUnmarshaller();
        Composition composition1 = (Composition) unmarshaller.unmarshal(getClass().getResourceAsStream("test_all_types.fixed.v1.xml"));
        Composition composition2 = (Composition) unmarshaller.unmarshal(getClass().getResourceAsStream("test_all_types.fixed.v1.xml"));
        assertEquals(composition1, composition2);
    }

    @Test
    public void testJsonStandardConfig() throws IOException {
        Composition expected = parseJson("validation_composition_test.v0.json");
        RMJacksonConfiguration standardConfig = RMJacksonConfiguration.createStandardsCompliant();

        Composition actual = processComposition(expected, standardConfig);
        assertEquals(expected, actual);
    }

    @Test
    public void testJsonDefaultConfig() throws IOException {
        Composition expected = parseJson("validation_composition_test.v0.json");
        RMJacksonConfiguration config = new RMJacksonConfiguration();

        Composition actual = processComposition(expected, config);
        assertEquals(expected, actual);
    }

    @Test
    public void testJsonJavascriptConfig() throws IOException {
        Composition expected = parseJson("validation_composition_test.v0.json");
        RMJacksonConfiguration javascriptConfig = RMJacksonConfiguration.createConfigForJavascriptUsage();

        Composition actual = processComposition(expected, javascriptConfig);
        assertEquals(expected, actual);
    }

    private Composition processComposition(Composition original, RMJacksonConfiguration configuration) throws IOException {
        ObjectMapper objectMapper = JacksonUtil.getObjectMapper(configuration);

        StringWriter json = new StringWriter();
        objectMapper.writeValue(json, original);
        return objectMapper.readValue(json.toString(), Composition.class);
    }

    private Composition parseJson(String resourceName) throws IOException {
        ObjectMapper objectMapper = JacksonUtil.getObjectMapper(RMJacksonConfiguration.createStandardsCompliant());
        return objectMapper.readValue(getClass().getResourceAsStream(resourceName), Composition.class);
    }
}