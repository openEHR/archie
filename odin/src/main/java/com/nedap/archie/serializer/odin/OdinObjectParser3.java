package com.nedap.archie.serializer.odin;

import tools.jackson.databind.JavaType;
import com.nedap.archie.adlparser.antlr.AdlParser;


/** Binds ODIN in ADL to objects with Jackson 3. */
public class OdinObjectParser3 {

    public static <T> T convert(AdlParser.Odin_textContext odin, Class<T> clazz) {
        try {
            return AdlOdinToJsonConverter3.getObjectMapper().readValue(new AdlOdinToJsonConverter3().convert(odin), clazz);
        } catch (tools.jackson.core.JacksonException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T convert(AdlParser.Odin_textContext odin, JavaType clazz) {
        try {
            return AdlOdinToJsonConverter3.getObjectMapper().readValue(new AdlOdinToJsonConverter3().convert(odin), clazz);
        } catch (tools.jackson.core.JacksonException e) {
            throw new RuntimeException(e);
        }
    }

}
