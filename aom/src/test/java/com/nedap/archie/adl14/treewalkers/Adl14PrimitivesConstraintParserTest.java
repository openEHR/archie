package com.nedap.archie.adl14.treewalkers;

import com.nedap.archie.adlparser.antlr.Adl14Lexer;
import com.nedap.archie.adlparser.antlr.Adl14Parser;
import com.nedap.archie.antlr.errors.ANTLRParserErrors;
import com.nedap.archie.aom.primitives.CTerminologyCodeADL14;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link Adl14PrimitivesConstraintParser#parseCTerminologyCode}.
 * Each test parses a single terminology code snippet and asserts on the resulting
 * {@link CTerminologyCodeADL14} — covering the four branches in the parser:
 * single TERM_CODE_REF (local), single TERM_CODE_REF (external), qualified multi-code
 * (local), qualified multi-code (external), and AC-code.
 */
public class Adl14PrimitivesConstraintParserTest {

    @Test
    public void parsesSingleLocalAtCode() {
        CTerminologyCodeADL14 result = parse("[local::at0001]");
        assertEquals(Arrays.asList("at0001"), result.getConstraint());
        assertNull(result.getAssumedValue());
    }

    @Test
    public void parsesSingleExternalTermCode() {
        CTerminologyCodeADL14 result = parse("[snomed-ct::12345]");
        // For external single-code TERM_CODE_REF the parser keeps the full bracketed form so the
        // converter can later create a binding from it.
        assertEquals(Arrays.asList("[snomed-ct::12345]"), result.getConstraint());
    }

    @Test
    public void parsesMultiCodeLocalConstraint() {
        // This is the case that motivated the PR — used to silently parse to an empty list.
        CTerminologyCodeADL14 result = parse("[local::at0001, at0002, at0003]");
        assertEquals(Arrays.asList("at0001", "at0002", "at0003"), result.getConstraint());
    }

    @Test
    public void parsesMultiCodeExternalConstraint() {
        CTerminologyCodeADL14 result = parse("[openehr::271, 272, 273, 253]");
        // The parser stores the terminologyId as the first entry and the bare codes after it,
        // which the converter then assembles into full term-code refs during conversion.
        assertEquals(Arrays.asList("openehr", "271", "272", "273", "253"), result.getConstraint());
    }

    @Test
    public void parsesAcCode() {
        CTerminologyCodeADL14 result = parse("[ac0001]");
        assertEquals(Arrays.asList("ac0001"), result.getConstraint());
        assertNull(result.getAssumedValue());
    }

    @Test
    public void parsesQualifiedTermCodeWithAssumedValue() {
        // Qualified-form term codes carry their assumed value via the assumed_value grammar rule,
        // which the parser hands directly to setAssumedValue.
        CTerminologyCodeADL14 result = parse("[local::at0001; at0002]");
        assertEquals(Arrays.asList("at0001"), result.getConstraint());
        assertEquals("at0002", result.getAssumedValue().getCodeString());
    }

    @Test
    public void parserReportsNoErrorsForValidInputs() {
        ANTLRParserErrors errors = new ANTLRParserErrors();
        parse("[local::at0001, at0002]", errors);
        assertTrue(errors.getErrors().isEmpty(),
                () -> "unexpected parse errors: " + errors);
    }

    private CTerminologyCodeADL14 parse(String terminologyCodeText) {
        return parse(terminologyCodeText, new ANTLRParserErrors());
    }

    private CTerminologyCodeADL14 parse(String terminologyCodeText, ANTLRParserErrors errors) {
        Adl14Lexer lexer = new Adl14Lexer(CharStreams.fromString(terminologyCodeText));
        Adl14Parser parser = new Adl14Parser(new CommonTokenStream(lexer));
        Adl14Parser.C_terminology_codeContext context = parser.c_terminology_code();
        return new Adl14PrimitivesConstraintParser(errors).parseCTerminologyCode(context);
    }
}
