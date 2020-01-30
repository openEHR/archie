package com.nedap.archie.aom;

import com.nedap.archie.base.MultiplicityInterval;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by pieter.bos on 14/04/2017.
 */
public class CAttributeTest {

    @Test
    public void mandatory() {
        CAttribute attribute = new CAttribute();
        attribute.setExistence(new MultiplicityInterval(0, 1));
        assertFalse(attribute.isMandatory());
        attribute.setExistence(new MultiplicityInterval(1, 1));
        assertTrue(attribute.isMandatory());
    }

    @Test
    public void prohibited() {
        CAttribute attribute = new CAttribute();
        attribute.setExistence(new MultiplicityInterval(0, 1));
        assertFalse(attribute.isProhibited());
        attribute.setExistence(new MultiplicityInterval(1, 1));
        assertFalse(attribute.isProhibited());
        attribute.setExistence(new MultiplicityInterval(0, 0));
        assertTrue(attribute.isProhibited());
    }

    @Test
    public void single() {
        CAttribute attribute = new CAttribute();
        attribute.setMultiple(false);
        assertTrue(attribute.isSingle());
        attribute.setMultiple(true);
        assertFalse(attribute.isSingle());
    }

    @Test
    public void getChildByNodeId() {
        CAttribute attribute = new CAttribute();
        CComplexObject child1 = createCComplexObject("id2");
        CComplexObject child2 = createCComplexObject("id3");
        attribute.addChild(child1);
        attribute.addChild(child2);

        assertEquals(child1, attribute.getChild("id2"));
        assertEquals(child2, attribute.getChild("id3"));
        assertNull(attribute.getChild("id4"));
    }

    @Test
    public void anyAllowed() {
        CAttribute attribute = new CAttribute();

        assertTrue(attribute.anyAllowed());

        attribute.setExistence(new MultiplicityInterval(0, 0));
        assertFalse(attribute.anyAllowed());

        attribute.setExistence(new MultiplicityInterval(1, 1));
        assertTrue(attribute.anyAllowed());


        CComplexObject child1 = createCComplexObject("id2");
        attribute.addChild(child1);
        assertFalse(attribute.anyAllowed());
    }

    @Test
    public void replaceChild() {
        CAttribute attribute = new CAttribute();
        CComplexObject child1 = createCComplexObject("id2");
        CComplexObject child2 = createCComplexObject("id3");
        CComplexObject child3 = createCComplexObject("id4");
        attribute.addChild(child1);
        attribute.addChild(child2);


        attribute.replaceChild("id2", child3);
        assertNull(attribute.getChild("id2"));
        assertEquals(child3, attribute.getChild("id4"));
        assertEquals(child2, attribute.getChild("id3"));
    }

    private CComplexObject createCComplexObject(String nodeId) {
        CComplexObject complexObject = new CComplexObject();
        complexObject.setNodeId(nodeId);
        return complexObject;
    }
}
