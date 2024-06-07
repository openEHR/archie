package com.nedap.archie.rules.evaluation;

import com.nedap.archie.adlparser.ADLParseException;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.creation.ExampleJsonInstanceGenerator;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.FlattenerConfiguration;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.openehr.serialisation.json.OpenEhrRmJacksonUtil;
import org.openehr.rm.archetyped.Pathable;
import org.openehr.rm.composition.Observation;
import org.openehr.rm.datastructures.Cluster;
import org.openehr.rm.datastructures.Element;
import org.openehr.rm.datatypes.CodePhrase;
import org.openehr.rm.datavalues.DvCodedText;
import org.openehr.rm.datavalues.quantity.DvQuantity;
import org.openehr.rm.support.identification.TerminologyId;
import com.nedap.archie.openehr.rminfo.OpenEhrRmInfoLookup;
import com.nedap.archie.rules.BinaryOperator;
import com.nedap.archie.rules.ExpressionVariable;
import com.nedap.archie.rules.RuleStatement;
import com.nedap.archie.rules.VariableDeclaration;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.openehr.referencemodels.AllMetaModelsInitialiser;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.*;

/**
 * Created by pieter.bos on 01/04/16.
 */
public abstract class ParsedRulesEvaluationTest {

    ADLParser parser;
    Archetype archetype;

    TestUtil testUtil;

    @Before
    public void setup() {
        testUtil = new TestUtil(OpenEhrRmInfoLookup.getInstance());
        parser = new ADLParser(AllMetaModelsInitialiser.getMetaModels());
    }

    public Archetype getArchetype() {
        return archetype;
    }

    @Test
    public void precedenceOverride() throws Exception {
        parse("simplearithmetic.adls");

        ExpressionVariable booleanExtendedTest = (ExpressionVariable) getVariableDeclarationByName(archetype, "boolean_extended_test");
        BinaryOperator operator = (BinaryOperator) booleanExtendedTest.getExpression();
        assertTrue(operator.getLeftOperand().isPrecedenceOverridden());
        assertFalse(operator.getRightOperand().isPrecedenceOverridden());

        ExpressionVariable arithmeticParentheses = (ExpressionVariable) getVariableDeclarationByName(archetype, "arithmetic_parentheses");
        BinaryOperator arithmeticOperator = (BinaryOperator) arithmeticParentheses.getExpression();
        assertTrue(arithmeticOperator.getLeftOperand().isPrecedenceOverridden());
        assertFalse(arithmeticOperator.getRightOperand().isPrecedenceOverridden());

    }

    Archetype parse(String filename) throws IOException, ADLParseException {
        archetype = parser.parse(ParsedRulesEvaluationTest.class.getResourceAsStream(filename));
        assertTrue(parser.getErrors().toString(), parser.getErrors().hasNoErrors());
        return archetype;
    }

    @Test
    public void simpleArithmetic() throws Exception {
        parse("simplearithmetic.adls");
        assertTrue(parser.getErrors().hasNoErrors());
        RuleEvaluation<Pathable> ruleEvaluation = getRuleEvaluation();
        Observation root = new Observation();
        ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        VariableMap variables = ruleEvaluation.getVariableMap();
        assertEquals(8l, variables.get("arithmetic_test").getObject(0));
        assertTrue(variables.get("arithmetic_test").getPaths(0).isEmpty());
        assertEquals(false, variables.get("boolean_false_test").getObject(0));
        assertTrue(variables.get("boolean_false_test").getPaths(0).isEmpty());
        assertEquals(true, variables.get("boolean_true_test").getObject(0));
        assertTrue(variables.get("boolean_true_test").getPaths(0).isEmpty());
        assertEquals(true, variables.get("boolean_extended_test").getObject(0));
        assertTrue(variables.get("boolean_extended_test").getPaths(0).isEmpty());
        assertEquals(true, variables.get("not_false").getObject(0));
        assertTrue(variables.get("not_false").getPaths(0).isEmpty());
        assertEquals(false, variables.get("not_not_not_true").getObject(0));
        assertTrue(variables.get("not_not_not_true").getPaths(0).isEmpty());
        assertEquals(3l, variables.get("variable_reference").getObject(0));
        assertTrue(variables.get("variable_reference").getPaths(0).isEmpty());
        assertEquals(25l, variables.get("arithmetic_parentheses").getObject(0));
        assertTrue(variables.get("arithmetic_parentheses").getPaths(0).isEmpty());

    }

