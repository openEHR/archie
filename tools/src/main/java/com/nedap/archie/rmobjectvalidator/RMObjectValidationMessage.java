package com.nedap.archie.rmobjectvalidator;

import com.nedap.archie.aom.ArchetypeConstraint;

/**
 * Created by pieter.bos on 02/09/15.
 */
public class RMObjectValidationMessage {

    private String archetypePath;
    private String path;
    private String humanReadableArchetypePath;
    private String message;

    private RMObjectValidationMessageType type;


    public RMObjectValidationMessage(ArchetypeConstraint constraint, String actualPath, String message) {
        this(constraint, actualPath, message, RMObjectValidationMessageType.DEFAULT);
    }

    public RMObjectValidationMessage(ArchetypeConstraint constraint, String actualPath, String message, RMObjectValidationMessageType type) {
        this(actualPath, constraint == null ? null : constraint.getPath(), constraint == null ? null : constraint.getLogicalPath(), message, type);
    }


    // Constructors with attribute assignment
    public RMObjectValidationMessage(String path, String archetypePath, String humanPath, String message, RMObjectValidationMessageType type) {
        this.path = path;
        this.archetypePath = archetypePath;
        this.humanReadableArchetypePath = humanPath;
        this.message = message;
        this.type = type;
    }

    public RMObjectValidationMessage(RMObjectValidationException e) {
        this.path = e.getPath();
        this.humanReadableArchetypePath = e.getHumanPath();
        this.message = e.getMessage();
    }

    public String getArchetypePath() {
        return archetypePath;
    }

    public String getPath() {
        return path;
    }

    public RMObjectValidationMessageType getType() {
        return type;
    }

    public void setType(RMObjectValidationMessageType type) {
        this.type = type;
    }

    public String getHumanReadableArchetypePath() {
        return humanReadableArchetypePath;
    }

    public String getMessage() {
        return message;
    }

    public String toString() {
        return RMObjectValidationMessageIds.rm_VALIDATION_MESSAGE_TO_STRING.getMessage(humanReadableArchetypePath == null ? path : humanReadableArchetypePath, path, message);
    }
}
