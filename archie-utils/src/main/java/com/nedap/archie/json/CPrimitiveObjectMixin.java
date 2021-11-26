package com.nedap.archie.json;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class CPrimitiveObjectMixin {

    @JsonProperty("is_enumerated_type_constraint")
    @JsonAlias("enumerated_type_constraint")
    public abstract Boolean getEnumeratedTypeConstraint();
}
