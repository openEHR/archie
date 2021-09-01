package org.openehr.adl;

import com.nedap.archie.adlparser.treewalkers.CComplexObjectParser;
import com.nedap.archie.adlparser.antlr.AdlLexer;
import com.nedap.archie.adlparser.antlr.AdlParser;
import com.nedap.archie.adlparser.treewalkers.RulesParser;
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
        return new CComplexObjectParser(listener.getErrors(), metaModels).parseComplexObject(parser.c_complex_object());
    }

    public RulesSection parseRules(String text, ArchieErrorListener listener) {
        AdlLexer lexer = new AdlLexer(CharStreams.fromString(text));
        lexer.addErrorListener(listener);
        AdlParser parser = new AdlParser(new CommonTokenStream(lexer));
        parser.addErrorListener(listener);
        RulesSection result = new RulesSection();

        result.setContent(text);
        RulesParser rulesParser = new RulesParser(listener.getErrors());
        for(AdlParser.AssertionContext assertion:parser.assertion_list().assertion()) {
            result.addRule(rulesParser.parse(assertion));
        }

        return result;
    }
}
