package com.nedap.archie.rmobjectvalidator;

import org.openehr.utils.message.I18n;
import org.openehr.utils.message.MessageCode;

public enum RMObjectValidationMessageIds implements MessageCode {
    rm_VALIDATION_MESSAGE_TO_STRING(I18n.register("Message at {0} ({1}):  {2}")),
    rm_INCORRECT_TYPE(I18n.register("Object should be type {0}, but was {1}")),
    rm_TUPLE_CONSTRAINT(I18n.register("Multiple values for Tuple constraint {0}: {1}")),
    rm_TUPLE_MISMATCH(I18n.register("Object does not match tuple: {0}")),
    rm_PRIMITIVE_CONSTRAINT(I18n.register("Multiple values for Primitive Object constraint {0}: {1}")),
    rm_INVALID_FOR_CONSTRAINT(I18n.register("Not a valid value for constraint {0}: {1}")),
    rm_CARDINALITY_MISMATCH(I18n.register("Attribute does not match cardinality {0}")),
    rm_EXISTENCE_MISMATCH(I18n.register("Attribute {0} of class {1} does not match existence {2}")),
    rm_OCCURRENCE_MISMATCH(I18n.register("Attribute has {0} occurrences, but must be {1}"));

    private final String messageTemplate;

    RMObjectValidationMessageIds(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    @Override
    public String getCode() {
        String code = name();
        if (code.startsWith("rm_")) {
            return code.substring(3);
        }
        return code;
    }

    @Override
    public String getMessageTemplate() {
        return messageTemplate;
    }
}
