package com.nedap.archie.serializer.odin;


import com.nedap.archie.adlparser.antlr.AdlLexer;
import com.nedap.archie.adlparser.antlr.AdlParser;
import com.nedap.archie.antlr.errors.ArchieErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdlOdinToJsonConverterTest extends OdinToJsonConverterBaseTest {

    @Override
    public void assertConvertedEqual(String odin, String json) {
        String input = odin;
        AdlLexer adlLexer = new AdlLexer(CharStreams.fromString(input));
        AdlParser parser = new AdlParser(new CommonTokenStream(adlLexer));
        ArchieErrorListener errorListener = new ArchieErrorListener();
        parser.addErrorListener(errorListener);
        AdlOdinToJsonConverter converter = new AdlOdinToJsonConverter();
        String result = converter.convert(parser.odin_text());
        assertTrue(errorListener.getErrors().hasNoErrors(), errorListener.getErrors().toString());

        assertEquals(json, result, "the converted json should be equal to the expected");
    }
}
