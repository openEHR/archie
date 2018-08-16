package com.nedap.archie.archetypevalidator;

import com.google.common.base.Strings;

/**
 * Created by pieter.bos on 31/03/2017.
 */
public class ArchetypeValidationMessage {
    private ErrorType type;
    private String pathInArchetype;
    private String message;
    private boolean warning;//TODO: migrate to severity enum once we merge them

    public ArchetypeValidationMessage(ErrorType type) {
        this.type = type;
    }

    public ArchetypeValidationMessage(ErrorType type, String pathInArchetype) {
        this.type = type;
        this.pathInArchetype = pathInArchetype;
    }

    public ArchetypeValidationMessage(ErrorType type, String pathInArchetype, String message) {
        this.type = type;
        this.pathInArchetype = pathInArchetype;
        this.message = message;
    }

    public ErrorType getType() {
        return type;
    }

    public void setType(ErrorType type) {
        this.type = type;
    }

    public String getPathInArchetype() {
        return pathInArchetype;
    }

    public void setPathInArchetype(String pathInArchetype) {
        this.pathInArchetype = pathInArchetype;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toString() {
        String result = type.toString() + " at " + pathInArchetype;
        if(!Strings.isNullOrEmpty(message)) {
            result += ": " + message;
        }
        return result;
    }

    public boolean isWarning() {
        return warning;
    }

    public void setWarning(boolean warning) {
        this.warning = warning;
    }
}
