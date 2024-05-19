package com.nedap.archie.rules.evaluation;

import com.nedap.archie.ArchieLanguageConfiguration;
import com.nedap.archie.adlparser.ADLParseException;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.creation.RMObjectCreator;
import org.openehr.rm.archetyped.Locatable;
import org.openehr.rm.datastructures.Cluster;
import org.openehr.rm.datastructures.Element;
import org.openehr.rm.datastructures.ItemTree;
import org.openehr.rm.datavalues.DvBoolean;
import org.openehr.rm.datavalues.DvText;
import com.nedap.archie.openehr.rminfo.OpenEhrRmInfoLookup;
import com.nedap.archie.testutil.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openehr.referencemodels.AllMetaModelsInitialiser;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by pieter.bos on 04/04/2017.
 */
public class FixableAssertionsCheckerTest {

    private ADLParser parser;
    private Archetype archetype;

    private TestUtil testUtil;
    private RMObjectCreator rmObjectCreator;

    @Before
    public void setup() {
        testUtil = new TestUtil(OpenEhrRmInfoLookup.getInstance());
        rmObjectCreator = new RMObjectCreator(OpenEhrRmInfoLookup.getInstance());
        parser = new ADLParser(AllMetaModelsInitialiser.getMetaModels());
        ArchieLanguageConfiguration.setThreadLocalLogicalPathLanguage("en");
        ArchieLanguageConfiguration.setThreadLocalDescriptiongAndMeaningLanguage("en");
    }

    @After
    public void tearDown() throws Exception {
        ArchieLanguageConfiguration.setThreadLocalLogicalPathLanguage(null);
        ArchieLanguageConfiguration.setThreadLocalDescriptiongAndMeaningLanguage(null);
    }

    @Test
    public void fixableMatches() throws Exception {
        archetype = parser.parse(ParsedRulesEvaluationTest.class.getResourceAsStream("fixable_matches.adls"));
        RuleEvaluation<Locatable> ruleEvaluation = getRuleEvaluation();

        Locatable root = (Locatable) testUtil.constructEmptyRMObject(archetype.getDefinition());
        ItemTree itemTree = (ItemTree) root.itemAtPath("/data[id2]/events[id3]/data[id4]");

        // Add a second cluster with the boolean set to true
        Cluster cluster2 = new Cluster("id19", new DvText("Cluster"), new ArrayList<>());
        DvBoolean dvBoolean = new DvBoolean();
        dvBoolean.setValue(true);
        cluster2.addItem(new Element("id20", new DvText("First element"), dvBoolean));
        itemTree.addItem(cluster2);

        EvaluationResult evaluate = ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        assertEquals("There are eleven values that must be set", 16, evaluate.getSetPathValues().size());

        // Assert that paths must be set to specific values
        // DvText
        assertEquals("test string", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id5]/value/value").getValue());
        // DvCodedText
        assertEquals("at10", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id7]/value/defining_code/code_string").getValue());
        assertEquals("local", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id7]/value/defining_code/terminology_id/value").getValue());
        assertEquals("Option 1", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id7]/value/value").getValue());
        // DvOrdinal
        assertEquals("at11", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id13]/value/symbol/defining_code/code_string").getValue());
        assertEquals("local", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id13]/value/symbol/defining_code/terminology_id/value").getValue());
        assertEquals(1l, evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id13]/value/value").getValue());
        assertEquals("Option 2", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id13]/value/symbol/value").getValue());
        // DvScale
        assertEquals("at12", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id15]/value/symbol/defining_code/code_string").getValue());
        assertEquals("local", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id15]/value/symbol/defining_code/terminology_id/value").getValue());
        assertEquals(2.5, evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id15]/value/value").getValue());
        assertEquals("Option 3", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id15]/value/symbol/value").getValue());
        // DvCodedText with null flavour
        assertEquals("at10", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id17]/null_flavour/defining_code/code_string").getValue());
        assertEquals("local", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id17]/null_flavour/defining_code/terminology_id/value").getValue());
        assertEquals("Option 1", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id17]/null_flavour/value").getValue());
        // DvText in Cluster
        assertEquals("The boolean is true", evaluate.getSetPathValues().get("/data[id2]/events[id3, 1]/data[id4]/items[id19, 7]/items[id22]/value/value").getValue());

        // Now assert that the RM Object cloned by rule evaluation has been modified with the new values for further evaluation
        // DvText
        assertEquals("test string", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id5]/value/value"));
        // DvCodedText
        assertEquals("at10", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id7]/value/defining_code/code_string"));
        assertEquals("local", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id7]/value/defining_code/terminology_id/value"));
        assertEquals("Option 1", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id7]/value/value"));
        // DvOrdinal
        assertEquals("at11", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id13]/value/symbol/defining_code/code_string"));
        assertEquals("local", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id13]/value/symbol/defining_code/terminology_id/value"));
        assertEquals(1l, ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id13]/value/value"));
        // DvScale
        assertEquals("at12", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id15]/value/symbol/defining_code/code_string"));
        assertEquals("local", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id15]/value/symbol/defining_code/terminology_id/value"));
        assertEquals(2.5, ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id15]/value/value"));
        // DvCodedText with null flavour
        assertEquals("at10", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id17]/null_flavour/defining_code/code_string"));
        assertEquals("local", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id17]/null_flavour/defining_code/terminology_id/value"));
        assertEquals("Option 1", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id17]/null_flavour/value"));
        // DvText in Cluster
        assertEquals("The boolean is true", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3, 1]/data[id4]/items[id19, 7]/items[id22]/value/value"));

        // And of course the DV_CODED_TEXT, DV_ORDINAL and DV_SCALE should be constructed correctly, with the correct numeric respectively a textual value
        assertEquals("Option 1", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id7]/value/value"));
        assertEquals("Option 2", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id13]/value/symbol/value"));
        assertEquals(1l, ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id13]/value/value"));
        assertEquals("Option 3", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id15]/value/symbol/value"));
        assertEquals(2.5, ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id15]/value/value"));
        assertEquals("Option 1", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id17]/null_flavour/value"));

        evaluate = ruleEvaluation.evaluate(ruleEvaluation.getRMRoot(), archetype.getRules().getRules());
        for(AssertionResult result:evaluate.getAssertionResults()) {
            assertTrue(result.getResult());
        }
    }

