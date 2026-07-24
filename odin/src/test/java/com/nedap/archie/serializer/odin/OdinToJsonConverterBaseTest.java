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

    @Test
    public void convertBoolean() throws Exception {
        assertConvertedEqual("flag = <True>", "{\"flag\":true}");
        assertConvertedEqual("flag = <False>", "{\"flag\":false}");
    }

    @Test
    public void convertIntegerInterval() throws Exception {
        assertConvertedEqual("range = <|1..10|>",
                "{\"range\":{ \"_type\": \"INTERVAL\" ,\"lower_unbounded\": \"false\",\"upper_unbounded\": \"false\",\"lower_included\": \"true\",\"upper_included\": \"true\",\"lower\": 1,\"upper\": 10}}");
        assertConvertedEqual("range = <|>=5|>",
                "{\"range\":{ \"_type\": \"INTERVAL\" ,\"lower_unbounded\": \"false\",\"upper_unbounded\": \"true\",\"lower\": 5,\"lower_included\": \"true\"}}");
    }

    @Test
    public void convertDateAndDateTime() throws Exception {
        assertConvertedEqual("d = <\"2015-01-01\">", "{\"d\":\"2015-01-01\"}");
        assertConvertedEqual("dt = <\"2015-01-01T12:00:00\">", "{\"dt\":\"2015-01-01T12:00:00\"}");
    }

    @Test
    public void convertPrimitiveList() throws Exception {
        assertConvertedEqual("items = <\"a\", \"b\", \"c\">", "{\"items\":[\"a\",\"b\",\"c\"]}");
    }

    //checks that the given ODIN converts to the given JSON
    public abstract void assertConvertedEqual(String odin, String json);
}
