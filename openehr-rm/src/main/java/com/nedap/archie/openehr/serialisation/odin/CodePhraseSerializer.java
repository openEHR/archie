package com.nedap.archie.openehr.serialisation.odin;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.openehr.rm.datatypes.CodePhrase;

import java.io.IOException;

public class CodePhraseSerializer extends JsonSerializer<CodePhrase> {
    @Override
    public void serialize(CodePhrase value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String termId = value.getTerminologyId() == null ? null : value.getTerminologyId().getValue();
        String code = value.getCodeString();
        gen.writeRawValue("[" + termId + "::" + code + "]");
    }
}
