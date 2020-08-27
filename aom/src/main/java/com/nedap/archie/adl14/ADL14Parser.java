package com.nedap.archie.adl14;

import com.nedap.archie.adl14.treewalkers.ADL14Listener;
import com.nedap.archie.adlparser.antlr.Adl14Lexer;
import com.nedap.archie.adlparser.antlr.Adl14Parser;
import com.nedap.archie.adlparser.modelconstraints.BMMConstraintImposer;
import com.nedap.archie.adlparser.modelconstraints.ModelConstraintImposer;
import com.nedap.archie.adlparser.modelconstraints.ReflectionConstraintImposer;
import com.nedap.archie.antlr.errors.ANTLRParserErrors;
import com.nedap.archie.antlr.errors.ArchieErrorListener;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.rminfo.MetaModels;
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
 * Parses ADL 1.4 files to Archetype objects. Does not convert to ADL 2 yet!
 */
public class ADL14Parser {

    private final MetaModels metaModels;
    private ANTLRParserErrors errors;

    private Lexer lexer;
    private Adl14Parser parser;
    private ADL14Listener listener;
    private ParseTreeWalker walker;
    private Adl14Parser.AdlContext tree;
    public ArchieErrorListener errorListener;

    /**
     * If true, write errors to the console, if false, do not
     */
    private boolean logEnabled = true;

    public ADL14Parser(MetaModels models) {
        this.metaModels = models;
    }

    public Archetype parse(String adl, ADL14ConversionConfiguration conversionConfiguration) {
        return parse(CharStreams.fromString(adl), conversionConfiguration);
    }

    public Archetype parse(InputStream stream, ADL14ConversionConfiguration conversionConfiguration) throws IOException {
        return parse(CharStreams.fromStream(new BOMInputStream(stream), Charset.availableCharsets().get("UTF-8")), conversionConfiguration);
    }

    public Archetype parse(CharStream stream, ADL14ConversionConfiguration conversionConfiguration) {

        errors = new ANTLRParserErrors();
        errorListener = new ArchieErrorListener(errors);
        errorListener.setLogEnabled(logEnabled);

        lexer = new Adl14Lexer(stream);
        lexer.addErrorListener(errorListener);
        parser = new Adl14Parser(new CommonTokenStream(lexer));
        parser.addErrorListener(errorListener);
        tree = parser.adl(); // parse

        ADL14Listener listener = new ADL14Listener(errors, conversionConfiguration);
        walker= new ParseTreeWalker();
        walker.walk(listener, tree);
        Archetype result = listener.getArchetype();
        if (metaModels != null) {
            metaModels.selectModel(result);
            if(metaModels.getSelectedBmmModel() != null) {
                ModelConstraintImposer imposer = new BMMConstraintImposer(metaModels.getSelectedBmmModel());
                imposer.setSingleOrMultiple(result.getDefinition());
            } else if (metaModels.getSelectedModelInfoLookup() != null) {
                ModelConstraintImposer imposer = new ReflectionConstraintImposer(metaModels.getSelectedModelInfoLookup());
                imposer.setSingleOrMultiple(result.getDefinition());
            }
        }
        return result;

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

    public Adl14Parser getParser() {
        return parser;
    }

    public void setParser(Adl14Parser parser) {
        this.parser = parser;
    }

    public ADL14Listener getListener() {
        return listener;
    }

    public void setListener(ADL14Listener listener) {
        this.listener = listener;
    }

    public ParseTreeWalker getWalker() {
        return walker;
    }

    public void setWalker(ParseTreeWalker walker) {
        this.walker = walker;
    }

    public Adl14Parser.AdlContext getTree() {
        return tree;
    }

    public void setTree(Adl14Parser.AdlContext tree) {
        this.tree = tree;
    }

    public boolean isLogEnabled() {
        return logEnabled;
    }

    public void setLogEnabled(boolean logEnabled) {
        this.logEnabled = logEnabled;
    }
}