package com.nedap.archie.serializer.odin;

import org.junit.jupiter.api.Test;

/**
 * class that contains the tests for the two ODIN converters - that should be exactly the same, they just use a different
 * lexer - one has the ADL specific additions and is for use within ADL without an extra lexer/parser pass,
 * the other is independent.
 */
public abstract class OdinToJsonConverterBaseTest {

    @Test
    public void convertURI() throws Exception {
        assertConvertedEqual("original_resource_uri = < [\"resource A\"] = <http://test.example.com> >",
                "{\"original_resource_uri\":{\"resource A\":\"http://test.example.com\"}}");
        assertConvertedEqual("original_resource_uri = < [\"resource A\"] = <http://test.example.com/> >",
                "{\"original_resource_uri\":{\"resource A\":\"http://test.example.com/\"}}");

        assertConvertedEqual("original_resource_uri = < [\"resource A\"] = <http://test.example.com/aa/bb> >",
                "{\"original_resource_uri\":{\"resource A\":\"http://test.example.com/aa/bb\"}}");

        assertConvertedEqual("original_resource_uri = < [\"resource A\"] = <http://test.example.com/aa/bb/> >",
                "{\"original_resource_uri\":{\"resource A\":\"http://test.example.com/aa/bb/\"}}");
    }

    //checks that the given ODIN converts to the given JSON
    public abstract void assertConvertedEqual(String odin, String json);
}
