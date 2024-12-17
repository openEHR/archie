package com.nedap.archie.adl14;

import com.nedap.archie.adl14.log.ADL2ConversionLog;
import com.nedap.archie.aom.Archetype;
import org.openehr.utils.message.MessageCode;
import org.openehr.utils.message.MessageLogger;

/**
 * ADL 2 conversion result. Always has the archetypeId field set.
 * Either has archetype and conversionLog non-null in case of a successful conversion, or
 * exception non-null in case of an unexpected Exception
 */
public class ADL2ConversionResult {

    private String archetypeId;
    private Archetype archetype;
    private ADL2ConversionLog conversionLog;
    private MessageLogger log;
    private Exception exception;

    public ADL2ConversionResult() {
        /* Empty construction for Jackson parsing */
    }

    public ADL2ConversionResult(Archetype archetype) {
        this(archetype.getArchetypeId().getFullId(), null);
        this.archetype = archetype;
    }

    public ADL2ConversionResult(String archetypeId, Exception exception) {
        this.archetypeId = archetypeId;
        this.exception = exception;
        log = new MessageLogger();
    }

    /** GETTERS **/
    public String getArchetypeId() {
        return archetypeId;
    }

    public Archetype getArchetype() {
        return archetype;
    }

    public ADL2ConversionLog getConversionLog() {
        return conversionLog;
    }

    public MessageLogger getLog() {
        return log;
    }

    public Exception getException() {
        return exception;
    }

    /** SETTERS **/
    public void setArchetype(Archetype archetype) {
        this.archetype = archetype;
    }

    public void setConversionLog(ADL2ConversionLog conversionLog) {
        this.conversionLog = conversionLog;
    }
}
