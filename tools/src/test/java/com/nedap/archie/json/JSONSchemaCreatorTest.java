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
        BmmModel model = BuiltinReferenceModels.getBmmRepository().getModel("openehr_rm_1.1.0").getModel();
        Map<String, JsonObject> schemas = new JSONSchemaCreator().create(model);

        Map<String, Object> config = new HashMap<>();
        config.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory jsonWriterFactory = Json.createWriterFactory(config);

        printSchemas(schemas, jsonWriterFactory);

    }

    private void printSchemas(Map<String, JsonObject> schemas, JsonWriterFactory jsonWriterFactory) {
        for(String name:schemas.keySet()) {
            JsonObject schema = schemas.get(name);
            System.out.println("printing schema " + name + ":");
            jsonWriterFactory.createWriter(System.out).write(schema);
        }
    }

    @Test
    public void createSchemaWithoutAdditionalProperties() {
        BmmModel model = BuiltinReferenceModels.getBmmRepository().getModel("openehr_rm_1.1.0").getModel();
        Map<String, JsonObject> schemas = new JSONSchemaCreator().allowAdditionalProperties(false).create(model);


        Map<String, Object> config = new HashMap<>();
        config.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory jsonWriterFactory = Json.createWriterFactory(config);

        printSchemas(schemas, jsonWriterFactory);
    }

    @Test
    public void createMultiFileSchemaWithoutAdditionalProperties() {
        BmmModel model = BuiltinReferenceModels.getBmmRepository().getModel("openehr_rm_1.1.0").getModel();
        Map<String, JsonObject> schemas = new JSONSchemaCreator().allowAdditionalProperties(false).splitInMultipleFiles(true).create(model);


        Map<String, Object> config = new HashMap<>();
        config.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory jsonWriterFactory = Json.createWriterFactory(config);

        printSchemas(schemas, jsonWriterFactory);
    }
}
