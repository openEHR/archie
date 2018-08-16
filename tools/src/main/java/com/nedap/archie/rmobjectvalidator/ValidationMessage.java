package com.nedap.archie.rmobjectvalidator;

import com.nedap.archie.aom.ArchetypeConstraint;

/**
 * Created by pieter.bos on 02/09/15.
 */
public class ValidationMessage {

    private String archetypePath;
    private String path;
    private String humanReadableArchetypePath;
    private String message;

    private ValidationMessageType type;


    // Constructors with default message validation type
    public ValidationMessage(ArchetypeConstraint constraint, IDTreeElement object, String message) {
        this(constraint, object, message, ValidationMessageType.DEFAULT);
    }

    public ValidationMessage(ArchetypeConstraint constraint, AttributeTreeElement object, String message) {
        this(constraint, object, message, ValidationMessageType.DEFAULT);
    }

    public ValidationMessage(ArchetypeConstraint constraint, String actualPath, String message) {
        this(constraint, actualPath, message, ValidationMessageType.DEFAULT);
    }


    // Constructors with custom message validation type
    public ValidationMessage(ArchetypeConstraint constraint, IDTreeElement object, String message, ValidationMessageType type) {
        this(object.reconstructPath(), constraint.getPath(), constraint.getLogicalPath(), message, type);
    }

    public ValidationMessage(ArchetypeConstraint constraint, String actualPath, String message, ValidationMessageType type) {
        this(actualPath, constraint.getPath(), constraint.getLogicalPath(), message, type);
    }

    public ValidationMessage(ArchetypeConstraint constraint, AttributeTreeElement object, String message, ValidationMessageType type) {
        this(object.reconstructPath(), constraint.getPath(), constraint.getLogicalPath(), message, type);
    }


    // Constructors with attribute assignment
    public ValidationMessage(String path, String archetypePath, String humanPath, String message, ValidationMessageType type) {
        this.path = path;
        this.archetypePath = archetypePath;
        this.humanReadableArchetypePath = humanPath;
        this.message = message;
        this.type = type;
    }

    public ValidationMessage(RMObjectValidationException e) {
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

    public ValidationMessageType getType() {
        return type;
    }

    public void setType(ValidationMessageType type) {
        this.type = type;
    }

    public String getHumanReadableArchetypePath() {
        return humanReadableArchetypePath;
    }

    public String getMessage() {
        return message;
    }

    public String toString() {
        return "Message at " + humanReadableArchetypePath + " (" + path + "): " + message;
    }

}
