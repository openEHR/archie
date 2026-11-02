package com.nedap.archie.json;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class PatternConstraintCTemporalMixin {

    @JsonProperty("patterned_constraint")
    @JsonAlias("pattern_constraint")
    public abstract String getPatternConstraint();
}
