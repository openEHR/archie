package com.nedap.archie.adlparser.treewalkers;

import com.nedap.archie.antlr.errors.ANTLRParserErrors;
import com.nedap.archie.adlparser.antlr.AdlParser.*;
import com.nedap.archie.serializer.odin.OdinObjectParser;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;

/**
 * Parser for the terminology section of an archetype
 *
 * Created by pieter.bos on 19/10/15.
 */
public class TerminologyParser extends BaseTreeWalker {

    public TerminologyParser(ANTLRParserErrors errors) {
        super(errors);
    }

    public ArchetypeTerminology parseTerminology(TerminologySectionContext context) {
        ArchetypeTerminology terminology = OdinObjectParser.convert(context.odin_text(), ArchetypeTerminology.class);
        return terminology;
    }

}
