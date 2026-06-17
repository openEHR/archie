package org.openehr.bmm.v2.persistence.odin;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openehr.bmm.v2.persistence.PBmmSchema;
import org.openehr.bmm.v2.persistence.jackson.BmmJacksonUtil;
import org.openehr.bmm.v2.persistence.jackson3.BmmJacksonUtil3;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

public class BmmOdinParserTest {

    interface JsonMapper {
        String writeValueAsString(Object value) throws Exception;
    }

    private static Stream<Arguments> mappers() {
        return Stream.of(
                Arguments.of(Named.of("Jackson 2", (JsonMapper) o -> BmmJacksonUtil.getObjectMapper().writeValueAsString(o))),
                Arguments.of(Named.of("Jackson 3", (JsonMapper) o -> BmmJacksonUtil3.getObjectMapper().writeValueAsString(o)))
        );
    }

    @ParameterizedTest
    @MethodSource("mappers")
    public void parseTestBmm1(JsonMapper mapper) throws Exception {
        try (InputStream stream = getClass().getResourceAsStream("/testbmm/TestBmm1.bmm")) {
            PBmmSchema schema = BmmOdinParser.convert(stream);
            String s = mapper.writeValueAsString(schema);
            System.out.println(s);
        }
    }
}