    private VariableDeclaration getVariableDeclarationByName(Archetype archetype, String name) {
        for(RuleStatement statement:archetype.getRules().getRules()) {
            if(statement instanceof VariableDeclaration) {
                VariableDeclaration variable = (VariableDeclaration) statement;
                if(Objects.equals(variable.getName(), name)) {
                    return variable;
                }
            }
        }
        return null;
    }

      @Test
    public void modelReferences() throws Exception {
        parse("modelreferences.adls");
        RuleEvaluation<Pathable> ruleEvaluation = getRuleEvaluation();

        Pathable root = (Pathable) testUtil.constructEmptyRMObject(archetype.getDefinition());

        DvQuantity quantity = (DvQuantity) root.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id5]/value[id13]");
        quantity.setMagnitude(65d);

        ruleEvaluation.evaluate(root, archetype.getRules().getRules());

        assertEquals(65d, (Double) ruleEvaluation.getVariableMap().get("arithmetic_test").getObject(0), 0.001d);

        List<AssertionResult> assertionResults = ruleEvaluation.getEvaluationResult().getAssertionResults();
        assertEquals("one assertion should have been checked", 1, assertionResults.size());
        AssertionResult result = assertionResults.get(0);

        assertEquals("the assertion should have succeeded", true, result.getResult());
        assertEquals("the assertion tag should be correct", "blood_pressure_valid", result.getTag());
        assertEquals(1, result.getRawResult().getPaths(0).size());
        assertEquals("/data[id2]/events[id3, 1]/data[id4]/items[id5, 1]/value/magnitude", result.getRawResult().getPaths(0).get(0));

    }

    @Test
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
        assertEquals("/data[id2]/events[id3, 1]/data[id4]/items[id5, 1]/value/magnitude", extendedValidity.getPaths(0).get(0));
        assertEquals("/data[id2]/events[id3, 1]/data[id4]/items[id5, 1]/value/magnitude", extendedValidity2.getPaths(0).get(0));
        assertEquals("/data[id2]/events[id3, 1]/data[id4]/items[id5, 1]/value/magnitude", variableMatches.getPaths(0).get(0));
        quantity.setMagnitude(20d);

        ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        extendedValidity = ruleEvaluation.getVariableMap().get("extended_validity");
        assertEquals(true, extendedValidity.getObject(0));
        assertEquals("/data[id2]/events[id3, 1]/data[id4]/items[id5, 1]/value/magnitude", extendedValidity.getPaths(0).get(0));

        extendedValidity2 = ruleEvaluation.getVariableMap().get("extended_validity_2");
        assertEquals(true, extendedValidity2.getObject(0));
        assertEquals("/data[id2]/events[id3, 1]/data[id4]/items[id5, 1]/value/magnitude", extendedValidity2.getPaths(0).get(0));

        variableMatches = ruleEvaluation.getVariableMap().get("variable_matches");
        assertEquals(true, variableMatches.getObject(0));
        assertEquals("/data[id2]/events[id3, 1]/data[id4]/items[id5, 1]/value/magnitude", variableMatches.getPaths(0).get(0));

        quantity.setMagnitude(0d);

        ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        extendedValidity = ruleEvaluation.getVariableMap().get("extended_validity");
        assertEquals(true, extendedValidity.getObject(0));
        assertEquals("/data[id2]/events[id3, 1]/data[id4]/items[id5, 1]/value/magnitude", extendedValidity.getPaths(0).get(0));

        extendedValidity2 = ruleEvaluation.getVariableMap().get("extended_validity_2");
        assertEquals(false, extendedValidity2.getObject(0));
        assertEquals("/data[id2]/events[id3, 1]/data[id4]/items[id5, 1]/value/magnitude", extendedValidity2.getPaths(0).get(0));

        variableMatches = ruleEvaluation.getVariableMap().get("variable_matches");
        assertEquals(true, variableMatches.getObject(0));
        assertEquals("/data[id2]/events[id3, 1]/data[id4]/items[id5, 1]/value/magnitude", variableMatches.getPaths(0).get(0));

    }

    @Test
    public void multiValuedExpressions() throws Exception {
        parse("multiplicity.adls");
        RuleEvaluation<Pathable> ruleEvaluation = getRuleEvaluation();

        Pathable root = constructTwoBloodPressureObservations();

        ruleEvaluation.evaluate(root, archetype.getRules().getRules());

        List<AssertionResult> assertionResults = ruleEvaluation.getEvaluationResult().getAssertionResults();
        assertEquals("one assertion should have been checked", 1, assertionResults.size());
        AssertionResult result = assertionResults.get(0);

        assertEquals("the assertion should have succeeded", true, result.getResult());
        assertEquals("the assertion tag should be correct", "blood_pressure_valid", result.getTag());
    }

    @Test
    public void forAllExpression() throws Exception {
        parse("for_all.adls");
        RuleEvaluation<Pathable> ruleEvaluation = getRuleEvaluation();

        Pathable root = constructTwoBloodPressureObservations();

        ruleEvaluation.evaluate(root, archetype.getRules().getRules());

        List<AssertionResult> assertionResults = ruleEvaluation.getEvaluationResult().getAssertionResults();
        assertEquals("one assertion should have been checked", 1, assertionResults.size());
        AssertionResult result = assertionResults.get(0);

        assertEquals("the assertion should have succeeded", false, result.getResult());
        assertEquals("the assertion tag should be correct", "blood_pressure_valid", result.getTag());
    }

    public Pathable constructTwoBloodPressureObservations() {
        Pathable root = (Pathable) testUtil.constructEmptyRMObject(archetype.getDefinition());

        {
            DvQuantity systolic = (DvQuantity) root.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id5]/value[id13]");
            systolic.setMagnitude(76d);
            DvQuantity diastolic = (DvQuantity) root.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id6]/value[id14]");
            diastolic.setMagnitude(80d);
            //this is fine, because "blood_pressure_valid: $systolic > $diastolic - 5"
        }


        //add a second event
        {
            Pathable root2 = (Pathable) testUtil.constructEmptyRMObject(archetype.getDefinition());

            DvQuantity systolic = (DvQuantity) root2.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id5]/value[id13]");
            systolic.setMagnitude(60d);
            DvQuantity diastolic = (DvQuantity) root2.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id6]/value[id14]");
            diastolic.setMagnitude(80d);
            Observation observation = (Observation) root;
            Observation observation2 = (Observation) root2;
            observation.getData().addEvent(observation2.getData().getEvents().get(0));
            //a strange reading that will lead to a failure
        }
        return root;
    }

    public Pathable constructTwoBloodPressureObservationsOneEmptySystolic() {
        Pathable root = (Pathable) testUtil.constructEmptyRMObject(archetype.getDefinition());

        {
            DvQuantity systolic = (DvQuantity) root.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id5]/value[id13]");
            systolic.setMagnitude(120d);
            DvQuantity diastolic = (DvQuantity) root.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id6]/value[id14]");
            diastolic.setMagnitude(80d);
        }


        {
            Pathable root2 = (Pathable) testUtil.constructEmptyRMObject(archetype.getDefinition());

            DvQuantity systolic = (DvQuantity) root2.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id5]/value[id13]");
            //systolic.setMagnitude(60d);
            DvQuantity diastolic = (DvQuantity) root2.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id6]/value[id14]");
            diastolic.setMagnitude(80d);
            Observation observation = (Observation) root;
            Observation observation2 = (Observation) root2;
            observation.getData().addEvent(observation2.getData().getEvents().get(0));
        }
        return root;
    }

    public Pathable constructTwoBloodPressureObservationsOneEmptySystolicNoDiastolic() {
        Pathable root = (Pathable) testUtil.constructEmptyRMObject(archetype.getDefinition());

        {
            DvQuantity systolic = (DvQuantity) root.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id5]/value[id13]");
            systolic.setMagnitude(120d);
            DvQuantity diastolic = (DvQuantity) root.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id6]/value[id14]");
        }


        //add a second event
        {
            Pathable root2 = (Pathable) testUtil.constructEmptyRMObject(archetype.getDefinition());

            DvQuantity systolic = (DvQuantity) root2.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id5]/value[id13]");
            //systolic.setMagnitude(60d);
            DvQuantity diastolic = (DvQuantity) root2.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id6]/value[id14]");
            Observation observation = (Observation) root;
            Observation observation2 = (Observation) root2;
            observation.getData().addEvent(observation2.getData().getEvents().get(0));
        }
        return root;
    }

    @Test
    public void alreadyCorrectCalculatedPathValues() throws Exception {
        parse("calculated_path_values.adls");
        RuleEvaluation<Pathable> ruleEvaluation = getRuleEvaluation();

        Pathable root = (Pathable) testUtil.constructEmptyRMObject(archetype.getDefinition());
        DvQuantity systolic = (DvQuantity) root.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id5]/value[id13]");
        systolic.setMagnitude(100d);
        DvQuantity diastolic = (DvQuantity) root.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id6]/value[id14]");
        diastolic.setMagnitude(80d);

        DvQuantity something = (DvQuantity) root.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id7]/value");
        something.setMagnitude(20.0d);

        EvaluationResult evaluationResult = ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        assertEquals(1, evaluationResult.getAssertionResults().size());
        assertTrue(evaluationResult.getAssertionResults().get(0).getResult());
        assertEquals(1, evaluationResult.getSetPathValues().size());
        assertEquals(20.0d, (Double) evaluationResult.getSetPathValues().values().iterator().next().getValue(), 0.0001d);
    }


    @Test
    public void calculatedPathValues() throws Exception {
        parse("calculated_path_values.adls");
        RuleEvaluation<Pathable> ruleEvaluation = getRuleEvaluation();

        Pathable root = (Pathable) testUtil.constructEmptyRMObject(archetype.getDefinition());
        DvQuantity systolic = (DvQuantity) root.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id5]/value[id13]");
        systolic.setMagnitude(100d);
        DvQuantity diastolic = (DvQuantity) root.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id6]/value[id14]");
        diastolic.setMagnitude(80d);

        EvaluationResult evaluationResult = ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        assertEquals(1, evaluationResult.getAssertionResults().size());
        assertFalse(evaluationResult.getAssertionResults().get(0).getResult());
        assertEquals(1, evaluationResult.getSetPathValues().size());
        assertEquals(20.0d, (Double) evaluationResult.getSetPathValues().values().iterator().next().getValue(), 0.0001d);
    }

    @Test
    public void calculatedPathValuesWithNulls1() throws Exception {
        parse("calculated_path_values.adls");
        RuleEvaluation<Pathable> ruleEvaluation = getRuleEvaluation();

        Pathable root = (Pathable) testUtil.constructEmptyRMObject(archetype.getDefinition());
        DvQuantity systolic = (DvQuantity) root.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id5]/value[id13]");
        systolic.setMagnitude(80d);
        DvQuantity diastolic = (DvQuantity) root.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id6]/value[id14]");
        diastolic.setMagnitude(null);

        EvaluationResult evaluationResult = ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        assertEquals(1, evaluationResult.getAssertionResults().size());
        assertFalse(evaluationResult.getAssertionResults().get(0).getResult());
        assertEquals(1, evaluationResult.getSetPathValues().size());
        assertEquals(null, evaluationResult.getSetPathValues().values().iterator().next().getValue());
    }

    @Test
    public void calculatedPathValuesWithNulls2() throws Exception {
        parse("calculated_path_values.adls");
        RuleEvaluation<Pathable> ruleEvaluation = getRuleEvaluation();

        Pathable root = (Pathable) testUtil.constructEmptyRMObject(archetype.getDefinition());
        DvQuantity systolic = (DvQuantity) root.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id5]/value[id13]");
        systolic.setMagnitude(null);
        DvQuantity diastolic = (DvQuantity) root.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id6]/value[id14]");
        diastolic.setMagnitude(80d);

        EvaluationResult evaluationResult = ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        assertEquals(1, evaluationResult.getAssertionResults().size());
        assertFalse(evaluationResult.getAssertionResults().get(0).getResult());
        assertEquals(1, evaluationResult.getSetPathValues().size());
        assertEquals(null, evaluationResult.getSetPathValues().values().iterator().next().getValue());
    }

    @Test
    public void calculatedPathValuesWithNulls3() throws Exception {
        parse("extended_calculated_path_values.adls");
        RuleEvaluation<Pathable> ruleEvaluation = getRuleEvaluation();

        Pathable root = (Pathable) testUtil.constructEmptyRMObject(archetype.getDefinition());
        Element systolic = (Element) root.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id5]");
        systolic.setValue(null);
        Element diastolic = (Element) root.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id6]");
        diastolic.setValue(null);
        DvQuantity value3 = (DvQuantity) root.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id7]/value");
        value3.setMagnitude(80d);

        EvaluationResult evaluationResult = ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        assertEquals(1, evaluationResult.getAssertionResults().size());
        assertFalse(evaluationResult.getAssertionResults().get(0).getResult());
        assertEquals(1, evaluationResult.getSetPathValues().size());
        assertEquals(null, evaluationResult.getSetPathValues().values().iterator().next().getValue());
    }

    /**
     * Calculate a path value, then use that calculated path value in another rule to calculate more
     * Tests that calculated values are immediately set in the RMObject for further calculation correctly
     * @throws Exception
     */
    @Test
    public void calculatedPathValues2() throws Exception {
        parse("calculated_path_values_2.adls");
        RuleEvaluation<Pathable> ruleEvaluation = getRuleEvaluation();

        Pathable root = (Pathable) testUtil.constructEmptyRMObject(archetype.getDefinition());
        DvQuantity systolic = (DvQuantity) root.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id5]/value[id13]");
        systolic.setMagnitude(100d);
        DvQuantity diastolic = (DvQuantity) root.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id6]/value[id14]");
        diastolic.setMagnitude(80d);

        EvaluationResult evaluationResult = ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        assertEquals(2, evaluationResult.getAssertionResults().size());
        assertFalse(evaluationResult.getAssertionResults().get(0).getResult());
        assertFalse(evaluationResult.getAssertionResults().get(1).getResult());
        assertEquals(2, evaluationResult.getSetPathValues().size());
        Iterator<Value<?>> iterator = evaluationResult.getSetPathValues().values().iterator();
        assertEquals(20.0d, (Double) iterator.next().getValue(), 0.0001d);
        assertEquals(23.0d, (Double) iterator.next().getValue(), 0.0001d);
    }


    @Test
    public void forAllCalculatedValues() throws Exception {
        parse("for_all_calculated_path_values.adls");
        RuleEvaluation<Pathable> ruleEvaluation = getRuleEvaluation();

        Pathable root = constructTwoBloodPressureObservations();

        //-4 and -20 (sorry about the strange input)

        EvaluationResult evaluationResult = ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        assertEquals(1, evaluationResult.getAssertionResults().size());
        assertFalse(evaluationResult.getAssertionResults().get(0).getResult());
        assertEquals(2, evaluationResult.getSetPathValues().size());
        Iterator<Value<?>> setValuesIterator = evaluationResult.getSetPathValues().values().iterator();
        assertEquals(-4.0d, (Double) setValuesIterator.next().getValue(), 0.0001d);
        assertEquals(-20.0d, (Double) setValuesIterator.next().getValue(), 0.0001d);
    }

    @Test
    public void existsSucceeded() throws Exception {
        parse("exists.adls");
        RuleEvaluation<Pathable> ruleEvaluation = getRuleEvaluation();

        Pathable root = constructTwoBloodPressureObservations();

        EvaluationResult evaluationResult = ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        assertEquals(3, evaluationResult.getAssertionResults().size());
        for(AssertionResult assertionResult:evaluationResult.getAssertionResults()) {
            assertTrue(assertionResult.getResult());//TODO: check paths that caused this
        }

        assertEquals(4, evaluationResult.getPathsThatMustExist().size());
        assertEquals(0, evaluationResult.getPathsThatMustNotExist().size());
        assertEquals(0, evaluationResult.getSetPathValues().size());

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
        assertEquals("/data[id2]/events[id3, 1]/data[id4]/items[id5]/value/magnitude", evaluationResult.getPathsThatMustExist().get(2));
        assertEquals(0, evaluationResult.getPathsThatMustNotExist().size());
        assertEquals(0, evaluationResult.getSetPathValues().size());

    }

    @Test
    public void notExistsSucceeded() throws Exception {
        parse("not_exists.adls");
        RuleEvaluation<Pathable> ruleEvaluation = getRuleEvaluation();

        Pathable root = (Pathable) testUtil.constructEmptyRMObject(archetype.getDefinition());

        EvaluationResult evaluationResult = ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        assertEquals(3, evaluationResult.getAssertionResults().size());
        for(AssertionResult assertionResult:evaluationResult.getAssertionResults()) {
            assertTrue(assertionResult.getResult());
        }

        assertEquals(0, evaluationResult.getPathsThatMustExist().size());
        //the paths should not be creatable, so they should still be present in the result
        assertEquals(3, evaluationResult.getPathsThatMustNotExist().size());
        assertEquals(0, evaluationResult.getSetPathValues().size());

    }

    @Test
    public void notExistsFailed() throws Exception {
        parse("not_exists.adls");
        RuleEvaluation<Pathable> ruleEvaluation = getRuleEvaluation();

        Pathable root = constructTwoBloodPressureObservations();

        EvaluationResult evaluationResult = ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        assertEquals(3, evaluationResult.getAssertionResults().size());
        for(AssertionResult assertionResult:evaluationResult.getAssertionResults()) {
            assertFalse(assertionResult.getResult());
        }

        assertEquals(0, evaluationResult.getPathsThatMustExist().size());
        assertEquals(6, evaluationResult.getPathsThatMustNotExist().size());
        assertEquals(0, evaluationResult.getSetPathValues().size());

    }

    @Test
    public void existsMixed() throws Exception {
        parse("exists.adls");
        RuleEvaluation<Pathable> ruleEvaluation = getRuleEvaluation();

        Pathable root = constructTwoBloodPressureObservationsOneEmptySystolic();

        EvaluationResult evaluationResult = ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        assertEquals(3, evaluationResult.getAssertionResults().size());
        assertTrue(evaluationResult.getAssertionResults().get(0).getResult());//exists systolic. At least one true means true, so true
        assertTrue(evaluationResult.getAssertionResults().get(1).getResult()); //exists diastolic. This is always true
        assertFalse(evaluationResult.getAssertionResults().get(2).getResult()); //for all exists systolic - this is not true, so failed


        //four paths evaluated to exist, so ALL of them will be added to the list
        assertEquals(4, evaluationResult.getPathsThatMustExist().size());


        assertEquals(0, evaluationResult.getPathsThatMustNotExist().size());
        //this is the most specific path we can construct to the missing node, using the for_all variable context
        //or should this actually be /data[id4,1]/items[id5,1]/value[1]/magnitude, because the structure does exist?
        //that last thing might be hard to do.
        assertTrue(evaluationResult.getPathsThatMustExist().contains("/data[id2]/events[id3]/data[id4]/items[id5]/value/magnitude"));
        assertEquals(0, evaluationResult.getSetPathValues().size());

    }

    @Test
    public void notExistsMixed() throws Exception {
        parse("not_exists.adls");
        RuleEvaluation<Pathable> ruleEvaluation = getRuleEvaluation();

        Pathable root = constructTwoBloodPressureObservationsOneEmptySystolic();

        EvaluationResult evaluationResult = ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        assertEquals(3, evaluationResult.getAssertionResults().size());
        for(AssertionResult assertionResult:evaluationResult.getAssertionResults()) {
            assertFalse(assertionResult.getResult());
        }

        assertEquals(0, evaluationResult.getPathsThatMustExist().size());
        assertEquals(5, evaluationResult.getPathsThatMustNotExist().size());
        assertTrue(evaluationResult.getPathsThatMustNotExist().contains("/data[id2]/events[id3, 1]/data[id4]/items[id5, 1]/value/magnitude"));
        assertTrue(evaluationResult.getPathsThatMustNotExist().contains("/data[id2]/events[id3, 2]/data[id4]/items[id6, 2]/value/magnitude"));
        assertEquals(0, evaluationResult.getSetPathValues().size());

    }

    @Test
    public void implies() throws Exception {
        parse("implies.adls");
        RuleEvaluation<Pathable> ruleEvaluation = getRuleEvaluation();

        Pathable root = constructTwoBloodPressureObservationsOneEmptySystolicNoDiastolic();

        EvaluationResult evaluationResult = ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        assertEquals(3, evaluationResult.getAssertionResults().size());
        for(AssertionResult assertionResult:evaluationResult.getAssertionResults()) {
            assertFalse(assertionResult.getResult());
        }

        assertEquals(2, evaluationResult.getPathsThatMustExist().size());//one from the non-specific exists operator, one from the for_all that is very specific indeed
        assertTrue(evaluationResult.getPathsThatMustExist().contains("/data[id2]/events[id3]/data[id4]/items[id6]/value/magnitude"));
        assertTrue(evaluationResult.getPathsThatMustExist().contains("/data[id2]/events[id3]/data[id4]/items[id6]/value/magnitude"));
        assertEquals(0, evaluationResult.getPathsThatMustNotExist().size());
        assertEquals(1, evaluationResult.getSetPathValues().size());
    }

    @Test
    public void impliesEvaluatesToNull() throws Exception {
        parse("implies.adls");
        RuleEvaluation<Pathable> ruleEvaluation = getRuleEvaluation();

        Pathable root = (Pathable) testUtil.constructEmptyRMObject(archetype.getDefinition());

        EvaluationResult evaluationResult = ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        assertEquals(3, evaluationResult.getAssertionResults().size());
        for(AssertionResult assertionResult:evaluationResult.getAssertionResults()) {
            assertTrue(assertionResult + " failed and it should not", assertionResult.getResult()); //all three assertions should not lead to validation errors
        }

        //no paths must exist, not exist or set
        assertEquals(0, evaluationResult.getPathsThatMustExist().size());
        assertEquals(0, evaluationResult.getPathsThatMustNotExist().size());
        assertEquals(0, evaluationResult.getSetPathValues().size());
    }

    @Test
    public void orNoData() throws Exception {
        parse("or.adls");
        RuleEvaluation<Pathable> ruleEvaluation = getRuleEvaluation();

        Observation root = (Observation) testUtil.constructEmptyRMObject(archetype.getDefinition());
        // Simulate using evaluate without providing any data
        root.getData().getEvents().get(0).setData(null);

        EvaluationResult evaluationResult = ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        assertEquals(2, evaluationResult.getAssertionResults().size());
        assertTrue(evaluationResult.getAssertionResults().get(0).getResult());
        assertTrue(evaluationResult.getAssertionResults().get(1).getResult());

        // Check that when no answer is given, both paths should not exist
        assertEquals(2, evaluationResult.getPathsThatMustNotExist().size());
        assertEquals("/data[id2]/events[id3]/data[id4]/items[id7]", evaluationResult.getPathsThatMustNotExist().get(0));
        assertEquals("/data[id2]/events[id3]/data[id4]/items[id9]", evaluationResult.getPathsThatMustNotExist().get(1));
    }

    @Test
    public void orSelectPathNotExists() throws Exception {
        parse("or.adls");
        RuleEvaluation<Pathable> ruleEvaluation = getRuleEvaluation();

        Observation root = (Observation) testUtil.constructEmptyRMObject(archetype.getDefinition());
        // Simulate selecting the option that should hide the elements
        root.getData().getEvents().get(0).getData().getItems().remove(2);
        root.getData().getEvents().get(0).getData().getItems().remove(1);
        ((DvCodedText) ((Element) root.getData().getEvents().get(0).getData().getItems().get(0)).getValue()).setDefiningCode(new CodePhrase(new TerminologyId("ac1"), "at2"));
        ((DvCodedText) ((Element) root.getData().getEvents().get(0).getData().getItems().get(0)).getValue()).setValue("B");

        EvaluationResult evaluationResult = ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        assertEquals(2, evaluationResult.getAssertionResults().size());
        assertTrue(evaluationResult.getAssertionResults().get(0).getResult());
        assertTrue(evaluationResult.getAssertionResults().get(1).getResult());

        // Check that when answer B is given, both paths should not exist
        assertEquals(2, evaluationResult.getPathsThatMustNotExist().size());
        assertEquals("/data[id2]/events[id3]/data[id4]/items[id7]", evaluationResult.getPathsThatMustNotExist().get(0));
        assertEquals("/data[id2]/events[id3]/data[id4]/items[id9]", evaluationResult.getPathsThatMustNotExist().get(1));
    }

    @Test
    public void orSelectPathExists() throws Exception {
        parse("or.adls");
        RuleEvaluation<Pathable> ruleEvaluation = getRuleEvaluation();

        Observation root = (Observation) testUtil.constructEmptyRMObject(archetype.getDefinition());
        // Simulate selecting the option that should hide another element
        root.getData().getEvents().get(0).getData().getItems().remove(2);
        root.getData().getEvents().get(0).getData().getItems().remove(1);
        ((DvCodedText) ((Element) root.getData().getEvents().get(0).getData().getItems().get(0)).getValue()).setDefiningCode(new CodePhrase(new TerminologyId("ac1"), "at1"));
        ((DvCodedText) ((Element) root.getData().getEvents().get(0).getData().getItems().get(0)).getValue()).setValue("A");

        EvaluationResult evaluationResult = ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        assertEquals(2, evaluationResult.getAssertionResults().size());
        assertTrue(evaluationResult.getAssertionResults().get(0).getResult());
        assertTrue(evaluationResult.getAssertionResults().get(1).getResult());

        // Check that when answer A is given, both paths should exist, so no path must not exist
        assertEquals(0, evaluationResult.getPathsThatMustNotExist().size());
    }

    @Test
    public void booleanOperandRelOps() throws Exception {
        parse("boolean_operand_relops.adls");
        assertTrue(parser.getErrors().hasNoErrors());
        RuleEvaluation<Pathable> ruleEvaluation = getRuleEvaluation();
        Observation root = new Observation();
        ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        VariableMap variables = ruleEvaluation.getVariableMap();
        assertEquals(true, variables.get("false_is_not_true").getObject(0));
        assertEquals(true, variables.get("false_is_false").getObject(0));
        assertEquals(false, variables.get("false_is_true").getObject(0));
        assertEquals(true, variables.get("arithmetic_boolean_operands_true").getObject(0));
        assertEquals(false, variables.get("arithmetic_boolean_operands_false").getObject(0));
    }

    RuleEvaluation<Pathable> getRuleEvaluation() {
        return new RuleEvaluation<>(OpenEhrRmInfoLookup.getInstance(), archetype);
    }

    @Test
    public void stringLiterals() throws Exception {
        parse("string_literals.adls");
        assertTrue(parser.getErrors().hasNoErrors());
        RuleEvaluation<Pathable> ruleEvaluation = getRuleEvaluation();
        Observation root = new Observation();
        ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        VariableMap variables = ruleEvaluation.getVariableMap();

        assertEquals(false, variables.get("test_neq_test").getObject(0));
        assertEquals(true, variables.get("test_eq_test").getObject(0));
        assertEquals("string contents", variables.get("string_variable").getObject(0));

    }

    @Test
    public void flattenedRules() throws IOException, ADLParseException {
        Archetype valueSet = parse("matches_valueset.adls");
        Archetype parent = parse("termcodeparent.adls");
        InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
        repository.addArchetype(parent);
        repository.addArchetype(valueSet);
        Flattener flattener = new Flattener(repository, AllMetaModelsInitialiser.getMetaModels(), FlattenerConfiguration.forOperationalTemplate());
        OperationalTemplate opt = (OperationalTemplate) flattener.flatten(parent);
        ExampleJsonInstanceGenerator generator = new ExampleJsonInstanceGenerator(AllMetaModelsInitialiser.getMetaModels(), "en");
        Map<String, Object> exampleInstance = generator.generate(opt);
        Cluster cluster = OpenEhrRmJacksonUtil.getObjectMapper().readValue(OpenEhrRmJacksonUtil.getObjectMapper().writeValueAsString(exampleInstance), Cluster.class);
        //correct case first
        RuleEvaluation ruleEvaluation = new RuleEvaluation(OpenEhrRmInfoLookup.getInstance(), opt);
        DvCodedText codedText = (DvCodedText) cluster.itemAtPath("/items[1]/items[1]/value[1]");
        codedText.setDefiningCode(new CodePhrase(new TerminologyId("local"), "at4"));
        codedText.setValue("value 1");
        EvaluationResult result = ruleEvaluation.evaluate(cluster, opt.getRules().getRules());
        AssertionResult assertionResult = result.getAssertionResults().get(0);
        assertTrue("The given validation rule should pass", assertionResult.getResult());
        assertEquals("ac3", assertionResult.getPathsConstrainedToValueSets().get("/items[id2, 1]/items[id2]/value/defining_code"));
        assertEquals(2, result.getPathsConstrainedToValueSets().size());
        assertEquals("ac3", result.getPathsConstrainedToValueSets().get("/items[id2, 1]/items[id2]/value/defining_code"));
        assertEquals("ac3", result.getPathsConstrainedToValueSets().get("/items[id2, 1]/items[id6]/value/defining_code"));

        //incorrect case next
        codedText.setDefiningCode(new CodePhrase(new TerminologyId("local"), "at26"));//wrong code!
        codedText.setValue("value 26");
        EvaluationResult falseResult = ruleEvaluation.evaluate(cluster, opt.getRules().getRules());
        AssertionResult  falseAssertionResult = falseResult.getAssertionResults().get(0);
        assertFalse(falseAssertionResult.getResult());
        assertEquals("ac3", assertionResult.getPathsConstrainedToValueSets().get("/items[id2, 1]/items[id2]/value/defining_code"));
    }

    @Test
    public void termBindingConstraint() throws IOException, ADLParseException {
        parse("matches_term_binding.adls");

        RuleEvaluation<Pathable> ruleEvaluation = getRuleEvaluation();
        Pathable root = (Pathable) testUtil.constructEmptyRMObject(archetype.getDefinition());

        // Rule with single term
        DvCodedText codedText = (DvCodedText) root.itemAtPath("items[id2]/value[id3]");
        codedText.setDefiningCode(new CodePhrase(new TerminologyId("openehr"), "526"));
        EvaluationResult evaluationResult = ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        assertEquals(2, evaluationResult.getAssertionResults().size());
        assertTrue(evaluationResult.getAssertionResults().get(0).getResult());

        codedText.setDefiningCode(new CodePhrase(new TerminologyId("openehr"), "527"));
        evaluationResult = ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        assertEquals(2, evaluationResult.getAssertionResults().size());
        assertFalse(evaluationResult.getAssertionResults().get(0).getResult());

        // Rule with value set
        DvCodedText codedText2 = (DvCodedText) root.itemAtPath("items[id4]/value[id5]");
        codedText2.setDefiningCode(new CodePhrase(new TerminologyId("openehr"), "526"));
        EvaluationResult evaluationResult2 = ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        assertEquals(2, evaluationResult2.getAssertionResults().size());
        assertTrue(evaluationResult2.getAssertionResults().get(1).getResult());

        codedText2.setDefiningCode(new CodePhrase(new TerminologyId("openehr"), "528"));
        evaluationResult = ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        assertEquals(2, evaluationResult.getAssertionResults().size());
        assertFalse(evaluationResult.getAssertionResults().get(1).getResult());
    }

}
