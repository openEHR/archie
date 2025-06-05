package com.nedap.archie.adlparser;

import com.nedap.archie.adlparser.antlr.AdlLexer;
import com.nedap.archie.adlparser.antlr.AdlParser;
import com.nedap.archie.adlparser.modelconstraints.BMMConstraintImposer;
import com.nedap.archie.adlparser.modelconstraints.ModelConstraintImposer;
import com.nedap.archie.adlparser.modelconstraints.ReflectionConstraintImposer;
import com.nedap.archie.adlparser.treewalkers.ADLListener;
import com.nedap.archie.antlr.errors.ANTLRParserErrors;
import com.nedap.archie.antlr.errors.ArchieErrorListener;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.utils.ArchetypeParsePostProcessor;
import com.nedap.archie.rminfo.MetaModel;
import com.nedap.archie.rminfo.MetaModelProvider;
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
 * Parses ADL files to Archetype objects.
 *
 */
public class ADLParser {

    private final MetaModelProvider metaModelProvider;
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
        this.metaModelProvider = null;
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
        this.metaModelProvider = null;
    }


    /**
     * Creates an ADLParser with MetaModel knowledge. This is used to set the isSingle and isMultiple fields correctly
     * in the future, this will be used for more model-specific options, such as defined C_PRIMITIVE_OBJECTS and more
     * @param models
     * @deprecated Use {@link #ADLParser(MetaModelProvider)} instead.
     */
    @Deprecated
    public ADLParser(MetaModels models) {
        this((MetaModelProvider) models);
    }

    /**
     * Creates an ADLParser with MetaModel knowledge. This is used to set the isSingle and isMultiple fields correctly
     * in the future, this will be used for more model-specific options, such as defined C_PRIMITIVE_OBJECTS and more
     * @param metaModelProvider the provider for meta models
     */
    public ADLParser(MetaModelProvider metaModelProvider) {
        this.metaModelProvider = metaModelProvider;
        this.modelConstraintImposer = null;
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
            ADLListener listener = new ADLListener(errors, metaModelProvider);
            walker = new ParseTreeWalker();
            walker.walk(listener, tree);
            result = listener.getArchetype();
            //set some values that are not directly in ODIN or ADL
            ArchetypeParsePostProcessor.fixArchetype(result);

            if (modelConstraintImposer != null && result.getDefinition() != null) {
                modelConstraintImposer.imposeConstraints(result.getDefinition());
            } else if (metaModelProvider != null) {
                MetaModel metaModel = metaModelProvider.selectAndGetMetaModel(result);
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