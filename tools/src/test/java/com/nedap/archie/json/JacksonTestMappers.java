package com.nedap.archie.json;

import com.nedap.archie.json3.JacksonUtil3;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.provider.Arguments;

import java.io.InputStream;
import java.util.stream.Stream;

/**
 * Shared test utilities for parameterized tests that run against both Jackson 2 and Jackson 3.
 * Use {@link #mappers()} as a {@code @MethodSource} to get both mappers as test arguments.
 */
public class JacksonTestMappers {

    public interface JsonMapper {
        <T> T readValue(String content, Class<T> type) throws Exception;
        <T> T readValue(InputStream src, Class<T> type) throws Exception;
        String writeValueAsString(Object value) throws Exception;
    }

    public interface JsonMapperFactory {
        JsonMapper create();
        JsonMapper create(ArchieJacksonConfiguration config);
    }

    public static JsonMapper wrap(com.fasterxml.jackson.databind.ObjectMapper m) {
        return new JsonMapper() {
            public <T> T readValue(String s, Class<T> t) throws Exception { return m.readValue(s, t); }
            public <T> T readValue(InputStream s, Class<T> t) throws Exception { return m.readValue(s, t); }
            public String writeValueAsString(Object o) throws Exception { return m.writeValueAsString(o); }
        };
    }

    public static JsonMapper wrap(tools.jackson.databind.ObjectMapper m) {
        return new JsonMapper() {
            public <T> T readValue(String s, Class<T> t) { return m.readValue(s, t); }
            public <T> T readValue(InputStream s, Class<T> t) { return m.readValue(s, t); }
            public String writeValueAsString(Object o) { return m.writeValueAsString(o); }
        };
    }

    public static Stream<Arguments> mappers() {
        JsonMapperFactory j2 = new JsonMapperFactory() {
            public JsonMapper create() { return wrap(JacksonUtil.getObjectMapper()); }
            public JsonMapper create(ArchieJacksonConfiguration c) { return wrap(JacksonUtil.getObjectMapper(c)); }
        };
        JsonMapperFactory j3 = new JsonMapperFactory() {
            public JsonMapper create() { return wrap(JacksonUtil3.getObjectMapper()); }
            public JsonMapper create(ArchieJacksonConfiguration c) { return wrap(JacksonUtil3.getObjectMapper(c)); }
        };
        return Stream.of(
                Arguments.of(Named.of("Jackson 2", j2)),
                Arguments.of(Named.of("Jackson 3", j3))
        );
    }
}
