package com.nedap.archie.odin3;

import com.nedap.archie.rm.datatypes.CodePhrase;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * Jackson 3 port of {@link com.nedap.archie.odin.CodePhraseSerializer}.
 */
public class CodePhraseSerializer extends ValueSerializer<CodePhrase> {

    @Override
    public void serialize(CodePhrase value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        String termId = value.getTerminologyId() == null ? null : value.getTerminologyId().getValue();
        String code = value.getCodeString();
        gen.writeRawValue("[" + termId + "::" + code + "]");
    }
}
