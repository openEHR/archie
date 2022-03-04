package com.nedap.archie.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nedap.archie.rules.OperatorKind;

public interface OperatorLegacyFormatMixin {
    @JsonIgnore(false)
    OperatorKind getOperator();
    @JsonIgnore(false)
    boolean isUnary();
}
