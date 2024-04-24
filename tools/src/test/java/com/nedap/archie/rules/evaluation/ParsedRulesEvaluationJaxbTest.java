package com.nedap.archie.rules.evaluation;

import org.openehr.rm.archetyped.Pathable;
import org.openehr.rm.datavalues.quantity.DvQuantity;
import com.nedap.archie.openehr.rminfo.OpenEhrRmInfoLookup;
import com.nedap.archie.openehr.serialisation.xml.OpenEhrRmJAXBUtil;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * runs all the tests in ParsedRuledEvaluationTest, but with JAXB intead of RMPathQuery
 * contains some overridden tests because the generated paths are slightly different in JAXB and RM, but they are
 * functionally equivalent
 */
public class ParsedRulesEvaluationJaxbTest extends ParsedRulesEvaluationTest {

    @Override
    RuleEvaluation<Pathable> getRuleEvaluation() {
        return new RuleEvaluation<>(OpenEhrRmInfoLookup.getInstance(), OpenEhrRmJAXBUtil.getArchieJAXBContext(), getArchetype());
    }

    @Override
    public void notExistsMixed() throws Exception {

        parse("not_exists.adls");
        RuleEvaluation<Pathable> ruleEvaluation = getRuleEvaluation();

        Pathable root = constructTwoBloodPressureObservationsOneEmptySystolic();

        EvaluationResult evaluationResult = ruleEvaluation.evaluate(root, getArchetype().getRules().getRules());
        assertEquals(3, evaluationResult.getAssertionResults().size());
        for(AssertionResult assertionResult:evaluationResult.getAssertionResults()) {
            assertFalse(assertionResult.getResult());
        }

        assertEquals(0, evaluationResult.getPathsThatMustExist().size());
        assertEquals(5, evaluationResult.getPathsThatMustNotExist().size());
        assertTrue(evaluationResult.getPathsThatMustNotExist().contains("/data[id2]/events[id3,1]/data[id4]/items[id5,1]/value/magnitude"));
        assertTrue(evaluationResult.getPathsThatMustNotExist().contains("/data[id2]/events[id3,2]/data[id4]/items[id6,2]/value/magnitude"));
        assertEquals(0, evaluationResult.getSetPathValues().size());


    }

    @Override
    public void modelReferences() throws Exception {
        parse("modelreferences.adls");
        RuleEvaluation<Pathable> ruleEvaluation = getRuleEvaluation();

        Pathable root = (Pathable) testUtil.constructEmptyRMObject(getArchetype().getDefinition());

        DvQuantity quantity = (DvQuantity) root.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id5]/value[id13]");
        quantity.setMagnitude(65d);

        ruleEvaluation.evaluate(root, getArchetype().getRules().getRules());

        assertEquals(65d, (Double) ruleEvaluation.getVariableMap().get("arithmetic_test").getObject(0), 0.001d);

        List<AssertionResult> assertionResults = ruleEvaluation.getEvaluationResult().getAssertionResults();
        assertEquals("one assertion should have been checked", 1, assertionResults.size());
        AssertionResult result = assertionResults.get(0);

