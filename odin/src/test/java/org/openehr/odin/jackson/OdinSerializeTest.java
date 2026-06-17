package org.openehr.odin.jackson;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openehr.odin.jackson.testclasses.ContainerWithList;
import org.openehr.odin.jackson.testclasses.TestObject;
import org.openehr.odin.jackson3.ODINMapper3;

import java.util.stream.Stream;

public class OdinSerializeTest {

    interface OdinMapper {
        String writeValueAsString(Object value) throws Exception;
    }

    private static Stream<Arguments> mappers() {
        return Stream.of(
                Arguments.of(Named.of("Jackson 2", (OdinMapper) o -> new ODINMapper().writeValueAsString(o))),
                Arguments.of(Named.of("Jackson 3", (OdinMapper) o -> ODINMapper3.builder().build().writeValueAsString(o)))
        );
    }

    @ParameterizedTest
    @MethodSource("mappers")
    public void serializeListOfObject(OdinMapper mapper) throws Exception {
        ContainerWithList listContainer = new ContainerWithList();
        listContainer.setSomeField("some field");
        TestObject testObject = new TestObject();
        testObject.setStringField("test1");
        testObject.setIntField(1);
        listContainer.addListItem(testObject);

        TestObject testObject2 = new TestObject();
        testObject2.setStringField("test2");
        testObject2.setIntField(2);
        listContainer.addListItem(testObject2);

        String s = mapper.writeValueAsString(listContainer);
        System.out.println(s);
    }
}
