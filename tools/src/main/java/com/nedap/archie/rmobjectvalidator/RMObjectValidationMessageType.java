package com.nedap.archie.rmobjectvalidator;

/**
 * Created by peter.leppers on 09/05/17.
 */
public enum RMObjectValidationMessageType {
    DEFAULT, REQUIRED, WRONG_TYPE, CARDINALITY_MISMATCH, EMPTY_OBSERVATION, ARCHETYPE_SLOT_ID_MISMATCH, ARCHETYPE_NOT_FOUND, INVARIANT_ERROR, EXCEPTION;
}
