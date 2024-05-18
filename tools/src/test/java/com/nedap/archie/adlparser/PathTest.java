package com.nedap.archie.adlparser;

import com.nedap.archie.ArchieLanguageConfiguration;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.testutil.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by pieter.bos on 20/10/15.
 */
public class PathTest {

    private Archetype archetype;

    @Before
    public void setup() throws Exception {
        archetype = TestUtil.parseFailOnErrors(this.getClass(),"/basic.adl");
        ArchieLanguageConfiguration.setThreadLocalLogicalPathLanguage("en");
    }

    @After
    public void tearDown() throws Exception {
        ArchieLanguageConfiguration.setThreadLocalLogicalPathLanguage(null);
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


    @Test
    public void logicalPath() {
        CObject object = archetype.getDefinition()
                .getAttribute("context").getChild("id11")
                .getAttribute("other_context").getChild("id2")
                .getAttribute("items").getChild("id3")
                .getAttribute("items").getChild("id4");
        //id11 and id2 are not translated
        assertEquals("/context[id11]/other_context[id2]/items[Qualification]/items[OrderID]", object.getLogicalPath());
    }
}