    @Test
    public void andExpression() throws Exception {
        archetype = parser.parse(ParsedRulesEvaluationTest.class.getResourceAsStream("and.adls"));
        RuleEvaluation<Locatable> ruleEvaluation = getRuleEvaluation();

        Locatable root = (Locatable) testUtil.constructEmptyRMObject(archetype.getDefinition());
        EvaluationResult evaluate = ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        assertEquals("There are seven values that must be set", 7, evaluate.getSetPathValues().size());

        //assert that paths must be set to specific values
        assertEquals("test string", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id5]/value/value").getValue());
        assertEquals("at1", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id6]/value/defining_code/code_string").getValue());
        assertEquals("Option 1", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id6]/value/value").getValue());
        assertEquals("local", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id6]/value/defining_code/terminology_id/value").getValue());
        assertEquals("at6", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id7]/value/symbol/defining_code/code_string").getValue());
        assertEquals("local", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id7]/value/symbol/defining_code/terminology_id/value").getValue());
        assertEquals(0l, evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id7]/value/value").getValue());


        //now assert that the RM Object cloned by rule evaluation has been modified with the new values for further evaluation
        assertEquals("test string", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id5]/value/value"));
        assertEquals("at1", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id6]/value/defining_code/code_string"));
        assertEquals("Option 1", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id6]/value/value"));
        assertEquals("at6", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id7]/value/symbol/defining_code/code_string"));
        assertEquals("local", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id7]/value/symbol/defining_code/terminology_id/value"));
        assertEquals(0l, ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id7]/value/value"));


        //and of course the DV_ORDINAL and DV_CODED_TEXT should be constructed correctly, with the correct numeric respectively a textual value
        assertEquals(0l, ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id7]/value/value"));
        assertEquals("Option 1", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id6]/value/value"));


        evaluate = ruleEvaluation.evaluate(ruleEvaluation.getRMRoot(), archetype.getRules().getRules());
        for(AssertionResult result:evaluate.getAssertionResults()) {
            assertTrue(result.getResult());
        }

    }

    @Test
    public void constructOnlyNecessaryStructure() throws Exception {
        archetype = parser.parse(ParsedRulesEvaluationTest.class.getResourceAsStream("construct_only_necessary_structure.adls"));
        RuleEvaluation<Locatable> ruleEvaluation = getRuleEvaluation();

        Locatable root = rmObjectCreator.create(archetype.getDefinition());
        EvaluationResult evaluate = ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        assertEquals("there must be three values that must be set", 1, evaluate.getSetPathValues().size());

        //assert that paths must be set to specific values
        assertEquals("test string", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id5]/value/value").getValue());
        assertEquals(null, evaluate.getSetPathValues().get("d/ata[id2]/events[id3]/data[id4]/items[id6]/value"));

        //now assert that the RM Object cloned by rule evaluation has been modified with the new values for further evaluation
        assertEquals("test string", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id5]/value/value"));
        assertEquals(null, ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id6]/value"));

        evaluate = ruleEvaluation.evaluate(ruleEvaluation.getRMRoot(), archetype.getRules().getRules());
        for(AssertionResult result:evaluate.getAssertionResults()) {
            assertTrue(result.getResult());
        }

    }

    @Test
    public void constructArchetypeSlot() throws Exception {
        archetype = parser.parse(ParsedRulesEvaluationTest.class.getResourceAsStream("construct_archetype_slot.adls"));
        RuleEvaluation<Locatable> ruleEvaluation = getRuleEvaluation();

        // Construct the rmObject, containing an empty ArchetypeSlot
        Locatable root = rmObjectCreator.create(archetype.getDefinition());

        // Evaluate the rmObject and its rules.
        EvaluationResult evaluate = ruleEvaluation.evaluate(root, archetype.getRules().getRules());

        // Assert the evaluation result, it should contain the element that has to be set to a specific value.
        assertEquals(1, evaluate.getSetPathValues().size());
        assertEquals(
                "/openEHR-EHR-CLUSTER\\.some_archetype_option_a.v(\\d+\\.\\d+\\.\\d+)/",
                evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id14]/items[id19]/archetype_details/archetype_id/value").getValue());
    }

    private RuleEvaluation<Locatable> getRuleEvaluation() {
        return new RuleEvaluation<>(OpenEhrRmInfoLookup.getInstance(), archetype);
    }

}
