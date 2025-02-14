package com.nedap.archie.rmobjectvalidator;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CAttributeTuple;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.rm.datavalues.quantity.DvQuantity;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.testutil.TestUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by pieter.bos on 21/03/16.
 */
public class AttributeTupleConstraintsTest {

    private static CAttributeTuple attributeTuple;
    
    private final ValidationHelper helper = new ValidationHelper(ArchieRMInfoLookup.getInstance(), new ValidationConfiguration.Builder().build());

    @BeforeClass
    public static void setup() throws Exception {
        Archetype archetype = TestUtil.parseFailOnErrors("/basic.adl");
        CComplexObject valueObject = archetype.getDefinition().itemAtPath("/context[id11]/other_context[id2]/items[id3]/items[id7]/value[id16]");
        attributeTuple = valueObject.getAttributeTuples().get(0);
    }

    @Test
    public void tupleConstraint() throws Exception {

        HashMap<String, Object> kgValid = new HashMap<>();
        kgValid.put("units", "kg");
        kgValid.put("magnitude", 5d);
        HashMap<String, Object> lbValid = new HashMap<>();
        lbValid.put("units", "lb");
        lbValid.put("magnitude", 10d);

        assertTrue(helper.isValid(attributeTuple, kgValid));
        assertTrue(helper.isValid(attributeTuple, lbValid));

    }

    @Test
     public void tupleConstraintInvalid() throws Exception {

        HashMap<String, Object> lbInvalid = new HashMap<>();
        lbInvalid.put("units", "lb");
        lbInvalid.put("magnitude", 9d);
        HashMap<String, Object> kgInvalid = new HashMap<>();
        kgInvalid.put("units", "kg");
        kgInvalid.put("magnitude", 301d);

        HashMap<String, Object> invalidUnit = new HashMap<>();
        invalidUnit.put("units", "stone");
        invalidUnit.put("magnitude", 5d);

        assertFalse(helper.isValid(attributeTuple, lbInvalid));
        assertFalse(helper.isValid(attributeTuple, kgInvalid));
        assertFalse(helper.isValid(attributeTuple, invalidUnit));

    }

    @Test
    public void tupleConstraintExtraAttribute() throws Exception {
        HashMap<String, Object> extraAttribute = new HashMap<>();
        extraAttribute.put("units", "lb");
        extraAttribute.put("magnitude", 150d);
        extraAttribute.put("precison", 0.1d);

        //any extra attributes can be valid, because they are not constrained by this tuple
        assertTrue(helper.isValid(attributeTuple, extraAttribute));

    }

    @Test
    public void tupleConstraintMissingAttribute() throws Exception {
        HashMap<String, Object> missingAttribute = new HashMap<>();
        missingAttribute.put("units", "lb");

        assertFalse(helper.isValid(attributeTuple, missingAttribute));

    }

    @Test
    public void withRmObjectValid() throws Exception {
        DvQuantity valid = new DvQuantity();
        valid.setUnits("lb");
        valid.setMagnitude(150d);
        assertTrue(helper.isValid(attributeTuple, valid));
    }

    @Test
    public void withRmObjectInvalid() throws Exception {
        DvQuantity valid = new DvQuantity();
        valid.setUnits("kg");
        valid.setMagnitude(600d);
        assertFalse(helper.isValid(attributeTuple, valid));
    }

    @Test
    public void toStringTest() {
        String toString = attributeTuple.toString().replace("\t", "").replace("\n", "");
        assertEquals("[units, magnitude] ∈ {[{\"kg\"}, {|5.0..300.0|}],[{\"lb\"}, {|10.0..600.0|}]}", toString);

    }
}
