package com.nedap.archie.json;

import com.google.common.base.Charsets;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.openehr.bmm.core.BmmModel;

import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonStructure;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Validates JSON files against a schema automatically created from a BMM repository.
 *
 * <p>
 *     This is a Convenience API, you can easily manually do the same with the JsonSchemaCreator.
 * </p>
 *
 * Note that currently the root JSON element must have a '_type' property for the schema validator to know which type to validate
 */
public class JsonSchemaValidator2 {

    /** The generated json schema files, in memory */
    private final Map<String, JsonObject> schemaFiles;
    /** a cache of earlier resolved schemas, to not cause too many performance problems */

    /** the single resolved schema */
    Schema schema;


    /**
     * Creates a JsonSchemaValidator that validates against the json schema created from the given Bmm Model
     * The JSON Schema complies to the JSON format that the OpenEHR project uses. This may very well be different from
     * the serialization rules corresponding to your own different BMM file, if it is not an OpenEHR model.
     *
     * @param bmmModel the model to create the JSON Schema for
     * @param allowAdditionalProperties whether to allow additional properties in the JSON
     */
    public JsonSchemaValidator2(BmmModel bmmModel, boolean allowAdditionalProperties) {
        schemaFiles = new LinkedHashMap<>();
        new JSONSchemaCreator()
                .allowAdditionalProperties(allowAdditionalProperties)
                .withBaseUri("http://something/")
                //the validator can actually handle a schema split in multiple files, but
                //Justify's implementation is not perfect, causing some extra memory use that might be better to avoid.
                .splitInMultipleFiles(true)
                .withFullReferences(true)
                .withJsonSchemaUriProvider(new ItsJsonUriProvider("http://something/", "main.json"))
                .create(bmmModel)
                .forEach( (uri, schema) -> schemaFiles.put(uri.getId(), schema));
        //The first entry in schemaFiles is guaranteed to be the main schema by the JSONSchemaCreator.
        JsonObject schemaJson = schemaFiles.values().iterator().next();

        JSONObject rawSchema = new JSONObject(new JSONTokener(schemaJson.toString()));
        SchemaLoader.SchemaLoaderBuilder schemaLoaderBuilder = SchemaLoader.builder()
                .schemaJson(rawSchema);
        schemaFiles.forEach( (key, value) -> {
            try {
                schemaLoaderBuilder.registerSchemaByURI(new URI(key), new JSONObject(new JSONTokener(value.toString())));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
        schema = schemaLoaderBuilder.build().load().build();

    }


    private ByteArrayInputStream createByteArrayInputStream(String json) {
        return new ByteArrayInputStream(json.getBytes(Charsets.UTF_8));
    }

    /**
     * Validate the given json against the schema
     * @param json the json
     * @return the list of problems found during validation, or an empty list if the json validated
     * @throws IOException
     */
    public void validate(String json) throws IOException {
        JSONObject jsonObject = new JSONObject(new JSONTokener(json));
        schema.validate(jsonObject);

    }

}
