package com.nedap.archie.rm.directory;

import org.openehr.rm.datastructures.Element;
import org.openehr.rm.datastructures.ItemStructure;
import org.openehr.rm.datastructures.ItemTree;
import org.openehr.rm.datavalues.DvText;
import org.openehr.rm.directory.Folder;
import org.openehr.rm.support.identification.HierObjectId;
import org.openehr.rm.support.identification.ObjectRef;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class FolderTest {

    @Test
    public void testEquals(){
        Element elementOne = new Element("elementOne", new DvText("textElementOne"), new DvText("valueElementOne"));
        Element elementTwo = new Element("elementTwo", new DvText("textElementTwo"), new DvText("valueElementTwo"));
        ItemStructure details = new ItemTree("itemTree", new DvText("Text1"), Arrays.asList(elementOne, elementTwo));

        ObjectRef<HierObjectId> item = new ObjectRef<>(new HierObjectId("test-ref"), "test-ns", "test-type");

        List<ObjectRef<?>> items = new ArrayList<>();
        items.add(item);

        List<Folder> folders = new ArrayList<>();

        Folder folder0 = new Folder("archetype-node-id", new DvText("testFolder"), details, items, null);
        folders.add(folder0);

        Folder folder0_1 = new Folder("archetype-node-id", new DvText("testFolder"), null, items, folders);
        Folder folder0_2 = new Folder("archetype-node-id", new DvText("testFolder"), details, null, folders);


        Folder folder1 = new Folder("archetype-node-id", new DvText("testFolder"), details, items, folders);
        Folder folder2 = new Folder("archetype-node-id", new DvText("testFolder"), details, items, folders);

        Folder folder4 = new Folder("archetype-node-id", new DvText("testFolder"), null, items, folders);
        Folder folder5 = new Folder("archetype-node-id", new DvText("testFolder"), null, items, folders);

        assertNotEquals(folder4, folder0_2);

        Folder folder6 = new Folder("archetype-node-id", new DvText("testFolder"), null, null, folders);
        Folder folder7 = new Folder("archetype-node-id", new DvText("testFolder"), null, null, folders);

        assertNotEquals(folder6, folder0_1);

        Folder folder8 = new Folder("archetype-node-id", new DvText("testFolder"), null, null, null);
        Folder folder9 = new Folder("archetype-node-id", new DvText("testFolder"), null, null, null);

        assertEquals(folder1, folder2);
        assertNotEquals(folder0, folder1);

        assertEquals(folder4, folder5);
        assertEquals(folder6, folder7);
        assertEquals(folder8, folder9);
    }
}
