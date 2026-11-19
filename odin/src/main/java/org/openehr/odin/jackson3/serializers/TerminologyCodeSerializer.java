package org.openehr.odin.jackson3.serializers;

import com.nedap.archie.base.terminology.TerminologyCode;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.jsontype.TypeSerializer;

public class TerminologyCodeSerializer extends ValueSerializer<TerminologyCode> {

    @Override
    public void serialize(TerminologyCode value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        gen.writeRawValue(value.toString());
    }

    @Override
    public void serializeWithType(TerminologyCode value, JsonGenerator gen, SerializationContext ctxt, TypeSerializer typeSer) throws JacksonException {
        gen.writeRawValue(value.toString());
    }
}
