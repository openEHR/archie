package com.nedap.archie.flattener.specexamples;

import com.nedap.archie.adlparser.ADLParseException;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
public class FlattenerTestUtil {

    public static Archetype parse(String filename) throws IOException, ADLParseException {
        ADLParser parser = new ADLParser();
        try(InputStream stream = FlattenerTestUtil.class.getResourceAsStream(filename)) {
            if (stream == null) {
                fail("cannot find file: " + filename);
            }
            Archetype result = parser.parse(stream);
            assertThat("there should be no errors parsing " + filename + ", but was: " + parser.getErrors(), parser.getErrors().hasNoMessages());
            return result;
        }
    }
}
