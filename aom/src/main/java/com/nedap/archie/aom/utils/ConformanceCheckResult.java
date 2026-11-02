package com.nedap.archie.aom.utils;

import com.nedap.archie.archetypevalidator.ErrorType;

public class ConformanceCheckResult {
    private boolean conforms;
    private ErrorType errorType;
    private String error;

    public ConformanceCheckResult(boolean conforms, ErrorType errorType, String error) {
        this.conforms = conforms;
        this.errorType = errorType;
        this.error = error;
    }

    public static ConformanceCheckResult conforms() {
        return new ConformanceCheckResult(true, null, null);
    }

    public static ConformanceCheckResult fails() {
        return new ConformanceCheckResult(false, ErrorType.OTHER, "");
    }

    public static ConformanceCheckResult fails(String error) {
        return fails(ErrorType.OTHER, error);
    }

    public static ConformanceCheckResult fails(ErrorType errorType, String error) {
        return new ConformanceCheckResult(false, errorType, error);
    }

    public boolean doesConform() {
        return conforms;
    }

    public String getError() {
        return error;
    }

    public ErrorType getErrorType() {
        return errorType;
    }
}
