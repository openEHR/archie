package org.openehr.bmm.v2.persistence.converters;


import org.junit.jupiter.api.Test;
import org.openehr.bmm.v2.persistence.PBmmSchema;
import org.openehr.bmm.v2.persistence.odin.BmmOdinParser;
import org.openehr.bmm.v2.persistence.odin.BmmOdinSerializer;

import static org.openehr.bmm.v2.persistence.converters.BmmTestUtil.parse;

/**
 * Parse a number of BMM schemas
 *
 * Serialize it
 *
 * Parse again
 *
 * Check that they are equal
 */
public class BmmParseRoundtripTest {

    @Test
    public void parseTestBmmRoundTrip() throws Exception{
         parseRoundTrip("/testbmm/TestBmm1.bmm");
    }

    @Test
    public void parseOpenEHRRoundTrip() throws Exception{
        parseRoundTrip("/openehr/openehr_basic_types_102.bmm");
        parseRoundTrip("/openehr/openehr_demographic_102.bmm");
        parseRoundTrip("/openehr/openehr_ehr_102.bmm");
        parseRoundTrip("/openehr/openehr_primitive_types_102.bmm");
        parseRoundTrip("/openehr/openehr_rm_102.bmm");
        parseRoundTrip("/openehr/openehr_structures_102.bmm");
    }

    public void parseRoundTrip(String name) throws Exception {
        PBmmSchema schema = parse(name);
        String serialized = new BmmOdinSerializer().serialize(schema);
        System.out.print(serialized);
        PBmmSchema parsed = BmmOdinParser.convert(serialized);
        PBmmEqualsAssertions.assertSchemaEquals(schema, parsed);
    }

}
