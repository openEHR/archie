package com.nedap.archie.serializer.rules;

import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.rules.evaluation.ParsedRulesEvaluationTest;
import com.nedap.archie.serializer.adl.ADLArchetypeSerializer;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created by pieter.bos on 15/06/16.
 */
public class ADLRulesSerializerTest {

    @Test
    public void basicArithmetic() throws Exception {
        Archetype archetype = load("simplearithmetic.adls");
        String serializedADL = ADLArchetypeSerializer.serialize(archetype);

        
        assertTrue(serializedADL.contains("$arithmetic_test:Integer ::= 3 * 5 + 2 * 2 - 15 + 4"));
        assertTrue(serializedADL.contains("$boolean_false_test:Boolean ::= 3 > 5 + 6 * 7 + 3 * 23 + 8 /  (1 + 2)"));
        assertTrue(serializedADL.contains("$boolean_true_test:Boolean ::= 3 < 5 + 6 * 7 + 3 * 23 + 8 /  (1 + 2)"));
        assertTrue(serializedADL.contains("$boolean_extended_test:Boolean ::=  (3 < 5 or 2 > 1)  and 1 = 1"));
        assertTrue(serializedADL.contains("not_false:Boolean ::= not false"));
        assertTrue(serializedADL.contains("$not_not_not_true:Boolean ::= not not not true"));
        assertTrue(serializedADL.contains("$variable_reference:Integer ::= $arithmetic_test % 5"));
        assertTrue(serializedADL.contains("$arithmetic_parentheses:Integer ::=  (3 + 2)  * 5"));
    }

    @Test
    public void matches() throws Exception {
        Archetype archetype = load("matches.adls");
        String serializedADL = ADLArchetypeSerializer.serialize(archetype);

        assertTrue(serializedADL.contains("$extended_validity:Boolean ::= /data[id2]/events[id3]/data[id4]/items[id5]/value/magnitude matches {|0.0..30.0|}"));
    }

    @Test
    public void modelReferences() throws Exception {
        Archetype archetype = load("modelreferences.adls");
        String serializedADL = ADLArchetypeSerializer.serialize(archetype);

        assertTrue(serializedADL.contains("$arithmetic_test:Real ::= /data[id2]/events[id3]/data[id4]/items[id5]/value/magnitude"));
        assertTrue(serializedADL.contains("blood_pressure_valid: $arithmetic_test > 50"));

    }

    @Test
    public void forAll() throws Exception {
        Archetype archetype = load("for_all.adls");
        String serializedADL = ADLArchetypeSerializer.serialize(archetype);

        assertTrue(serializedADL.contains("blood_pressure_valid: for_all $event in /data[id2]/events\n" +
                "         $event/data[id4]/items[id5]/value/magnitude > $event/data[id4]/items[id6]/value/magnitude - 5"));
    }


    private Archetype load(String resourceName) throws IOException {
        return new ADLParser().parse(ParsedRulesEvaluationTest.class.getResourceAsStream(resourceName));

    }
}
