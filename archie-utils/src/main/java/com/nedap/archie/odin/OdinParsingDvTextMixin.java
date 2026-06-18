package com.nedap.archie.odin;

import com.nedap.archie.rm.datavalues.TermMapping;

import java.util.List;

public interface OdinParsingDvTextMixin {

    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(converter = TermMappingMapToListConverter.class)
    @tools.jackson.databind.annotation.JsonDeserialize(converter = com.nedap.archie.odin3.TermMappingMapToListConverter.class)
    void setMappings(List<TermMapping> mappings);
}
