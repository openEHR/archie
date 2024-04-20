package com.nedap.archie.rm.datastructures;

import org.openehr.rm.datastructures.Element;
import org.openehr.rm.datastructures.ItemList;
import org.openehr.rm.datavalues.DvText;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ItemListTest {

    @Test
    public void testEquals() {
        Element elementOne = new Element("elementOne", new DvText("textElementOne"), new DvText("valueElementOne"));
        Element elementTwo = new Element("elementTwo", new DvText("textElementTwo"), new DvText("valueElementTwo"));

        ItemList itemListOne = new ItemList("itemList", new DvText("Text1"), Arrays.asList(elementOne, elementTwo));
        ItemList itemListTwo = new ItemList("itemList", new DvText("Text1"), Arrays.asList(elementOne, elementTwo));
        ItemList itemListThree = new ItemList("itemList", new DvText("Text1"), null);

        assertEquals(itemListOne, itemListTwo);
        assertNotEquals(itemListOne, itemListThree);
        assertNotEquals(itemListThree, itemListTwo);
    }
}