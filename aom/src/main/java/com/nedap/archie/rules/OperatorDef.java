package com.nedap.archie.rules;

/**
 * Operator Def used to conform to the newer expression language specification. Not used except for json (de)serialization
 * use OperatorKind intead!
 */
public class OperatorDef {
    private String identifier;

    public OperatorDef() {

    }

    public OperatorDef(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
