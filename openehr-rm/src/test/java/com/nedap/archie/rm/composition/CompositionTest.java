package com.nedap.archie.rm.composition;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nedap.archie.json.JacksonUtil;
import com.nedap.archie.json.ArchieJacksonConfiguration;
import com.nedap.archie.xml.JAXBUtil;
import org.junit.Test;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
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
        ArchieJacksonConfiguration standardConfig = ArchieJacksonConfiguration.createStandardsCompliant();

        Composition actual = processComposition(expected, standardConfig);
        assertEquals(expected, actual);
    }

    @Test
    public void testJsonLegacyConfig() throws IOException {
        Composition expected = parseJson("validation_composition_test.v0.json");
        ArchieJacksonConfiguration config = ArchieJacksonConfiguration.createLegacyConfiguration();

        Composition actual = processComposition(expected, config);
        assertEquals(expected, actual);
    }

    @Test
    public void testJsonJavascriptConfig() throws IOException {
        Composition expected = parseJson("validation_composition_test.v0.json");
        ArchieJacksonConfiguration javascriptConfig = ArchieJacksonConfiguration.createConfigForJavascriptUsage();

        Composition actual = processComposition(expected, javascriptConfig);
        assertEquals(expected, actual);
    }

    private Composition processComposition(Composition original, ArchieJacksonConfiguration configuration) throws IOException {
        ObjectMapper objectMapper = JacksonUtil.getObjectMapper(configuration);

        StringWriter json = new StringWriter();
        objectMapper.writeValue(json, original);
        return objectMapper.readValue(json.toString(), Composition.class);
    }

    private Composition parseJson(String resourceName) throws IOException {
        ObjectMapper objectMapper = JacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant());
        return objectMapper.readValue(getClass().getResourceAsStream(resourceName), Composition.class);
    }
}