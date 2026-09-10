package com.nedap.archie.rminfo;

import tools.jackson.databind.ObjectMapper;

/** Jackson 3 mappers for RM JSON and ODIN input/output. */
public interface RMObjectMapperProvider3 {

    ObjectMapper getInputOdinObjectMapper();

    ObjectMapper getOutputOdinObjectMapper();

    ObjectMapper getJsonObjectMapper();
}
