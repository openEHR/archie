package com.nedap.archie.adlparser;

import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.primitives.CString;
import com.nedap.archie.rules.Assertion;
import com.nedap.archie.rules.BinaryOperator;
import com.nedap.archie.rules.Constraint;
import com.nedap.archie.rules.ModelReference;
import com.nedap.archie.rules.RuleStatement;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by pieter.bos on 31/10/15.
 */
public class RegexTest extends PrimitivesConstraintParserTest {

    @Test
    public void basic_regexps() {

        CString regexp1 = getAttribute("string_attr2");
        CString regexp2 = getAttribute("string_attr3");

        assertEquals("/this|that|something else/", regexp1.getConstraint().get(0));
        assertEquals("/cardio.*/", regexp2.getConstraint().get(0));

    }

    @Test
    public void extended_regexp() throws Exception {
        archetype = TestUtil.parseFailOnErrors("/regexps.adls");
        CString regexpSlashEscape = getAttribute("slash_escaped");
        CString regexpUTF8 = getAttribute("utf8");
        CString regexpAssumedValue = getAttribute("utf8_assumed_value");
        assertEquals("/regexp with escapes slash \\/.* (x|y) /", regexpSlashEscape.getConstraint().get(0));
        assertEquals("/☃/", regexpUTF8.getConstraint().get(0));
        assertEquals("/(☃|☄)/", regexpAssumedValue.getConstraint().get(0));
        assertEquals("☄", regexpAssumedValue.getAssumedValue());

        {
            //path and regexpes can clash if the parser is wrong
            CAttribute pathAttribute = archetype.getDefinition().getAttribute("/start");
            assertEquals("/start", pathAttribute.getDifferentialPath());
            CString regex = (CString) pathAttribute.getChildren().get(0);
            assertEquals("/this should work/", regex.getConstraint().get(0));
        }

        //the following still fails due to https://github.com/openEHR/adl-antlr/issues/20
        {
            //path and regexpes can clash if the parser is wrong
            CAttribute pathAttribute = archetype.getDefinition().getAttribute("/start[id2]/end");
            assertEquals("/start[id2]/end", pathAttribute.getDifferentialPath());
            CString regex = (CString) pathAttribute.getChildren().get(0);
            assertEquals("/this should work/", regex.getConstraint().get(0));
        }
        {
            //rules section contains some
            List<RuleStatement> rules = archetype.getRules().getRules();
            assertEquals(1, rules.size());
            Assertion rule = (Assertion) rules.get(0);
            BinaryOperator binaryExpression = (BinaryOperator) rule.getExpression();
            ModelReference ref = (ModelReference) binaryExpression.getLeftOperand();
            assertEquals("relative[id3]/path", ref.getPath());
            Constraint regexConstraint = (Constraint) binaryExpression.getRightOperand();
            CString regex = (CString) regexConstraint.getItem();
            assertEquals("/regexp/", regex.getConstraint().get(0));
        }
    }




}
