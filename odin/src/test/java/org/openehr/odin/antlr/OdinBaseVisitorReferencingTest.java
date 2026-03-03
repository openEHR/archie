package org.openehr.odin.antlr;

import org.junit.jupiter.api.Test;
import org.openehr.odin.CompositeOdinObject;
import org.openehr.odin.loader.OdinLoaderImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OdinBaseVisitorReferencingTest {

    @Test
    public void loadOdinNestedAttributeStructures1() throws Exception {
        OdinLoaderImpl loader = new OdinLoaderImpl();
        OdinVisitorImpl visitor = loader.loadOdinFile(OdinBaseVisitorTest.class.getResourceAsStream("/odin/odin_test.txt"));
        assertEquals(1, visitor.getStack().size(), "Stack should consist of a single item");
        CompositeOdinObject root = visitor.getAstRootNode();
    }
}
