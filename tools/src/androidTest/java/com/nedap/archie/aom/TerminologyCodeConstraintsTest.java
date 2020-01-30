package com.nedap.archie.aom;

import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.base.terminology.TerminologyCode;
import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by pieter.bos on 23/02/16.
 */
public class TerminologyCodeConstraintsTest {

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
}
