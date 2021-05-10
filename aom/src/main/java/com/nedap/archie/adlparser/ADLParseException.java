package com.nedap.archie.adlparser;

import com.nedap.archie.antlr.errors.ANTLRParserErrors;
import com.nedap.archie.antlr.errors.ANTLRParserMessage;
import com.nedap.archie.aom.Archetype;

import java.text.ParseException;

public class ADLParseException extends Exception {

    private Archetype archetype;
    private ANTLRParserErrors errors;

    /**
     * Constructs a ParseException with the specified detail message and
     * offset.
     * A detail message is a String that describes this particular exception.
     *
     * @param errors      the parser errors that led to this Exception
     * @param archetype   the partially parsed Archetype
     */
    public ADLParseException(ANTLRParserErrors errors, Archetype archetype) {
        super(errors.toString());
        this.errors = errors;
        this.archetype = archetype;
    }


    /**
     * The parser does a best effort to even generate an arcehtype when there are parse errors.
     * This is not guaranteed to be complete - in fact it likely is not, but it can be used for modeling tools to at least
     * show part of the archetype in case of errors.
     * If an archetype could not be constructed, returns null.
     * @return the partially parsed archetype, or null if partial parsig failed
     */
    public Archetype getArchetype() {
        return archetype;
    }

    /**
     * Get the ANTLRParserErrors that occurred during parsing.
     * @return the ANTLRParserErrors that occurred during parsing
     */
    public ANTLRParserErrors getErrors() {
        return errors;
    }
}
