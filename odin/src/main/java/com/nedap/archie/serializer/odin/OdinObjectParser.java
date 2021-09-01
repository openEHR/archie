package com.nedap.archie.serializer.odin;

import com.fasterxml.jackson.databind.JavaType;
import com.nedap.archie.adlparser.antlr.AdlLexer;
import com.nedap.archie.adlparser.antlr.AdlParser;
import com.nedap.archie.antlr.errors.ANTLRParserErrors;
import com.nedap.archie.antlr.errors.ArchieErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by pieter.bos on 02/11/15.
 */
public class OdinObjectParser {

    public static <T> T convert(AdlParser.Odin_textContext odin, Class<T> clazz) {
        try {
            return AdlOdinToJsonConverter.getObjectMapper().readValue(new AdlOdinToJsonConverter().convert(odin), clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T convert(AdlParser.Odin_textContext odin, JavaType clazz) {
        try {
            return AdlOdinToJsonConverter.getObjectMapper().readValue(new AdlOdinToJsonConverter().convert(odin), clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T convert(InputStream odin, Class<T> clazz) throws IOException {
        AdlLexer adlLexer = new AdlLexer(CharStreams.fromStream(odin));
        AdlParser parser = new AdlParser(new CommonTokenStream(adlLexer));
        return convert(parser.odin_text(), clazz);
    }

    public static <T> T convert(String odin, Class<T> clazz) {
        AdlLexer adlLexer = new AdlLexer(CharStreams.fromString(odin));
        AdlParser parser = new AdlParser(new CommonTokenStream(adlLexer));
        return convert(parser.odin_text(), clazz);
    }

    public static <T> T convert(String odin, Class<T> clazz, ArchieErrorListener errors) {
        AdlLexer adlLexer = new AdlLexer(CharStreams.fromString(odin));
        adlLexer.addErrorListener(errors);
        AdlParser parser = new AdlParser(new CommonTokenStream(adlLexer));
        parser.addErrorListener(errors);
        return convert(parser.odin_text(), clazz);
    }

    public static <T> T convert(String odin, JavaType clazz, ArchieErrorListener errors) {
        AdlLexer adlLexer = new AdlLexer(CharStreams.fromString(odin));
        adlLexer.addErrorListener(errors);
        AdlParser parser = new AdlParser(new CommonTokenStream(adlLexer));
        parser.addErrorListener(errors);
        return convert(parser.odin_text(), clazz);
    }

    public static <T> T convert(String odin, JavaType clazz) {
        AdlLexer adlLexer = new AdlLexer(CharStreams.fromString(odin));
        AdlParser parser = new AdlParser(new CommonTokenStream(adlLexer));
        return convert(parser.odin_text(), clazz);
    }
}
