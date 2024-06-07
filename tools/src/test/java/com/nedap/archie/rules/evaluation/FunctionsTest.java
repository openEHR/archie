package com.nedap.archie.rules.evaluation;

import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.adlparser.modelconstraints.RMConstraintImposer;
import com.nedap.archie.aom.Archetype;
import org.openehr.rm.archetyped.Locatable;
import org.openehr.rm.composition.Observation;
import org.openehr.rm.datastructures.Element;
import org.openehr.rm.datastructures.Item;
import org.openehr.rm.datastructures.ItemTree;
import org.openehr.rm.datavalues.quantity.DvQuantity;
import com.nedap.archie.openehr.rminfo.OpenEhrRmInfoLookup;
import com.nedap.archie.rules.PrimitiveType;
import com.nedap.archie.testutil.TestUtil;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by pieter.bos on 06/04/2017.
 */
public class FunctionsTest {

    private ADLParser parser;
    private Archetype archetype;

    @Before
    public void setup() {
        parser = new ADLParser(new RMConstraintImposer());
    }

    @Test
    public void min() throws Exception {
        archetype = parser.parse(ParsedRulesEvaluationTest.class.getResourceAsStream("functions.adls"));
        assertTrue(parser.getErrors().hasNoErrors());
        System.out.println(archetype);
        RuleEvaluation<Observation> ruleEvaluation = getRuleEvaluation();
        Observation root = new Observation();
        ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        ValueList min = ruleEvaluation.getVariableMap().get("min");
        assertEquals(PrimitiveType.Real, min.getType());
        assertEquals("min should work", 3.0, (Double) min.getValues().get(0).getValue(), 0.001);
    }

    @Test
    public void max() throws Exception {
        archetype = parser.parse(ParsedRulesEvaluationTest.class.getResourceAsStream("functions.adls"));
        assertTrue(parser.getErrors().hasNoErrors());
        System.out.println(archetype);
        RuleEvaluation<Observation> ruleEvaluation = getRuleEvaluation();
        Observation root = new Observation();
        ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        ValueList max = ruleEvaluation.getVariableMap().get("max");
        assertEquals(PrimitiveType.Real, max.getType());
        assertEquals("max should work", 2.0, (Double) max.getValues().get(0).getValue(), 0.001);
    }

    private <T> RuleEvaluation<T> getRuleEvaluation() {
        return new RuleEvaluation<>(OpenEhrRmInfoLookup.getInstance(), archetype);
    }

    @Test
    public void flatSum() throws Exception {
        archetype = parser.parse(ParsedRulesEvaluationTest.class.getResourceAsStream("functions.adls"));
        assertTrue(parser.getErrors().hasNoErrors());
        System.out.println(archetype);
        RuleEvaluation<Locatable> ruleEvaluation = getRuleEvaluation();

        Locatable rmObject = (Locatable) new TestUtil(OpenEhrRmInfoLookup.getInstance()).constructEmptyRMObject(archetype.getDefinition());
        DvQuantity quantity = (DvQuantity) rmObject.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id5]/value");
        quantity.setMagnitude(65d);

