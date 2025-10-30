
package com.nedap.archie.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;

/**
 * Jackson deserializer for the lifecycle_state field.
 * <p>
 * Supports two JSON representations:
 * 1. Simple string form (current): {@code "lifecycle_state": "published"}
 * 2. Complex object form (legacy): {@code "lifecycle_state": {"code_string": "published"}}
 *
 * This deserializer ensures backward compatibility by accepting both formats
 * and always returning the lifecycle state value as a plain String.
 * <p>
 * Created by pieter.bos on 30/06/16.
 */
public class LifecycleStateDeserializer extends JsonDeserializer<String> {

    /**
     * Deserializes a lifecycle_state value from JSON.
     * <p>
     * Handles two cases:
     * - Simple string: directly returns the string value
     * - Complex object: extracts the "code_string" field value
     *
     * @param p the JSON parser positioned at the lifecycle_state value
     * @param ctxt the deserialization context
     * @return the lifecycle state as a String, or null if the value is null
     * @throws IOException if an I/O error occurs during parsing
     * @throws JsonMappingException if the value is neither a string nor a valid object with "code_string"
     */
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t = p.getCurrentToken();

        if (t == JsonToken.VALUE_STRING) {
            return p.getValueAsString();
        }

        if (t == JsonToken.START_OBJECT) {
            ObjectNode node = p.getCodec().readTree(p);

            if (node.hasNonNull("code_string")) {
                return node.get("code_string").asText();
            }
        }

        // Unexpected case
        return null;
    }
}