package com.nedap.archie.aom;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArchetypeTest {

    @Test
    public void testGetUsedIdCodes() {
        Archetype archetype = createLevel0Archetype();
        assertEquals(Sets.newHashSet("id1", "id2"), archetype.getUsedIdCodes());
    }

    @Test
    public void testLevel0GenerateIdCode() {
        Archetype archetype = createLevel0Archetype();
        //the generator will generate the same codes until their corresponding objects are added to the archetype
        assertEquals("id3", archetype.generateNextIdCode());
        assertEquals("at3", archetype.generateNextValueCode());
        assertEquals("ac3", archetype.generateNextValueSetCode());
    }

    @Test
    public void testLevel1GenerateIdCode() {
        Archetype archetype = createLevel1Archetype();
        //the generator will generate the same codes until their corresponding objects are added to the archetype
        assertEquals("id0.3", archetype.generateNextIdCode());
        assertEquals("at0.3", archetype.generateNextValueCode());
        assertEquals("ac0.3", archetype.generateNextValueSetCode());
    }

    private Archetype createLevel0Archetype() {
        Archetype archetype = new Archetype();
        CComplexObject definition = new CComplexObject();
        definition.setRmTypeName("CLUSTER");
        definition.setNodeId("id1");
        CAttribute attribute1 = new CAttribute("items");
        definition.addAttribute(attribute1);
        CComplexObject element = new CComplexObject();
        element.setRmTypeName("ELEMENT");
        element.setNodeId("id2");
        attribute1.addChild(element);
        archetype.setDefinition(definition);
        return archetype;
    }

    private Archetype createLevel1Archetype() {
        Archetype archetype = new Archetype();
        CComplexObject definition = new CComplexObject();
        definition.setRmTypeName("CLUSTER");
        definition.setNodeId("id1.1");
        CAttribute attribute1 = new CAttribute("items");
        definition.addAttribute(attribute1);
        CComplexObject element = new CComplexObject();
        element.setRmTypeName("ELEMENT");
        element.setNodeId("id0.2");
        attribute1.addChild(element);
        archetype.setDefinition(definition);
        return archetype;
    }


}
