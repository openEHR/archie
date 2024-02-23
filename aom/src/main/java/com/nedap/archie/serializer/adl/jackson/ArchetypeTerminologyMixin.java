package com.nedap.archie.serializer.adl.jackson;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ValueSet;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@JsonPropertyOrder({"term_definitions", "term_bindings", "terminology_extracts", "value_sets"})
public class ArchetypeTerminologyMixin {

    @JsonIgnore
    private Boolean differential;
    @JsonIgnore
    private String originalLanguage;
    @JsonIgnore
    private String conceptCode;

}
