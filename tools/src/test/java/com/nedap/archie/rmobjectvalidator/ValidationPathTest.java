package com.nedap.archie.rmobjectvalidator;

import static com.nedap.archie.rmobjectvalidator.ValidationPath.ROOT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nedap.archie.aom.ArchetypeSlot;
import org.junit.jupiter.api.Test;

class ValidationPathTest {

    @Test
    void root() {
        assertEquals("", ROOT.toString());
    }

    @Test
    void attributeChaining() {
        ValidationPath path = ROOT.add("data").add("events").add("value");
        assertEquals("/data/events/value", path.toString());
    }

    @Test
    void addAttributeWithCObjectAppendsNodeId() {
        ArchetypeSlot cObject = new ArchetypeSlot();
        cObject.setNodeId("at0001");
        ValidationPath path = ROOT.add("data", cObject);
        assertEquals("/data[at0001]", path.toString());
    }

    @Test
    void joinPathsOnRoot() {
        assertEquals("/value", ROOT.joinPaths("value").toString());
        assertEquals("/value", ROOT.joinPaths("/value").toString());
        assertEquals("/", ROOT.joinPaths("").toString());
        assertEquals("/", ROOT.joinPaths("/").toString());
    }

    @Test
    void joinPathsStripsOnAdd() {
        assertEquals("/data/value", ROOT.add("data").joinPaths("/value").toString());
        assertEquals("/data/value", ROOT.add("data").joinPaths("value").toString());
    }

    @Test
    void joinPathsOnJoin() {
        assertEquals("/data/value", ROOT.joinPaths("data").joinPaths("value").toString());
        assertEquals("/data/value", ROOT.joinPaths("data/").joinPaths("value").toString());
        assertEquals("/data/value", ROOT.joinPaths("data/").joinPaths("/value").toString());
        assertEquals("/data/value", ROOT.joinPaths("data").joinPaths("/value").toString());

        assertEquals("/data", ROOT.joinPaths("data").joinPaths("").toString());
        assertEquals("/data", ROOT.joinPaths("data").joinPaths("/").toString());
        assertEquals("/data", ROOT.joinPaths("data/").joinPaths("").toString());
        assertEquals("/data", ROOT.joinPaths("data/").joinPaths("/").toString());
    }

    @Test
    void stripLastPath() {
        assertEquals("", ROOT.stripLastPathSegment().toString());
        assertEquals("", ROOT.joinPaths("/").stripLastPathSegment().toString());
        assertEquals("/data", ROOT.add("data").add("events").stripLastPathSegment().toString());
        assertEquals("", ROOT.add("data").stripLastPathSegment().toString());
    }
}
