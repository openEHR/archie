package com.nedap.archie.adlparser.treewalkers;

import com.nedap.archie.antlr.errors.ANTLRParserErrors;

/**
 * Created by pieter.bos on 29/10/15.
 */
public class BaseTreeWalker {

    private ANTLRParserErrors errors;

    public BaseTreeWalker(ANTLRParserErrors errors) {
        this.errors = errors;
    }

    public void addError(String error) {
        errors.addError(error);
    }

    public void addWarning(String warning) {
        errors.addError(warning);
    }

    public ANTLRParserErrors getErrors() {
        return errors;
    }
}
