package com.nedap.archie.odin;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.util.Converter;
import com.nedap.archie.rm.datatypes.CodePhrase;

import java.io.IOException;

public class CodePhraseSerializer extends JsonSerializer<CodePhrase> {
    @Override
    public void serialize(CodePhrase value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String termId = value.getTerminologyId() == null ? null : value.getTerminologyId().getValue();
        String code = value.getCodeString();
        gen.writeRawValue("[" + termId + "::" + code + "]");
    }
}
