package org.openehr.bmm.v2.persistence.converters;

import org.openehr.bmm.v2.persistence.PBmmSchema;
import org.openehr.bmm.v2.persistence.odin.BmmOdinParser;

import java.io.IOException;
import java.io.InputStream;

public class BmmTestUtil {

    public static PBmmSchema parse(String name) throws IOException {
        try (InputStream stream = BmmTestUtil.class.getResourceAsStream(name)) {
            return BmmOdinParser.convert(stream);
        }
    }
}