        quantity = (DvQuantity) rmObject.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id6]/value");
        quantity.setMagnitude(21d);

        // Add an extra id5 element
        Element elem = new Element();
        elem.setArchetypeNodeId("id5");
        quantity = new DvQuantity();
        quantity.setMagnitude(33.3);
        elem.setValue(quantity);

        ((ItemTree) rmObject.itemAtPath("/data[id2]/events[id3]/data[id4]")).addItem(elem);

        ruleEvaluation.evaluate(rmObject, archetype.getRules().getRules());
        ValueList flatSum = ruleEvaluation.getVariableMap().get("flat_sum");
        assertEquals(PrimitiveType.Real, flatSum.getType());
        assertEquals("flat_sum should work", 122.6, (Double) flatSum.getValues().get(0).getValue(), 0.001);
    }



    @Test
    public void sum() throws Exception {
        archetype = parser.parse(ParsedRulesEvaluationTest.class.getResourceAsStream("functions.adls"));
        assertTrue(parser.getErrors().hasNoErrors());
        System.out.println(archetype);
        RuleEvaluation<Locatable> ruleEvaluation = getRuleEvaluation();

        Locatable rmObject = (Locatable) new TestUtil(OpenEhrRmInfoLookup.getInstance()).constructEmptyRMObject(archetype.getDefinition());
        DvQuantity quantity = (DvQuantity) rmObject.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id5]/value");
        quantity.setMagnitude(65d);

        quantity = (DvQuantity) rmObject.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id6]/value");
        quantity.setMagnitude(21d);

        ruleEvaluation.evaluate(rmObject, archetype.getRules().getRules());
        ValueList sum = ruleEvaluation.getVariableMap().get("sum");
        assertEquals(PrimitiveType.Real, sum.getType());
        assertEquals("sum should work", 65+21+3.3, (Double) sum.getValues().get(0).getValue(), 0.001);
    }

    @Test
    public void mean() throws Exception {
        archetype = parser.parse(ParsedRulesEvaluationTest.class.getResourceAsStream("functions.adls"));
        assertTrue(parser.getErrors().hasNoErrors());
        System.out.println(archetype);
        RuleEvaluation<Locatable> ruleEvaluation = getRuleEvaluation();

        Locatable rmObject = (Locatable) new TestUtil(OpenEhrRmInfoLookup.getInstance()).constructEmptyRMObject(archetype.getDefinition());
        DvQuantity quantity = (DvQuantity) rmObject.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id5]/value");
        quantity.setMagnitude(65d);

        quantity = (DvQuantity) rmObject.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id6]/value");
        quantity.setMagnitude(21d);

        ruleEvaluation.evaluate(rmObject, archetype.getRules().getRules());
        ValueList mean = ruleEvaluation.getVariableMap().get("mean");
        assertEquals(PrimitiveType.Real, mean.getType());
        assertEquals("mean should work", (65+21+3.3)/3, (Double) mean.getValues().get(0).getValue(), 0.001);
    }
    @Test
    public void valueWhenUndefined() throws Exception {
        archetype = parser.parse(ParsedRulesEvaluationTest.class.getResourceAsStream("functions.adls"));
        assertTrue(parser.getErrors().hasNoErrors());

        RuleEvaluation<Observation> ruleEvaluation = getRuleEvaluation();
        Observation root = new Observation();
        ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        ValueList valueWhenUndefined = ruleEvaluation.getVariableMap().get("value_when_undefined");
        assertEquals(PrimitiveType.Real, valueWhenUndefined.getType());
        assertEquals("value when undefined should be set", 10.0d, (Double) valueWhenUndefined.getObject(0), 0.00001d);
    }

    @Test
    public void valueWhenUndefinedWithValue() throws Exception {
        archetype = parser.parse(ParsedRulesEvaluationTest.class.getResourceAsStream("functions.adls"));
        assertTrue(parser.getErrors().hasNoErrors());

        RuleEvaluation<Locatable> ruleEvaluation = getRuleEvaluation();
        Locatable rmObject = (Locatable) new TestUtil(OpenEhrRmInfoLookup.getInstance()).constructEmptyRMObject(archetype.getDefinition());
        DvQuantity quantity = (DvQuantity) rmObject.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id5]/value");
        quantity.setMagnitude(65d);

        ruleEvaluation.evaluate(rmObject, archetype.getRules().getRules());
        ValueList valueWhenUndefined = ruleEvaluation.getVariableMap().get("value_when_undefined");
        assertEquals(PrimitiveType.Real, valueWhenUndefined.getType());
        assertEquals("value when undefined should be set to original value", 65d, (Double) valueWhenUndefined.getObject(0), 0.00001d);
    }

    @Test
    public void round() throws Exception {
        archetype = parser.parse(ParsedRulesEvaluationTest.class.getResourceAsStream("functions.adls"));
        assertTrue(parser.getErrors().hasNoErrors());

        RuleEvaluation<Observation> ruleEvaluation = getRuleEvaluation();
        Observation root = new Observation();
        ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        ValueList roundedUp = ruleEvaluation.getVariableMap().get("round_up");
        ValueList roundedDown = ruleEvaluation.getVariableMap().get("round_down");
        ValueList roundedNonDecimal = ruleEvaluation.getVariableMap().get("round_non_decimal");
        assertEquals(PrimitiveType.Integer, roundedUp.getType());
        assertEquals(PrimitiveType.Integer, roundedDown.getType());
        assertEquals(PrimitiveType.Integer, roundedNonDecimal.getType());
        assertEquals("round should round up", 2L, (long) (Long) roundedUp.getValues().get(0).getValue());
        assertEquals("round should round down", 1L, (long) (Long) roundedDown.getValues().get(0).getValue());
        assertEquals("round non-decimal should also work", 3L, (long) (Long) roundedNonDecimal.getValues().get(0).getValue());
    }

    @Test
    public void roundMultiple() throws Exception {
        archetype = parser.parse(ParsedRulesEvaluationTest.class.getResourceAsStream("functions.adls"));
        assertTrue(parser.getErrors().hasNoErrors());

        Locatable rmObject = (Locatable) new TestUtil(OpenEhrRmInfoLookup.getInstance()).constructEmptyRMObject(archetype.getDefinition());
        ItemTree itemTree = (ItemTree) rmObject.itemAtPath("/data[id2]/events[id3]/data[id4]");
        Item item = (Item) itemTree.itemAtPath("/items[id5]");
        itemTree.addItem( (Item) item.clone() );

        DvQuantity quantity1 = (DvQuantity) itemTree.itemAtPath("/items[id5,1]/value");
        DvQuantity quantity2 = (DvQuantity) itemTree.itemAtPath("/items[id5,4]/value");
        quantity1.setMagnitude(65.4);
        quantity2.setMagnitude(45.6);

        RuleEvaluation<Locatable> ruleEvaluation = getRuleEvaluation();
        ruleEvaluation.evaluate(rmObject, archetype.getRules().getRules());
        ValueList roundMultiple = ruleEvaluation.getVariableMap().get("round_multiple");
        assertEquals(PrimitiveType.Integer, roundMultiple.getType());
        assertEquals("rounds each value in a value list", 2, roundMultiple.getValues().size());
        assertEquals("first value is rounded", 65L, (long) (Long) roundMultiple.getValues().get(0).getValue());
        assertEquals("second value is rounded", 46L, (long) (Long) roundMultiple.getValues().get(1).getValue());
    }

    @Test
    public void ceil() throws Exception {
        archetype = parser.parse(ParsedRulesEvaluationTest.class.getResourceAsStream("functions.adls"));
        assertTrue(parser.getErrors().hasNoErrors());

        RuleEvaluation<Locatable> ruleEvaluation = getRuleEvaluation();
        Locatable rmObject = (Locatable) new TestUtil(OpenEhrRmInfoLookup.getInstance()).constructEmptyRMObject(archetype.getDefinition());
        DvQuantity quantity = (DvQuantity) rmObject.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id5]/value");
        quantity.setMagnitude(26.1);

        ruleEvaluation.evaluate(rmObject, archetype.getRules().getRules());
        ValueList ceilPath = ruleEvaluation.getVariableMap().get("ceil_path");
        assertEquals(PrimitiveType.Integer, ceilPath.getType());
        assertEquals("ceil should round up", 14L, ceilPath.getValues().get(0).getValue());
    }

    @Test
    public void floor() throws Exception {
        archetype = parser.parse(ParsedRulesEvaluationTest.class.getResourceAsStream("functions.adls"));
        assertTrue(parser.getErrors().hasNoErrors());

        RuleEvaluation<Locatable> ruleEvaluation = getRuleEvaluation();
        Locatable rmObject = (Locatable) new TestUtil(OpenEhrRmInfoLookup.getInstance()).constructEmptyRMObject(archetype.getDefinition());
        DvQuantity quantity = (DvQuantity) rmObject.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id5]/value");
        quantity.setMagnitude(27.9);

        ruleEvaluation.evaluate(rmObject, archetype.getRules().getRules());
        ValueList floorPath = ruleEvaluation.getVariableMap().get("floor_path");
        assertEquals(PrimitiveType.Integer, floorPath.getType());
        assertEquals("floor should round down", 13L, floorPath.getValues().get(0).getValue());
    }
}
