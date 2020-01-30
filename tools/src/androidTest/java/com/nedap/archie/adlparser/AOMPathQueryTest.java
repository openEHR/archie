package com.nedap.archie.adlparser;

import com.nedap.archie.ArchieLanguageConfiguration;
import com.nedap.archie.adlparser.modelconstraints.RMConstraintImposer;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeModelObject;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.query.AOMPathQuery;
import com.nedap.archie.testutil.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Test APath queries with archetype model objects
 *
 * Created by pieter.bos on 20/10/15.
 */
public class AOMPathQueryTest {

    private Archetype archetype;

    @Before
    public void setup() throws Exception {
        archetype = TestUtil.parseFailOnErrors("/basic.adl");
        ArchieLanguageConfiguration.setThreadLocalLogicalPathLanguage("en");
        ArchieLanguageConfiguration.setThreadLocalDescriptiongAndMeaningLanguage("en");
    }

    @After
    public void tearDown() throws Exception {
        ArchieLanguageConfiguration.setThreadLocalLogicalPathLanguage(null);
        ArchieLanguageConfiguration.setThreadLocalDescriptiongAndMeaningLanguage(null);
    }


    @Test
    public void basicPaths() throws Exception {

        AOMPathQuery query = new AOMPathQuery("/context[id11]");
        ArchetypeModelObject archetypeModelObject = query.find(archetype.getDefinition());
        assertEquals("EVENT_CONTEXT", ((CComplexObject) archetypeModelObject).getRmTypeName());

        //"/context[id11]/other_context[id2]/items[id3]/items[id4]/value
        query = new AOMPathQuery("/context/other_context");
        archetypeModelObject = query.find(archetype.getDefinition());
        assertEquals("other_context", ((CAttribute) archetypeModelObject).getRmAttributeName());

        query = new AOMPathQuery("/context[id11]/other_context[id2]");
        archetypeModelObject = query.find(archetype.getDefinition());
        assertEquals("ITEM_TREE", ((CComplexObject) archetypeModelObject).getRmTypeName());
    }

    @Test
    public void differentialPaths() throws Exception {
        Archetype archetype = TestUtil.parseFailOnErrors("/adl2-tests/features/specialisation/openEHR-EHR-OBSERVATION.redefine_1_value.v1.0.0.adls");

        //query with a differential path halfway
        AOMPathQuery query = new AOMPathQuery("/data/events[id3]/data/items[id4.1]/value[id0.6]");
        ArchetypeModelObject archetypeModelObject = query.find(archetype.getDefinition());
        assertEquals("id0.6", ((CComplexObject) archetypeModelObject).getNodeId());

        //partial match of differential path should not return result
        query = new AOMPathQuery("/data/events[id3]");
        archetypeModelObject = query.find(archetype.getDefinition());
        assertNull(archetypeModelObject);

    }

    @Test
    public void nameAttributeIgnoredForNow() throws Exception {
        AOMPathQuery query = new AOMPathQuery("/context[id11 and name=\"ignored\"]");
        ArchetypeModelObject archetypeModelObject = query.find(archetype.getDefinition());
        assertEquals("EVENT_CONTEXT", ((CComplexObject) archetypeModelObject).getRmTypeName());

    }

    @Test
    public void logicalPaths() throws Exception {
        AOMPathQuery query = new AOMPathQuery("/context[id11]/other_context[id2]/items[qualification]/items[orderid]");
        ArchetypeModelObject archetypeModelObject = query.find(archetype.getDefinition());
        assertNotNull(archetypeModelObject);
        assertEquals("id4", ((CComplexObject) archetypeModelObject).getNodeId());

    }


    @Test
    public void indexedPath() throws Exception {
        AOMPathQuery query = new AOMPathQuery("/context[id11]/other_context[id2]/items[qualification]/items[2]");
        ArchetypeModelObject archetypeModelObject = query.find(archetype.getDefinition());
        assertNotNull(archetypeModelObject);
        assertEquals("id5", ((CComplexObject) archetypeModelObject).getNodeId());

    }
}
