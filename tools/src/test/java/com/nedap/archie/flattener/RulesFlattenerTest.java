package com.nedap.archie.flattener;

import com.nedap.archie.adlparser.ADLParseException;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.rminfo.ReferenceModels;
import com.nedap.archie.rules.*;
import com.nedap.archie.serializer.adl.ADLArchetypeSerializer;
import org.junit.Before;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import static org.junit.Assert.*;

/**
 * Created by pieter.bos on 15/05/2017.
 */
public class RulesFlattenerTest {

    private Archetype withRules;
    private Archetype specializedRules;
    private Archetype containingRules;

    private Flattener flattener;
    private SimpleArchetypeRepository repository;

    private ReferenceModels models;

    @Before
    public void setup() throws Exception {
        models = BuiltinReferenceModels.getAvailableModelInfoLookups();

        withRules = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-OBSERVATION.with_rules.v1.adls"));
        Archetype withRulesMixedCaseDashes = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-OBSERVATION.With-rules-Mixed-case-dashes.v1.adls"));
        specializedRules = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-OBSERVATION.specialized_rules.v1.adls"));
        containingRules = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-COMPOSITION.containing_rules.v1.adls"));

        repository = new SimpleArchetypeRepository();
        repository.addArchetype(withRules);
        repository.addArchetype(withRulesMixedCaseDashes);
        repository.addArchetype(specializedRules);
        repository.addArchetype(containingRules);

        flattener = new Flattener(repository, models).createOperationalTemplate(true);
    }

    @Test
    public void specializedRules() throws ADLParseException {
        Archetype flattened = flattener.flatten(specializedRules);
        assertEquals(5, flattened.getRules().getRules().size()); //three original rules, one overwritten, one added

        ExpressionVariable systolic = (ExpressionVariable) flattened.getRules().getRules().get(0);
        ExpressionVariable diastolic = (ExpressionVariable) flattened.getRules().getRules().get(1);
        Assertion bloodPressure = (Assertion) flattened.getRules().getRules().get(2);
        ExpressionVariable flattenedPathArguments = (ExpressionVariable) flattened.getRules().getRules().get(3);
        Assertion biggerThan90 = (Assertion) flattened.getRules().getRules().get(4);

        assertEquals("systolic", systolic.getName());
        assertEquals("diastolic", diastolic.getName());
        assertEquals("blood_pressure", bloodPressure.getTag());
        assertEquals("flattened_path_arguments", flattenedPathArguments.getName());
        assertEquals(BinaryOperator.class, biggerThan90.getExpression().getClass());
        checkCorrectSyntax(flattened);
    }

    private void checkCorrectSyntax(Archetype flattened) throws ADLParseException {
        String serialized= ADLArchetypeSerializer.serialize(flattened);
        ADLParser parser = new ADLParser();
        parser.parse(serialized);
        assertFalse(parser.getErrors().toString(), parser.getErrors().hasErrors());
    }

    @Test
    public void flattenedRules() throws Exception {
        Archetype flattened = flattener.flatten(containingRules);

        CObject systolicCObject = flattened.itemAtPath("/content[id5]/data/events/data/items[id5]");
        assertEquals("systolic", systolicCObject.getTerm().getText());
        assertEquals(9, flattened.getRules().getRules().size()); //specialized rules, prefixed with the content[id5] path

        ExpressionVariable systolic = (ExpressionVariable) flattened.getRules().getRules().get(0);
        ExpressionVariable diastolic = (ExpressionVariable) flattened.getRules().getRules().get(1);
        Assertion bloodPressure = (Assertion) flattened.getRules().getRules().get(2);
        ExpressionVariable flattenedPathArguments = (ExpressionVariable) flattened.getRules().getRules().get(3);
        Assertion biggerThan90 = (Assertion) flattened.getRules().getRules().get(4);

        assertEquals("specialized_rules_systolic", systolic.getName());
        assertEquals("specialized_rules_diastolic", diastolic.getName());
        assertEquals("specialized_rules_blood_pressure", bloodPressure.getTag());
        assertEquals(ForAllStatement.class, biggerThan90.getExpression().getClass());
        assertEquals("/content[id5]/data[id2]/events[id3]/data[id4]/items[id5]/value/magnitude", ((ModelReference) ((Function) flattenedPathArguments.getExpression()).getArguments().get(0)).getPath());
        assertEquals("/content[id5]/data[id2]/events[id3]/data[id4]/items[id6]/value/magnitude", ((ModelReference) ((Function) flattenedPathArguments.getExpression()).getArguments().get(1)).getPath());
        assertEquals("/content[id5]", ((ModelReference)((ForAllStatement)biggerThan90.getExpression()).getPathExpression()).getPath());

        assertEquals("with_rules_mixed_case_dashes_systolic", ((ExpressionVariable) flattened.getRules().getRules().get(5)).getName());
        assertEquals("with_rules_mixed_case_dashes_Diastolic", ((ExpressionVariable) flattened.getRules().getRules().get(6)).getName());
        assertEquals("with_rules_mixed_case_dashes_Blood_Pressure", ((Assertion) flattened.getRules().getRules().get(7)).getTag());
        assertEquals("with_rules_mixed_case_dashes_flattened_path_arguments", ((ExpressionVariable) flattened.getRules().getRules().get(8)).getName());

        //test that we can actually parse the output
        ADLParser parser = new ADLParser();
        parser.parse(ADLArchetypeSerializer.serialize(flattened));
        assertTrue(parser.getErrors().hasNoErrors());
        checkCorrectSyntax(flattened);
    }

}
