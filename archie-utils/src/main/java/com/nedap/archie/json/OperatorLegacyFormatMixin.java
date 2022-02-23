package com.nedap.archie.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nedap.archie.rules.Expression;
import com.nedap.archie.rules.OperatorKind;

public interface OperatorLegacyFormatMixin {
    @JsonIgnore(false)
    Expression getLeftOperand();
    @JsonIgnore(false)
    Expression getRightOperand();
    @JsonIgnore(false)
    OperatorKind getOperator();

}
