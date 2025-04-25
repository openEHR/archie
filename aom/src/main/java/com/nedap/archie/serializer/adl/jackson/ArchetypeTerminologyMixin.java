package com.nedap.archie.serializer.adl.jackson;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"term_definitions", "term_bindings", "terminology_extracts", "value_sets"})
public class ArchetypeTerminologyMixin {

    @JsonIgnore
    private Boolean differential;
    @JsonIgnore
    private String originalLanguage;
    @JsonIgnore
    private String conceptCode;

}
