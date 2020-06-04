package com.nedap.archie.antlr.errors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pieter.bos on 19/10/15.
 */
public class ANTLRParserErrors {

    private static final Logger logger = LoggerFactory.getLogger(ANTLRParserErrors.class);

    private List<ANTLRParserMessage> errors = new ArrayList<>();
    private List<ANTLRParserMessage> warnings = new ArrayList<>();

    public void addError(String error) {
        errors.add(new ANTLRParserMessage(error));
    }

    public void addError(String error, String shortMessage, int line, int charPositionInLine, int length, String offendingSymbol) {
        errors.add(new ANTLRParserMessage(error, shortMessage, line, charPositionInLine, length, offendingSymbol));
    }

    public void addWarning(String error) {
        warnings.add(new ANTLRParserMessage(error));
    }

    public void addWarning(String error, String shortMessage, int line, int charPositionInLine, int length, String offendingSymbol) {
        warnings.add(new ANTLRParserMessage(error, shortMessage, line, charPositionInLine, length, offendingSymbol));
    }

    public void logToLogger() {
        for(ANTLRParserMessage message:warnings) {
            logger.warn(message.getMessage());
        }
        for(ANTLRParserMessage message:errors) {
            logger.error(message.getMessage());
        }
    }

    public List<ANTLRParserMessage> getErrors() {
        return errors;
    }

    public void setErrors(List<ANTLRParserMessage> errors) {
        this.errors = errors;
    }

    public List<ANTLRParserMessage> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<ANTLRParserMessage> warnings) {
        this.warnings = warnings;
    }

    public boolean hasNoMessages() {
        return this.getErrors().isEmpty() && this.getWarnings().isEmpty();
    }

    public boolean hasNoErrors() {
        return this.getErrors().isEmpty();
    }

    public boolean hasErrors() {
        return !getErrors().isEmpty();
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        append(result, "Warning", warnings);
        append(result, "Error", errors);
        return result.toString();
    }

    private void append(StringBuilder result, String level, List<ANTLRParserMessage> messages) {
        for(ANTLRParserMessage message:messages) {
            result.append(level);
            result.append(": ");
            result.append(message.getMessage());
            result.append("\n");
        }
    }
}
