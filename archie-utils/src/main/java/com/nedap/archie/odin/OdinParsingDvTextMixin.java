package com.nedap.archie.odin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nedap.archie.rm.datavalues.TermMapping;

import java.util.List;

public interface OdinParsingDvTextMixin {

    @JsonDeserialize(converter=TermMappingMapToListConverter.class)
    void setMappings(List<TermMapping> mappings);
}