        assertEquals("the assertion should have succeeded", true, result.getResult());
        assertEquals("the assertion tag should be correct", "blood_pressure_valid", result.getTag());
        assertEquals(1, result.getRawResult().getPaths(0).size());
        assertEquals("/data[id2]/events[id3]/data[id4]/items[id5]/value/magnitude", result.getRawResult().getPaths(0).get(0));

    }

    @Override
    public void booleanConstraint() throws Exception {
        parse("matches.adls");

        RuleEvaluation<Pathable> ruleEvaluation = getRuleEvaluation();

        Pathable root = (Pathable) testUtil.constructEmptyRMObject(archetype.getDefinition());

        DvQuantity quantity = (DvQuantity) root.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id5]/value[id13]");
        quantity.setMagnitude(40d);

        ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        assertEquals(false, ruleEvaluation.getVariableMap().get("extended_validity").getObject(0));
        assertEquals(false, ruleEvaluation.getVariableMap().get("extended_validity_2").getObject(0));

        ValueList extendedValidity = ruleEvaluation.getVariableMap().get("extended_validity");
        ValueList extendedValidity2 = ruleEvaluation.getVariableMap().get("extended_validity_2");
        ValueList variableMatches = ruleEvaluation.getVariableMap().get("variable_matches");
        assertEquals(false, extendedValidity.getObject(0));
        assertEquals(false, extendedValidity2.getObject(0));
        assertEquals(false, variableMatches.getObject(0));
        assertEquals("/data[id2]/events[id3]/data[id4]/items[id5]/value/magnitude", extendedValidity.getPaths(0).get(0));
        assertEquals("/data[id2]/events[id3]/data[id4]/items[id5]/value/magnitude", extendedValidity2.getPaths(0).get(0));
        assertEquals("/data[id2]/events[id3]/data[id4]/items[id5]/value/magnitude", variableMatches.getPaths(0).get(0));
        quantity.setMagnitude(20d);

        ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        extendedValidity = ruleEvaluation.getVariableMap().get("extended_validity");
        assertEquals(true, extendedValidity.getObject(0));
        assertEquals("/data[id2]/events[id3]/data[id4]/items[id5]/value/magnitude", extendedValidity.getPaths(0).get(0));

        extendedValidity2 = ruleEvaluation.getVariableMap().get("extended_validity_2");
        assertEquals(true, extendedValidity2.getObject(0));
        assertEquals("/data[id2]/events[id3]/data[id4]/items[id5]/value/magnitude", extendedValidity2.getPaths(0).get(0));

        variableMatches = ruleEvaluation.getVariableMap().get("variable_matches");
        assertEquals(true, variableMatches.getObject(0));
        assertEquals("/data[id2]/events[id3]/data[id4]/items[id5]/value/magnitude", variableMatches.getPaths(0).get(0));

        quantity.setMagnitude(0d);

        ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        extendedValidity = ruleEvaluation.getVariableMap().get("extended_validity");
        assertEquals(true, extendedValidity.getObject(0));
        assertEquals("/data[id2]/events[id3]/data[id4]/items[id5]/value/magnitude", extendedValidity.getPaths(0).get(0));

        extendedValidity2 = ruleEvaluation.getVariableMap().get("extended_validity_2");
        assertEquals(false, extendedValidity2.getObject(0));
        assertEquals("/data[id2]/events[id3]/data[id4]/items[id5]/value/magnitude", extendedValidity2.getPaths(0).get(0));

        variableMatches = ruleEvaluation.getVariableMap().get("variable_matches");
        assertEquals(true, variableMatches.getObject(0));
        assertEquals("/data[id2]/events[id3]/data[id4]/items[id5]/value/magnitude", variableMatches.getPaths(0).get(0));

    }

    @Test
    public void existsFailed() throws Exception {
        archetype = parser.parse(ParsedRulesEvaluationTest.class.getResourceAsStream("exists.adls"));
        RuleEvaluation<Pathable> ruleEvaluation = getRuleEvaluation();

        Pathable root = (Pathable) testUtil.constructEmptyRMObject(archetype.getDefinition());

        EvaluationResult evaluationResult = ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        assertEquals(3, evaluationResult.getAssertionResults().size());
        for(AssertionResult assertionResult:evaluationResult.getAssertionResults()) {
            assertFalse(assertionResult.getResult());
        }

        assertEquals(3, evaluationResult.getPathsThatMustExist().size());
        assertEquals("/data[id2]/events[id3]/data[id4]/items[id5]/value/magnitude", evaluationResult.getPathsThatMustExist().get(0));
        assertEquals("/data[id2]/events[id3]/data[id4]/items[id6]/value/magnitude", evaluationResult.getPathsThatMustExist().get(1));
        assertEquals("/data[id2]/events[id3]/data[id4]/items[id5]/value/magnitude", evaluationResult.getPathsThatMustExist().get(2));
        assertEquals(0, evaluationResult.getPathsThatMustNotExist().size());
        assertEquals(0, evaluationResult.getSetPathValues().size());

    }
}
