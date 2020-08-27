package com.nedap.archie.archetypevalidator;

import com.google.common.base.Strings;
import org.openehr.utils.message.I18n;

/**
 * Created by pieter.bos on 31/03/2017.
 */
public class ValidationMessage {
    private ErrorType type;
    private String pathInArchetype;
    private String message;
    private boolean warning;//TODO: migrate to severity enum once we merge them

    public ValidationMessage() {}

    public ValidationMessage(ErrorType type) {
        this.type = type;
    }

    public ValidationMessage(ErrorType type, String pathInArchetype) {
        this.type = type;
        this.pathInArchetype = pathInArchetype;
    }

    public ValidationMessage(ErrorType type, String pathInArchetype, String message) {
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
        StringBuilder result = new StringBuilder();
        if(warning) {
            result.append(I18n.t("Warning"));
        } else {
            result.append(I18n.t("Error"));
        }
        boolean outputNewLine = false;
        if(!Strings.isNullOrEmpty(message)) {
            result.append(": ");
            result.append(message);
            result.append("\n");
            outputNewLine = true;
        }
        if(!Strings.isNullOrEmpty(pathInArchetype)) {

            if(!outputNewLine) {
                result.append(" ");
            }
            result.append(I18n.t("at path: "));
            result.append(pathInArchetype);
            result.append("\n");
            outputNewLine = true;
        }
        if(!outputNewLine) {
            result.append(" ");
        }
        result.append(type.getCode());
        result.append(": ");
        result.append(type.getMessage());

        return result.toString();
    }

    public boolean isWarning() {
        return warning;
    }

    public void setWarning(boolean warning) {
        this.warning = warning;
    }
}
