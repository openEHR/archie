package com.nedap.archie.testutil;

import com.nedap.archie.adlparser.ADLParseException;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ParseValidArchetypeTestUtil {

    public static Archetype parse(Class<?> caller, String filename) throws IOException, ADLParseException {
        ADLParser parser = new ADLParser();
        try(InputStream stream = caller.getResourceAsStream(filename)) {
            if (stream == null) {
                fail("cannot find file: " + filename);
            }
            Archetype result = parser.parse(stream);
            assertTrue("there should be no errors parsing " + filename + ", but was: " + parser.getErrors(), parser.getErrors().hasNoMessages());
            return result;
        }
    }
}
