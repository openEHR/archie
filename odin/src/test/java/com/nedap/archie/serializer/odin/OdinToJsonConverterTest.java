package com.nedap.archie.serializer.odin;

import com.nedap.archie.adlparser.antlr.AdlLexer;
import com.nedap.archie.adlparser.antlr.AdlParser;
import com.nedap.archie.adlparser.antlr.odinLexer;
import com.nedap.archie.adlparser.antlr.odinParser;
import com.nedap.archie.antlr.errors.ArchieErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OdinToJsonConverterTest extends OdinToJsonConverterBaseTest {

    @Override
    public void assertConvertedEqual(String odin, String json) {
        String input = odin;
        odinLexer odinLexer = new odinLexer(CharStreams.fromString(input));
        odinParser parser = new odinParser(new CommonTokenStream(odinLexer));
        ArchieErrorListener errorListener = new ArchieErrorListener();
        parser.addErrorListener(errorListener);
        OdinToJsonConverter converter = new OdinToJsonConverter();
        String result = converter.convert(parser.odin_text());
        assertTrue(errorListener.getErrors().toString(), errorListener.getErrors().hasNoErrors());

        assertEquals("the converted json should be equal to the expected", json, result);
    }
}
