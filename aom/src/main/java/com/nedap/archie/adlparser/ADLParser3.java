package com.nedap.archie.adlparser;

import com.nedap.archie.adlparser.antlr.AdlLexer;
import com.nedap.archie.adlparser.antlr.AdlParser;
import com.nedap.archie.adlparser.modelconstraints.BMMConstraintImposer;
import com.nedap.archie.adlparser.modelconstraints.ModelConstraintImposer;
import com.nedap.archie.adlparser.modelconstraints.ReflectionConstraintImposer;
import com.nedap.archie.adlparser.treewalkers.ADLListener3;
import com.nedap.archie.antlr.errors.ANTLRParserErrors;
import com.nedap.archie.antlr.errors.ArchieErrorListener;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.utils.ArchetypeParsePostProcessor;
import com.nedap.archie.rminfo.MetaModel3;
import com.nedap.archie.rminfo.MetaModelProvider3;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.io.input.BOMInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;


/**
 * Parses ADL files to Archetype objects.
 *
 */
public class ADLParser3 {

    private final MetaModelProvider3 metaModelProvider;
    private ANTLRParserErrors errors;

    private Lexer lexer;
    private AdlParser parser;
    private ADLListener3 listener;
    private ParseTreeWalker walker;
    private AdlParser.AdlContext tree;
    public ArchieErrorListener errorListener;

    /**
     * If true, write errors to the console, if false, do not
     */
    private boolean logEnabled = true;

    public ADLParser3() {
        this(null);
    }

    public ADLParser3(MetaModelProvider3 metaModelProvider) {
        this.metaModelProvider = metaModelProvider;
    }

    public Archetype parse(String adl) throws ADLParseException {
        return parse(CharStreams.fromString(adl));
    }

    public Archetype parse(InputStream stream) throws ADLParseException, IOException {
        return parse(CharStreams.fromStream(new BOMInputStream(stream), Charset.availableCharsets().get("UTF-8")));
    }

    public Archetype parse(CharStream stream) throws ADLParseException {

        errors = new ANTLRParserErrors();
        errorListener = new ArchieErrorListener(errors);
        errorListener.setLogEnabled(logEnabled);
        Archetype result = null;

        lexer = new AdlLexer(stream);
        lexer.addErrorListener(errorListener);
        parser = new AdlParser(new CommonTokenStream(lexer));
        parser.addErrorListener(errorListener);
        tree = parser.adl(); // parse

        try {
            ADLListener3 listener = new ADLListener3(errors, metaModelProvider);
            walker = new ParseTreeWalker();
            walker.walk(listener, tree);
            result = listener.getArchetype();
            //set some values that are not directly in ODIN or ADL
            ArchetypeParsePostProcessor.fixArchetype(result);

            if (metaModelProvider != null) {
                MetaModel3 metaModel = metaModelProvider.getMetaModel(result);
                if (metaModel.getBmmModel() != null) {
                    ModelConstraintImposer imposer = new BMMConstraintImposer(metaModel.getBmmModel() );
                    imposer.setSingleOrMultiple(result.getDefinition());
                } else if (metaModel.getModelInfoLookup() != null) {
                    ModelConstraintImposer imposer = new ReflectionConstraintImposer(metaModel.getModelInfoLookup());
                    imposer.setSingleOrMultiple(result.getDefinition());
                }
            }
            return result;
        } finally {
            if (errors.hasErrors()) {
                throw new ADLParseException(errors, result);
            }
        }


    }

    public ANTLRParserErrors getErrors() {
        return errors;
    }

    public Lexer getLexer() {
        return lexer;
    }

    public void setLexer(Lexer lexer) {
        this.lexer = lexer;
    }

    public AdlParser getParser() {
        return parser;
    }

    public void setParser(AdlParser parser) {
        this.parser = parser;
    }

    public ADLListener3 getListener() {
        return listener;
    }

    public void setListener(ADLListener3 listener) {
        this.listener = listener;
    }

    public ParseTreeWalker getWalker() {
        return walker;
    }

    public void setWalker(ParseTreeWalker walker) {
        this.walker = walker;
    }

    public AdlParser.AdlContext getTree() {
        return tree;
    }

    public void setTree(AdlParser.AdlContext tree) {
        this.tree = tree;
    }

    public boolean isLogEnabled() {
        return logEnabled;
    }

    public void setLogEnabled(boolean logEnabled) {
        this.logEnabled = logEnabled;
    }
}