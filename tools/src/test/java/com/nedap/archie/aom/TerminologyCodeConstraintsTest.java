package com.nedap.archie.aom;

import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.archetypevalidator.ArchetypeValidatorTest;
import com.nedap.archie.base.terminology.TerminologyCode;
import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rmobjectvalidator.RMObjectValidator;
import com.nedap.archie.serializer.adl.ADLArchetypeSerializerTest;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by pieter.bos on 23/02/16.
 */
public class TerminologyCodeConstraintsTest {

    private ADLParser parser = new ADLParser();
    TestUtil testUtil = new TestUtil();
    RMObjectValidator validator = new RMObjectValidator(ArchieRMInfoLookup.getInstance());

    @Test
    public void noConstraint() {
        CTerminologyCode code = new CTerminologyCode();
        assertTrue(code.isValidValue(TerminologyCode.createFromString("[ac12::at23]")));
    }

    @Test
    public void terminologyIdConstraint() {
        CTerminologyCode code = new CTerminologyCode();
        code.addConstraint("ac12");
        assertTrue(code.isValidValue(TerminologyCode.createFromString("[ac12::at23]")));
        assertTrue(code.isValidValue(TerminologyCode.createFromString("[ac12::at24]")));
        assertFalse(code.isValidValue(TerminologyCode.createFromString("[ac13::at23]")));
    }

    @Test
    public void terminologyIdConstraintValueWithoutTerminologyId() throws Exception {
        Archetype archetype = parse("/adl2-tests/rmobjectvalidity/openEHR-EHR-CLUSTER.value_sets.v1.0.0.adls");

        CTerminologyCode code = (CTerminologyCode) archetype.getDefinition()
                .getAttribute("items").getChild("id2")
                .getAttribute("value").getChild("id3")
                .getAttribute("defining_code").getChildren().get(0);

        assertTrue(code.isValidValue(TerminologyCode.createFromString("at10")));
        assertFalse(code.isValidValue(TerminologyCode.createFromString("at12")));
    }

    @Test
    public void terminologyIdConstraintWithoutTerminologyIdAndArchetype() throws Exception {
        CTerminologyCode code = new CTerminologyCode();
        code.addConstraint("ac12");
        assertFalse(code.isValidValue(TerminologyCode.createFromString("at23")));
    }

        @Test
    public void terminologyCodeConstraint() {
        CTerminologyCode code = new CTerminologyCode();
        code.addConstraint("at23");
        assertTrue(code.isValidValue(TerminologyCode.createFromString("[ac12::at23]")));
        assertTrue(code.isValidValue(TerminologyCode.createFromString("[ac13::at23]")));
        assertFalse(code.isValidValue(TerminologyCode.createFromString("[ac13::at24]")));
    }

    @Test
    public void dvCodedText() {
        //DV_CODED_TEXT can be constrained by a C_TERMINOLOGY_CONSTRAINT, according to lots of DV_ORDINAL usage in the CKM
        CTerminologyCode code = new CTerminologyCode();
        code.addConstraint("at23");
        DvCodedText text = new DvCodedText();
        text.setValue("does not matter for this validation");
        text.setDefiningCode(new CodePhrase("[ac12::at23]"));
        assertTrue(code.isValidValue(ArchieRMInfoLookup.getInstance(), text));
        text.setDefiningCode(new CodePhrase("[ac13::at24]"));
        assertFalse(code.isValidValue(ArchieRMInfoLookup.getInstance(), text));
    }

    private Archetype parse(String filename) throws IOException {
        Archetype archetype = parser.parse(ArchetypeValidatorTest.class.getResourceAsStream(filename));
        assertTrue(parser.getErrors().toString(), parser.getErrors().hasNoErrors());
        return archetype;
    }
}
