package com.nedap.archie.template.betterjson.parser;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public interface TerminologyIdParsingTerminologyCodeMixin {
    @JsonDeserialize(converter = TerminologyIdConverter.class)
    void setTerminologyId(String terminologyId);
}
