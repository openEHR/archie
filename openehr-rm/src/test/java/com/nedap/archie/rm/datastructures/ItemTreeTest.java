package com.nedap.archie.rm.datastructures;

import org.openehr.rm.datastructures.Element;
import org.openehr.rm.datastructures.ItemTree;
import org.openehr.rm.datavalues.DvText;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ItemTreeTest {

    @Test
    public void testEquals() {
        Element elementOne = new Element("elementOne", new DvText("textElementOne"), new DvText("valueElementOne"));
        Element elementTwo = new Element("elementTwo", new DvText("textElementTwo"), new DvText("valueElementTwo"));

        ItemTree itemTreeOne = new ItemTree("itemTree", new DvText("Text1"), Arrays.asList(elementOne, elementTwo));
        ItemTree itemTreeTwo = new ItemTree("itemTree", new DvText("Text1"), Arrays.asList(elementOne, elementTwo));
        ItemTree itemTreeThree = new ItemTree("itemTree", new DvText("Text1"), null);

        assertEquals(itemTreeOne, itemTreeTwo);
        assertNotEquals(itemTreeOne, itemTreeThree);
        assertNotEquals(itemTreeThree, itemTreeTwo);
    }
}