package org.openehr.odin.jackson3.serializers;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * ODIN map keys serialize differently from propery field names.
 * Jackson has a mechanism that allows you to set a key serializer. So use this class for that, it adds the key in the
 * ["key_name"] format without affecting object property names
 */
public class OdinIntegerMapKeySerializer extends ValueSerializer<Integer> {

    @Override
    public void serialize(Integer value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        gen.writeName("[" + value + "]");
    }
}
