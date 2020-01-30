package com.nedap.archie.adl14;

import com.nedap.archie.json.JacksonUtil;

import java.io.IOException;
import java.io.InputStream;

public class ConversionConfigForTest {

    private static ADL14ConversionConfiguration config = null;

    public static ADL14ConversionConfiguration getConfig() throws IOException {
        if(config != null) {
            return config;
        }
        try(InputStream stream = ConversionConfigForTest.class.getResourceAsStream("configuration.json")) {
            config = JacksonUtil.getObjectMapper().readValue(stream, ADL14ConversionConfiguration.class);
        }
        return config;
    }
}
