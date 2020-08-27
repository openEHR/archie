package com.nedap.archie.adlparser;

import com.nedap.archie.adlparser.antlr.*;
import com.nedap.archie.adlparser.modelconstraints.BMMConstraintImposer;
import com.nedap.archie.adlparser.modelconstraints.ModelConstraintImposer;
import com.nedap.archie.adlparser.modelconstraints.ReflectionConstraintImposer;
import com.nedap.archie.adlparser.treewalkers.ADLListener;
import com.nedap.archie.antlr.errors.ArchieErrorListener;
import com.nedap.archie.antlr.errors.ANTLRParserErrors;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.utils.ArchetypeParsePostProcesser;
import com.nedap.archie.rminfo.MetaModels;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.io.input.BOMInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;


/**
 * Parses ADL files to Archetype objects.
 *
 */
public class ADLParser {

    private final MetaModels metaModels;
    private final ModelConstraintImposer modelConstraintImposer;
    private ANTLRParserErrors errors;

    private Lexer lexer;
    private AdlParser parser;
    private ADLListener listener;
    private ParseTreeWalker walker;
    private AdlParser.AdlContext tree;
    public ArchieErrorListener errorListener;

    /**
     * If true, write errors to the console, if false, do not
     */
    private boolean logEnabled = true;

    public ADLParser() {
        this.metaModels = null;
        this.modelConstraintImposer = null;
    }

    /**
     * The ModelConstraintImposer is a bit of a relic from the beginning of Archie
     * It's still very useful to set single/multiple, and in some tools, but not
     * necesarilly here. So, deprecated, if you want it it's available to do yourself
     * @param modelConstraintImposer
     */
    @Deprecated
    public ADLParser(ModelConstraintImposer modelConstraintImposer) {
        this.modelConstraintImposer = modelConstraintImposer;
        this.metaModels = null;
    }


    /**
     * Creates an ADLParser with MetaModel knowledge. This is used to set the isSingle and isMultiple fields correctly
     * in the future, this will be used for more model-specific options, such as defined C_PRIMITIVE_OBJECTS and more
     * @param models
     */
    public ADLParser(MetaModels models) {
        this.metaModels = models;
        this.modelConstraintImposer = null;
    }

    public Archetype parse(String adl) {
        return parse(CharStreams.fromString(adl));
    }

    public Archetype parse(InputStream stream) throws IOException {
        return parse(CharStreams.fromStream(new BOMInputStream(stream), Charset.availableCharsets().get("UTF-8")));
    }

    public Archetype parse(CharStream stream) {

        errors = new ANTLRParserErrors();
        errorListener = new ArchieErrorListener(errors);
        errorListener.setLogEnabled(logEnabled);

        lexer = new AdlLexer(stream);
        lexer.addErrorListener(errorListener);
        parser = new AdlParser(new CommonTokenStream(lexer));
        parser.addErrorListener(errorListener);
        tree = parser.adl(); // parse

        ADLListener listener = new ADLListener(errors, metaModels);
        walker= new ParseTreeWalker();
        walker.walk(listener, tree);
        Archetype result = listener.getArchetype();
        //set some values that are not directly in ODIN or ADL
        ArchetypeParsePostProcesser.fixArchetype(result);

        if(modelConstraintImposer != null && result.getDefinition() != null) {
            modelConstraintImposer.imposeConstraints(result.getDefinition());
        } else if (metaModels != null) {
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

    public AdlParser getParser() {
        return parser;
    }

    public void setParser(AdlParser parser) {
        this.parser = parser;
    }

    public ADLListener getListener() {
        return listener;
    }

    public void setListener(ADLListener listener) {
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