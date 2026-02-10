package com.nedap.archie.aom;

import com.nedap.archie.testutil.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertFalse;

/**
 * Created by pieter.bos on 21/10/15.
 */
public class ArchetypeCloneTest {

    private Archetype archetype;

    @BeforeEach
    public void setup() throws Exception {
        archetype = TestUtil.parseFailOnErrors("/basic.adl");
    }

    @Test
    public void cloneArchetype() {
        Archetype cloned = archetype.clone();
        assertFalse(cloned == archetype);
        assertFalse(archetype.getDefinition().getAttributes().get(0) ==
                cloned.getDefinition().getAttributes().get(0));
    }


}
