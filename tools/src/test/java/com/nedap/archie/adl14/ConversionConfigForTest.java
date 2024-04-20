package com.nedap.archie.adl14;

import com.nedap.archie.openehr.serialisation.json.OpenEhrRmJacksonUtil;

import java.io.IOException;
import java.io.InputStream;

public class ConversionConfigForTest {


    public static ADL14ConversionConfiguration getConfig() throws IOException {

        try(InputStream stream = ConversionConfigForTest.class.getResourceAsStream("configuration.json")) {
            return OpenEhrRmJacksonUtil.getObjectMapper().readValue(stream, ADL14ConversionConfiguration.class);
        }

    }
}
