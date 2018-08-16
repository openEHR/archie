package com.nedap.archie.rmobjectvalidator;

/**
 * Exception to indicate RM Object validation has failed
 * <p>
 * Created by pieter.bos on 01/09/15.
 */
public class RMObjectValidationException extends Exception {

    private String path;
    private String humanPath;

    public RMObjectValidationException(String path, String humanPath, String message) {
        super(message);
        this.path = path;
        this.humanPath = humanPath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getHumanPath() {
        return humanPath;
    }

    public void setHumanPath(String humanPath) {
        this.humanPath = humanPath;
    }

    public RMObjectValidationMessage getValidationMessage() {
        return new RMObjectValidationMessage(this);

    }
}
