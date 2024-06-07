package com.nedap.archie.adlparser;

import com.nedap.archie.ArchieLanguageConfiguration;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeModelObject;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.primitives.CReal;
import com.nedap.archie.query.AOMPathQuery;
import com.nedap.archie.query.PartialMatch;
import com.nedap.archie.testutil.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test APath queries with archetype model objects
 *
 * Created by pieter.bos on 20/10/15.
 */
public class AOMPathQueryTest {

    private Archetype archetype;

    @Before
    public void setup() throws Exception {
        archetype = TestUtil.parseFailOnErrors(this.getClass(),"/basic.adl");
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
        Archetype archetype = TestUtil.parseFailOnErrors(this.getClass(),"/adl2-tests/features/specialisation/openEHR-EHR-OBSERVATION.redefine_1_value.v1.0.0.adls");

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

    @Test
    public void findOneMatchingObject() {
        // Get dv_quantity object
        AOMPathQuery query = new AOMPathQuery("/context[id11]/other_context[id2]/items[qualification]/items[4]/value[1]");
        CComplexObject dvQuantity = query.find(archetype.getDefinition());

        // Attribute magnitude in this dv_quantity has two children
        CAttribute magnitude = dvQuantity.itemAtPath("/magnitude");
        assertEquals(2, magnitude.getChildren().size());
        // So this should give something back
        CReal magnitudeChild = dvQuantity.itemAtPath("/magnitude[2]");
        assertNotNull(magnitudeChild);

        // Attribute precision in this dv_quantity has zero children
        CAttribute precision = dvQuantity.itemAtPath("/precision");
        assertEquals(0, precision.getChildren().size());
        // So this should give nothing back
        precision = dvQuantity.itemAtPath("/precision[1]");
        assertNull(precision);
    }

    @Test
    public void findPartial() {
        String queryString = "/context[id11]/other_context[id2]/items[qualification]/items[4]/value[1]";
        AOMPathQuery query = new AOMPathQuery(queryString);
        PartialMatch fullMatch = query.findPartial(archetype.getDefinition());
        assertTrue("this should be a full match", fullMatch.isFullMatch());
        assertEquals("only one object should be found", 1, fullMatch.getFoundObjects().size());
        assertEquals("the matched string should be equal to the query", queryString, fullMatch.getPathMatched());
        assertEquals("the remainder should be '/'", "/", fullMatch.getRemainingPath());

        queryString = "/context[id11]/health_care_facility/name";
        query = new AOMPathQuery(queryString);
        PartialMatch partialMatch = query.findPartial(archetype.getDefinition());
        assertFalse("this should be a partial match", partialMatch.isFullMatch());
        assertEquals("only one object should be found", 1, partialMatch.getFoundObjects().size());
        assertEquals(archetype.getDefinition().getAttribute("context").getChildren().get(0),
                partialMatch.getFoundObjects().get(0));
        assertEquals("the matched string should be equal to the first part of the query",
                "/context[id11]", partialMatch.getPathMatched());
        assertEquals("the remainder should contain the last part",
                "/health_care_facility/name", partialMatch.getRemainingPath());

        queryString = "/non_existing_path/with_extras";
        query = new AOMPathQuery(queryString);
        PartialMatch noMatch = query.findPartial(archetype.getDefinition());
        assertFalse("this should be a partial match", noMatch.isFullMatch());
        assertEquals("the root node should be found", 1, noMatch.getFoundObjects().size());
        assertEquals(archetype.getDefinition(), noMatch.getFoundObjects().get(0));
        assertEquals("the matched string should be empty",
                "/", noMatch.getPathMatched());
        assertEquals("the remainder should contain the full query",
                queryString, noMatch.getRemainingPath());
    }
}
