package com.nedap.archie.rminfo;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Used to retrieve object mappers for RM Objects. Three forms of ObjectMappers:
 * <ol>
 * <li>1. Json format ObjectMapper</li>
 * <li>2. ODIN format ObjectMapper, only to be used for output/writing</li>
 * <li>3. ODIN format ObjectMapper, only to be used for input/reading</li>
 * </ol>
 *
 * Mapper 2 and 3 would be better as one object mapper, but none such currently exist for the ODIN format, as there is
 * no native ODIN jackson parser, only a ODIN -&gt; JSON -&gt; Objects route, and there is a native ODIN serializer.
s * The methods ending in 3 return the Jackson 3 equivalents. They are used by the Jackson 3 parser and serializer, and
 * throw an {@link UnsupportedOperationException} unless an implementation overrides them.
 */
public interface RMObjectMapperProvider {

    ObjectMapper getInputOdinObjectMapper();

    ObjectMapper getOutputOdinObjectMapper();

    ObjectMapper getJsonObjectMapper();

    default tools.jackson.databind.ObjectMapper getInputOdinObjectMapper3() {
        throw unsupportedJackson3("getInputOdinObjectMapper3");
    }

    default tools.jackson.databind.ObjectMapper getOutputOdinObjectMapper3() {
        throw unsupportedJackson3("getOutputOdinObjectMapper3");
    }

    default tools.jackson.databind.ObjectMapper getJsonObjectMapper3() {
        throw unsupportedJackson3("getJsonObjectMapper3");
    }

    private UnsupportedOperationException unsupportedJackson3(String method) {
        return new UnsupportedOperationException(getClass().getName() + " does not support Jackson 3. Override "
                + method + "() to use it with the Jackson 3 parser or serializer.");
    }
}
