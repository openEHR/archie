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
 */
public interface RMObjectMapperProvider {

    ObjectMapper getInputOdinObjectMapper();

    ObjectMapper getOutputOdinObjectMapper();

    ObjectMapper getJsonObjectMapper();
}
