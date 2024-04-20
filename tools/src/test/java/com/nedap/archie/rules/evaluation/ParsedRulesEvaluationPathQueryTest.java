package com.nedap.archie.rules.evaluation;

import org.openehr.rm.archetyped.Pathable;
import org.openehr.rm.datastructures.Cluster;
import org.openehr.rm.datastructures.Element;
import org.openehr.rm.datavalues.quantity.datetime.DvDate;
import org.openehr.rm.datavalues.quantity.datetime.DvDateTime;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class ParsedRulesEvaluationPathQueryTest extends ParsedRulesEvaluationTest {

    @Test
    public void dateMagnitude() throws Exception {
        parse("date_magnitude.adls");
        RuleEvaluation<Pathable> ruleEvaluation = getRuleEvaluation();
        Cluster root = new Cluster();
        root.setArchetypeNodeId("id1");
        Element element = new Element();
        element.setArchetypeNodeId("id2");
        element.setValue(new DvDate(LocalDate.of(2021, 6, 1)));
        root.addItem(element);
        ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        VariableMap variables = ruleEvaluation.getVariableMap();

        assertEquals(18779l+ DvDate.DAYS_BETWEEN_0001_AND_1970, variables.get("date_magnitude").getObject(0));
    }

    @Test
    public void dateMagnitudeDifference() throws Exception {
        parse("date_magnitude.adls");
        RuleEvaluation<Pathable> ruleEvaluation = getRuleEvaluation();
        Cluster root = new Cluster();
        root.setArchetypeNodeId("id1");
        Element element = new Element();
        element.setArchetypeNodeId("id2");
        element.setValue(new DvDate(LocalDate.of(2021, 6, 20)));
        root.addItem(element);
        Element element2 = new Element();
        element2.setArchetypeNodeId("id10");
        element2.setValue(new DvDate(LocalDate.of(2021, 6, 1)));

        root.addItem(element2);
        ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        VariableMap variables = ruleEvaluation.getVariableMap();

        assertEquals(19l, variables.get("date_difference").getObject(0));
    }

    @Test
    public void dateTimeMagnitudeDifference() throws Exception {
        parse("date_magnitude.adls");
        RuleEvaluation<Pathable> ruleEvaluation = getRuleEvaluation();
        Cluster root = new Cluster();
        root.setArchetypeNodeId("id1");
        Element element = new Element();
        element.setArchetypeNodeId("id2");
        element.setValue(new DvDateTime(LocalDateTime.of(2021, 6, 20, 0, 0, 40)));
        root.addItem(element);
        Element element2 = new Element();
        element2.setArchetypeNodeId("id10");
        element2.setValue(new DvDateTime(LocalDateTime.of(2021, 6, 20, 0, 0, 1)));

        root.addItem(element2);
        ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        VariableMap variables = ruleEvaluation.getVariableMap();

        assertEquals(39l, variables.get("date_difference").getObject(0));
    }

    @Test
    public void dateTimeMagnitude() throws Exception {
        parse("date_magnitude.adls");
        RuleEvaluation<Pathable> ruleEvaluation = getRuleEvaluation();
        Cluster root = new Cluster();
        root.setArchetypeNodeId("id1");
        Element element = new Element();
        element.setArchetypeNodeId("id2");
        element.setValue(new DvDateTime(LocalDateTime.of(2021, 6, 1, 0, 0, 0)));
        root.addItem(element);
        ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        VariableMap variables = ruleEvaluation.getVariableMap();

        assertEquals(63758102400l, variables.get("date_magnitude").getObject(0));
    }
}
