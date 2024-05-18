package com.nedap.archie.json;


import org.junit.Test;
import org.openehr.bmm.core.BmmModel;
import org.openehr.referencemodels.AllMetaModelsInitialiser;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonWriterFactory;
import jakarta.json.stream.JsonGenerator;
import java.util.HashMap;
import java.util.Map;

public class JSONSchemaCreatorTest {

    @Test
    public void createSchema() {
        BmmModel model = AllMetaModelsInitialiser.getBmmRepository().getModel("openehr_rm_1.1.0").getModel();
        Map<JsonSchemaUri, JsonObject> schemas = new OpenEHRRmJSONSchemaCreator().create(model);

        Map<String, Object> config = new HashMap<>();
        config.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory jsonWriterFactory = Json.createWriterFactory(config);

        printSchemas(schemas, jsonWriterFactory);

    }

    private void printSchemas(Map<JsonSchemaUri, JsonObject> schemas, JsonWriterFactory jsonWriterFactory) {
        for(JsonSchemaUri name:schemas.keySet()) {
            JsonObject schema = schemas.get(name);
            System.out.println("printing schema " + name + ":");
            jsonWriterFactory.createWriter(System.out).write(schema);
        }
    }

    @Test
    public void createSchemaWithoutAdditionalProperties() {
        BmmModel model = AllMetaModelsInitialiser.getBmmRepository().getModel("openehr_rm_1.1.0").getModel();
        Map<JsonSchemaUri, JsonObject> schemas = new OpenEHRRmJSONSchemaCreator().allowAdditionalProperties(false).create(model);


        Map<String, Object> config = new HashMap<>();
        config.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory jsonWriterFactory = Json.createWriterFactory(config);

        printSchemas(schemas, jsonWriterFactory);
    }

    @Test
    public void createMultiFileSchemaWithoutAdditionalProperties() {
        BmmModel model = AllMetaModelsInitialiser.getBmmRepository().getModel("openehr_rm_1.1.0").getModel();
        Map<JsonSchemaUri, JsonObject> schemas = new OpenEHRRmJSONSchemaCreator().allowAdditionalProperties(false).splitInMultipleFiles(true).create(model);


        Map<String, Object> config = new HashMap<>();
        config.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory jsonWriterFactory = Json.createWriterFactory(config);

        printSchemas(schemas, jsonWriterFactory);
    }
}
