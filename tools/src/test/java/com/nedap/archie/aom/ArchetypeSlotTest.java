package com.nedap.archie.aom;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ArchetypeSlotTest {

    @Test
    public void isLeaf() {
        ArchetypeSlot archetypeSlot = new ArchetypeSlot();

        assertTrue(archetypeSlot.isLeaf());
    }
}