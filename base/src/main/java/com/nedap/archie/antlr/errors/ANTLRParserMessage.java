package com.nedap.archie.antlr.errors;

/**
 * An error, info or warning message from the archetype parsing
 *
 * Created by pieter.bos on 19/10/15.
 */
public class ANTLRParserMessage {

    private Integer lineNumber;
    private Integer columnNumber;
    private String message;
    private String shortMessage;
    private Integer length;
    private String offendingSymbol;

    public ANTLRParserMessage(String message) {
        this.message = message;
    }

    public ANTLRParserMessage(String message, String shortMessage, Integer lineNumber, Integer columnNumber) {
        this.message = message;
        this.shortMessage = shortMessage;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    public ANTLRParserMessage(String message, String shortMessage, Integer lineNumber, Integer columnNumber, Integer length, String offendingSymbol) {
        this.message = message;
        this.shortMessage = shortMessage;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.length = length;
        this.offendingSymbol = offendingSymbol;
    }


    public String getMessage() {
        return message;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public String getShortMessage() {
        return shortMessage;
    }

    public Integer getLength() {
        return length;
    }

    public String getOffendingSymbol() {
        return offendingSymbol;
    }
}
