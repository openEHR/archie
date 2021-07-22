package com.nedap.archie.adl14;


import com.nedap.archie.json.JacksonUtil;

import java.io.IOException;
import java.io.InputStream;

public class OpenEHRADL14ConversionConfiguration {

    public static ADL14ConversionConfiguration getConfig() throws IOException {
        try(InputStream stream = OpenEHRADL14ConversionConfiguration.class.getResourceAsStream("/adl14conversionconfiguration.json")) {
            return JacksonUtil.getObjectMapper().readValue(stream, ADL14ConversionConfiguration.class);
        }
    }

}
