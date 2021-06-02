package com.nedap.archie.openapi;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonWriterFactory;
import jakarta.json.stream.JsonGenerator;
import org.junit.Test;
import org.openehr.bmm.core.BmmModel;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.util.HashMap;
import java.util.Map;

public class OpenAPIModelCreatorTest {

    @Test
    public void createSchema() {
        BmmModel model = BuiltinReferenceModels.getBmmRepository().getModel("openehr_rm_1.0.4").getModel();
        JsonObject jsonObject = new OpenAPIModelCreator().allowAdditionalProperties(true).create(model);

        Map<String, Object> config = new HashMap();
        config.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory jsonWriterFactory = Json.createWriterFactory(config);


        jsonWriterFactory.createWriter(System.out).write(jsonObject);
    }
}
