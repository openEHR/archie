package com.nedap.archie.serializer.odin;

import com.nedap.archie.adlparser.antlr.AdlLexer;
import com.nedap.archie.adlparser.antlr.AdlParser;
import com.nedap.archie.antlr.errors.ArchieErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Runs the existing ODIN conversion contract with Jackson 3. */
public class AdlOdinToJsonConverter3Test extends OdinToJsonConverterBaseTest {
    @Override
    public void assertConvertedEqual(String odin, String json) {
        AdlParser parser = new AdlParser(new CommonTokenStream(new AdlLexer(CharStreams.fromString(odin))));
        ArchieErrorListener errors = new ArchieErrorListener();
        parser.addErrorListener(errors);
        String result = new AdlOdinToJsonConverter3().convert(parser.odin_text());
        assertTrue(errors.getErrors().hasNoErrors(), errors.getErrors().toString());
        assertEquals(json, result);
    }
}
