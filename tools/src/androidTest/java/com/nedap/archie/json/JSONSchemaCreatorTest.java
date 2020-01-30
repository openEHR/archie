package com.nedap.archie.json;


import org.junit.Test;
import org.openehr.bmm.core.BmmModel;
import org.openehr.referencemodels.BuiltinReferenceModels;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import java.util.HashMap;
import java.util.Map;

public class JSONSchemaCreatorTest {

    @Test
    public void createSchema() {
        BmmModel model = BuiltinReferenceModels.getBmmRepository().getModel("openehr_rm_1.0.4").getModel();
        JsonObject jsonObject = new JSONSchemaCreator().create(model);

        Map<String, Object> config = new HashMap();
        config.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory jsonWriterFactory = Json.createWriterFactory(config);


        jsonWriterFactory.createWriter(System.out).write(jsonObject);
    }

    @Test
    public void createSchemaWithoutAdditionalProperties() {
        BmmModel model = BuiltinReferenceModels.getBmmRepository().getModel("openehr_rm_1.0.4").getModel();
        JsonObject jsonObject = new JSONSchemaCreator().allowAdditionalProperties(false).create(model);

        Map<String, Object> config = new HashMap();
        config.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory jsonWriterFactory = Json.createWriterFactory(config);


        jsonWriterFactory.createWriter(System.out).write(jsonObject);
    }
}
