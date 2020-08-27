package org.openehr.bmm.v2.persistence.odin;

import com.nedap.archie.adlparser.antlr.odinLexer;
import com.nedap.archie.adlparser.antlr.odinParser;
import com.nedap.archie.antlr.errors.ANTLRParserErrors;
import com.nedap.archie.antlr.errors.ArchieErrorListener;
import com.nedap.archie.serializer.odin.OdinToJsonConverter;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.openehr.bmm.v2.persistence.PBmmSchema;
import org.openehr.bmm.v2.persistence.jackson.BmmJacksonUtil;

import java.io.IOException;
import java.io.InputStream;

public class BmmOdinParser {

    public static PBmmSchema convert(odinParser.Odin_textContext odin) {
        try {
            String json = new OdinToJsonConverter().convert(odin);
            return BmmJacksonUtil.getObjectMapper().readValue(json, PBmmSchema.class);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static PBmmSchema convert(InputStream odin) throws IOException {

        odinLexer lexer = new odinLexer(CharStreams.fromStream(odin));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        odinParser parser = new odinParser(tokens);
        ANTLRParserErrors errors = new ANTLRParserErrors();
        ArchieErrorListener listener = new ArchieErrorListener(errors);
        parser.addErrorListener(listener);

        try {
            PBmmSchema converted = convert(parser.odin_text());
            if (errors.hasErrors()) { //if parse errors, throw those first!
                throw new RuntimeException("errors parsing ODIN file: " + errors);
            }
            return converted;
        }
        catch (RuntimeException e) {
            if (errors.hasErrors()) { //if parse errors, throw those first!
                throw new RuntimeException("errors parsing ODIN file: " + errors);
            }
            throw e;
        }
    }

    public static PBmmSchema convert(String odin) {
        odinLexer lexer = new odinLexer(CharStreams.fromString(odin));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        odinParser parser = new odinParser(tokens);
        ANTLRParserErrors errors = new ANTLRParserErrors();
        ArchieErrorListener listener = new ArchieErrorListener(errors);
        parser.addErrorListener(listener);
        try {
            PBmmSchema converted = convert(parser.odin_text());
            if (errors.hasErrors()) { //if parse errors, throw those first!
                throw new RuntimeException("errors parsing ODIN file: " + errors);
            }
            return converted;
        } catch (RuntimeException e) {
            if (errors.hasErrors()) { //if parse errors, throw those first!
                throw new RuntimeException("errors parsing ODIN file: " + errors);
            }
            throw e;
        }
    }

}
