package com.nedap.archie.adlparser;

import com.nedap.archie.ArchieLanguageConfiguration;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.testutil.TestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by pieter.bos on 20/10/15.
 */
public class PathTest {

    private Archetype archetype;

    @BeforeEach
    public void setup() throws Exception {
        archetype = TestUtil.parseFailOnErrors("/basic.adl");
    }

    @Test
    public void rootNode() {
        assertEquals("/", archetype.getDefinition().getPath());
    }

    @Test
    public void complexObject() {
        CObject object = archetype.getDefinition().getAttribute("context").getChild("id11").getAttribute("other_context").getChild("id2").getAttribute("items").getChild("id3");
        assertEquals("/context[id11]/other_context[id2]/items[id3]", object.getPath());
    }

    @Test
    public void primitiveObject() {
        CObject object = archetype.getDefinition()
                .getAttribute("context").getChild("id11")
                .getAttribute("other_context").getChild("id2")
                .getAttribute("items").getChild("id3")
                .getAttribute("items").getChild("id5")
                .getAttribute("value").getChild("id14")
                .getAttribute("value").getChildren().get(0);//TODO: no node id's here i think?
        assertEquals("/context[id11]/other_context[id2]/items[id3]/items[id5]/value[id14]/value", object.getPath());
    }

    @Test
    public void attribute() {
        CAttribute attribute = archetype.getDefinition().getAttribute("context").getChild("id11").getAttribute("other_context").getChild("id2").getAttribute("items");
        assertEquals("/context[id11]/other_context[id2]/items", attribute.getPath());
    }

}
