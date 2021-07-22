package com.nedap.archie.template.betterjson.parser;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nedap.archie.aom.ArchetypeHRID;

@JsonIgnoreProperties({"rmName", "templateId"})
public interface ArchetypeMixin {

    @JsonDeserialize(converter = ArchetypeIdConverter.class)
    public void setArchetypeId(ArchetypeHRID archetypeId);
    @JsonDeserialize(converter = ArchetypeIdConverter.class)
    ArchetypeHRID setParentArchetypeId(ArchetypeHRID archetypeId);
}
