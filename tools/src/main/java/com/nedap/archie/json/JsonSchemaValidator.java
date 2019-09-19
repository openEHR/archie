package com.nedap.archie.json;

import com.google.common.base.Charsets;
import org.leadpony.justify.api.JsonSchema;
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
import java.util.ArrayList;
import java.util.List;


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

    JsonSchema schema;

    /**
     * Creates a JsonSchemaValidator that validates against the json schema created from the given Bmm Model
     * The JSON Schema complies to the JSON format that the OpenEHR project uses. This may very well be different from
     * the serialization rules corresponding to your own different BMM file, if it is not an OpenEHR model.
     *
     * @param bmmModel the model to create the JSON Schema for
     * @param allowAdditionalProperties whether to allow additional properties in the JSON
     */
    public JsonSchemaValidator(BmmModel bmmModel, boolean allowAdditionalProperties) {
        JsonObject schemaJson = new JSONSchemaCreator().allowAdditionalProperties(allowAdditionalProperties).create(bmmModel);

        JsonValidationService service = JsonValidationService.newInstance();
        schema = service.readSchema(createByteArrayInputStream(schemaJson.toString()));

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

        JsonValidationService service = JsonValidationService.newInstance();
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
