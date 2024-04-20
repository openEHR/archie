package com.nedap.archie.openehr.serialisation.odin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.openehr.rm.datavalues.TermMapping;

import java.util.List;

public interface OdinParsingDvTextMixin {

    @JsonDeserialize(converter=TermMappingMapToListConverter.class)
    void setMappings(List<TermMapping> mappings);
}
