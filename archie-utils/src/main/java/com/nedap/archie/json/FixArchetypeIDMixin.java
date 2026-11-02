package com.nedap.archie.json;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class FixArchetypeIDMixin {

    @JsonIgnore
    public abstract String getFullId();

    @JsonIgnore
    public abstract String getSemanticId();

    @JsonIgnore
    public abstract String getNamespace();

    @JsonIgnore
    public abstract String getQualifiedRmEntity();

    @JsonIgnore
    public abstract String getDomainConcept();

    @JsonIgnore
    public abstract String getRmOriginator();

    @JsonIgnore
    public abstract String getRmName();

    @JsonIgnore
    public abstract String getRmEntity();

    @JsonIgnore
    public abstract String getSpecialisation();

    @JsonIgnore
    public abstract String getVersionId();

}
