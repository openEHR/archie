package org.openehr.odin.jackson;

import org.junit.jupiter.api.Test;
import org.openehr.odin.jackson.testclasses.ContainerWithList;
import org.openehr.odin.jackson.testclasses.TestObject;

public class OdinSerializeTest {

    @Test
    public void serializeListOfObject() throws Exception {
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

        ODINMapper mapper = new ODINMapper();

        String s = mapper.writeValueAsString(listContainer);
        System.out.println(s);
    }
}
