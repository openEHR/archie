package org.openehr.adl;

import com.nedap.archie.adlparser.treewalkers.CComplexObjectParser;
import com.nedap.archie.adlparser.antlr.AdlLexer;
import com.nedap.archie.adlparser.antlr.AdlParser;
import com.nedap.archie.adlparser.treewalkers.RulesParser;
import com.nedap.archie.antlr.errors.ANTLRParserErrors;
import com.nedap.archie.antlr.errors.ArchieErrorListener;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.RulesSection;
import com.nedap.archie.rminfo.MetaModels;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class CComplexObjectParserV2 {
    private final MetaModels metaModels;

    public CComplexObjectParserV2(MetaModels metaModels) {
        this.metaModels = metaModels;
    }

    public CComplexObject parseComplexObject(String text, ArchieErrorListener listener) {

        AdlLexer lexer = new AdlLexer(CharStreams.fromString(text));
        lexer.addErrorListener(listener);
        AdlParser parser = new AdlParser(new CommonTokenStream(lexer));
        parser.addErrorListener(listener);

        ANTLRParserErrors errors = new ANTLRParserErrors();
        CComplexObject cComplexObject = new CComplexObjectParser(errors, metaModels).parseComplexObject(parser.c_complex_object());
        listener.getErrors().addAll(errors, listener.getStartingLineNumber());
        return cComplexObject;
    }

    public RulesSection parseRules(String text, ArchieErrorListener listener) {
        AdlLexer lexer = new AdlLexer(CharStreams.fromString(text));
        lexer.addErrorListener(listener);
        AdlParser parser = new AdlParser(new CommonTokenStream(lexer));
        parser.addErrorListener(listener);
        RulesSection result = new RulesSection();

        result.setContent(text);
        ANTLRParserErrors errors = new ANTLRParserErrors();
        RulesParser rulesParser = new RulesParser(errors);
        for(AdlParser.AssertionContext assertion:parser.assertion_list().assertion()) {
            result.addRule(rulesParser.parse(assertion));
        }

        listener.getErrors().addAll(errors, listener.getStartingLineNumber());
        return result;
    }
}
