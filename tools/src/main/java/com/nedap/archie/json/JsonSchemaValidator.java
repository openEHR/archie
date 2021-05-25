package com.nedap.archie.json;

import com.google.common.base.Charsets;
import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonSchemaReader;
import org.leadpony.justify.api.JsonSchemaReaderFactory;
import org.leadpony.justify.api.JsonValidationService;
import org.leadpony.justify.api.Problem;
import org.leadpony.justify.api.ProblemHandler;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.v2.validation.BmmRepository;

import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
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
public class JsonSchemaValidator {

    /** The generated json schema files, in memory */
    private final Map<String, JsonObject> schemaFiles;
    /** a cache of earlier resolved schemas, to not cause too many performance problems */
    private final Map<String, JsonSchema> resolvedSchemas = new LinkedHashMap<>();
    /** the single resolved schema */
    JsonSchema schema;

    private JsonSchemaReaderFactory readerFactory;
    private JsonValidationService service;

    /**
     * Creates a JsonSchemaValidator that validates against the json schema created from the given Bmm Model
     * The JSON Schema complies to the JSON format that the OpenEHR project uses. This may very well be different from
     * the serialization rules corresponding to your own different BMM file, if it is not an OpenEHR model.
     *
     * @param bmmModel the model to create the JSON Schema for
     * @param allowAdditionalProperties whether to allow additional properties in the JSON
     */
    public JsonSchemaValidator(BmmModel bmmModel, boolean allowAdditionalProperties) {
        schemaFiles = new JSONSchemaCreator()
                .allowAdditionalProperties(allowAdditionalProperties)
                //the validator can actually handle a schema split in multiple files, but
                //Justify's implementation is not perfect, causing some extra memory use that might be better to avoid.
                .splitInMultipleFiles(false)
                .create(bmmModel);
        //The first entry in schemaFiles is guaranteed to be the main schema by the JSONSchemaCreator.
        JsonObject schemaJson = schemaFiles.values().iterator().next();


        service = JsonValidationService.newInstance();
        //the following is for multi-file validation only, and can be skipped altogether to validate just a single-file
        //schema
        readerFactory = service
                .createSchemaReaderFactoryBuilder()
                .withSchemaResolver(this::resolveSchema)
                .build();

        try (JsonSchemaReader schemaReader = readerFactory
                .createSchemaReader(createByteArrayInputStream(schemaJson.toString()))) {
            schema = schemaReader.read();
        }

    }


    /**
     * Resolves the referenced JSON schema.
     *
     * @param id the identifier of the referenced JSON schema.
     * @return referenced JSON schema.
     */
    private JsonSchema resolveSchema(URI uri) {

        String filename = uri.toString().replaceAll("#", "");
        JsonSchema resolvedSchema = resolvedSchemas.get(filename);
        if(resolvedSchema != null) {
            return resolvedSchema;
        }
        JsonObject schema = schemaFiles.get(filename);
        if ( schema == null ) {
            return null;
        }
        try (JsonSchemaReader reader = readerFactory.createSchemaReader(createByteArrayInputStream(schema.toString()))) {
            resolvedSchema = reader.read();
            //this caching approach is not perfect: as part of reader.read(), it will call this same function again to
            //resolve any referenced schemas, before returning frmo reader.read().
            //this can mean the result is not yet cached, and will be generated twice. Not something that can be easily fixed
            // however if no cache is used at all, there will be OutOfMemoryExceptions or very long runtime.
            resolvedSchemas.put(filename, resolvedSchema);
            return resolvedSchema;
        }

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
    public List<Problem> validate(String json) throws IOException {

        List<Problem> allProblems = new ArrayList<>();
        ProblemHandler problemHandler = new ProblemHandler() {
            @Override
            public void handleProblems(List<Problem> problems) {
                allProblems.addAll(problems);
            }
        };

        try (JsonReader reader = service.createReader(createByteArrayInputStream(json), schema, problemHandler)) {
            JsonStructure structure = reader.read();
            return allProblems;
        }
    }

}
