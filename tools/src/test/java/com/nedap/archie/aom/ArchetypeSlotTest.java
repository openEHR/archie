package com.nedap.archie.aom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ArchetypeSlotTest {

    @Test
    public void isLeaf() {
        ArchetypeSlot archetypeSlot = new ArchetypeSlot();

        assertTrue(archetypeSlot.isLeaf());
    }
}