package com.nedap.archie.aom.utils;

public class ConformanceCheckResult {
    private boolean conforms;
    private String error;

    public ConformanceCheckResult(boolean conforms, String error) {
        this.conforms = conforms;
        this.error = error;
    }

    public static ConformanceCheckResult conforms() {
        return new ConformanceCheckResult(true, null);
    }

    public static ConformanceCheckResult fails() {
        return new ConformanceCheckResult(false, "");
    }

    public static ConformanceCheckResult fails(String error) {
        return new ConformanceCheckResult(false, error);
    }

    public boolean doesConform() {
        return conforms;
    }

    public void setConforms(boolean conforms) {
        this.conforms = conforms;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
