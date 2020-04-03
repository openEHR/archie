package com.nedap.archie.adl14;

import com.nedap.archie.json.JacksonUtil;

import java.io.IOException;
import java.io.InputStream;

public class ConversionConfigForTest {


    public static ADL14ConversionConfiguration getConfig() throws IOException {

        try(InputStream stream = ConversionConfigForTest.class.getResourceAsStream("configuration.json")) {
            return JacksonUtil.getObjectMapper().readValue(stream, ADL14ConversionConfiguration.class);
        }

    }
}
