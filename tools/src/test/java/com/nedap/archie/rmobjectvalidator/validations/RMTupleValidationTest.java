package com.nedap.archie.rmobjectvalidator.validations;

import com.nedap.archie.aom.*;
import com.nedap.archie.aom.primitives.CReal;
import com.nedap.archie.aom.primitives.CString;
import com.nedap.archie.base.Interval;
import com.nedap.archie.query.RMObjectWithPath;
import com.nedap.archie.rm.datavalues.quantity.DvQuantity;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessage;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RMTupleValidationTest {
    private static ArchieRMInfoLookup lookup;

    @BeforeClass
    public static void beforeClass() {
        lookup = ArchieRMInfoLookup.getInstance();
    }

    @Test
    public void testValidateSingleTuple() {
        CComplexObject cObject = new CComplexObject();

        // Attribute tuple
        CAttributeTuple tuple = new CAttributeTuple();
        cObject.addAttributeTuple(tuple);

        CAttribute magnitudeAttr = new CAttribute("magnitude");
        tuple.addMember(magnitudeAttr);

        CAttribute unitsAttr = new CAttribute("units");
        tuple.addMember(unitsAttr);

        // Primitive tuple
        CPrimitiveTuple primitiveTuple = new CPrimitiveTuple();
        tuple.addTuple(primitiveTuple);

        CReal magnitudeConstraint = new CReal();
        primitiveTuple.addMember(magnitudeConstraint);
        magnitudeConstraint.addConstraint(Interval.upperUnbounded(0.0, false));

        CString unitsConstraint = new CString("m/s");
        primitiveTuple.addMember(unitsConstraint);

        // Set up quantity
        DvQuantity quantity = new DvQuantity();
        quantity.setMagnitude(-4.0);
        quantity.setUnits("wr/ong");

        List<RMObjectWithPath> rmObjects = new ArrayList<>();
        rmObjects.add(new RMObjectWithPath(quantity, null));

        // Run validation
        List<RMObjectValidationMessage> result = RMTupleValidation.validate(lookup, cObject, "/path/so/far", rmObjects, tuple);

        // Asserts
        assertEquals(2, result.size());

        RMObjectValidationMessage magnitudeMessage = result.get(0);
        assertEquals("/path/so/far/magnitude[id9999]", magnitudeMessage.getPath());
        String magnitudeString = "The value -4.0 must be greater than 0";
        assertEquals(magnitudeString, magnitudeMessage.getMessage());

        RMObjectValidationMessage unitsMessage = result.get(1);
        assertEquals("/path/so/far/units[id9999]", unitsMessage.getPath());
        String unitsString = "The value \"wr/ong\" must be \"m/s\"";
        assertEquals(unitsString, unitsMessage.getMessage());
    }

    @Test
    public void testValidateTupleWithNullValue() {
        CComplexObject cObject = new CComplexObject();

        // Attribute tuple
        CAttributeTuple tuple = new CAttributeTuple();
        cObject.addAttributeTuple(tuple);

        CAttribute magnitudeAttr = new CAttribute("magnitude");
        tuple.addMember(magnitudeAttr);

        CAttribute unitsAttr = new CAttribute("units");
        tuple.addMember(unitsAttr);

        // Primitive tuple
        CPrimitiveTuple primitiveTuple = new CPrimitiveTuple();
        tuple.addTuple(primitiveTuple);

        CReal magnitudeConstraint = new CReal();
        primitiveTuple.addMember(magnitudeConstraint);
        magnitudeConstraint.addConstraint(Interval.upperUnbounded(0.0, false));

        CString unitsConstraint = new CString("m/s");
        primitiveTuple.addMember(unitsConstraint);

        // Set up quantity
        DvQuantity quantity = new DvQuantity();
        quantity.setMagnitude(15D);
        quantity.setUnits(null);

        List<RMObjectWithPath> rmObjects = new ArrayList<>();
        rmObjects.add(new RMObjectWithPath(quantity, null));

        // Run validation
        List<RMObjectValidationMessage> result = RMTupleValidation.validate(lookup, cObject, "/path/so/far", rmObjects, tuple);

        // Asserts
        assertEquals(1, result.size());

        RMObjectValidationMessage unitsMessage = result.get(0);
        assertEquals("/path/so/far/units[id9999]", unitsMessage.getPath());
        String unitsString = "The value empty must be \"m/s\"";
        assertEquals(unitsString, unitsMessage.getMessage());
    }

    @Test
    public void testValidateMultipleTuple() {
        CComplexObject cObject = new CComplexObject();

        // Attribute tuple
        CAttributeTuple tuple = new CAttributeTuple();
        cObject.addAttributeTuple(tuple);

        CAttribute magnitudeAttr = new CAttribute("magnitude");
        tuple.addMember(magnitudeAttr);

        CAttribute unitsAttr = new CAttribute("units");
        tuple.addMember(unitsAttr);

        // Primitive tuple 1
        CPrimitiveTuple primitiveTuple1 = new CPrimitiveTuple();
        tuple.addTuple(primitiveTuple1);

        CReal magnitudeConstraint1 = new CReal();
        primitiveTuple1.addMember(magnitudeConstraint1);
        magnitudeConstraint1.addConstraint(new Interval<>(0.0, 20.0));

        primitiveTuple1.addMember(new CString("m/s"));

        // Primitive tuple 2
        CPrimitiveTuple primitiveTuple2 = new CPrimitiveTuple();
        tuple.addTuple(primitiveTuple2);

        CReal magnitudeConstraint = new CReal();
        primitiveTuple2.addMember(magnitudeConstraint);
        magnitudeConstraint.addConstraint(new Interval<>(0.0, 72.0));

        primitiveTuple2.addMember(new CString("km/h"));

        // Set up quantity
        DvQuantity quantity = new DvQuantity();
        quantity.setMagnitude(-4.0);
        quantity.setUnits("wr/ong");

        List<RMObjectWithPath> rmObjects = new ArrayList<>();
        rmObjects.add(new RMObjectWithPath(quantity, null));

        // Run validation
        List<RMObjectValidationMessage> result = RMTupleValidation.validate(lookup, cObject, "/path/so/far", rmObjects, tuple);

        // Asserts
        assertEquals(1, result.size());

        RMObjectValidationMessage validationMessage = result.get(0);
        assertEquals("/path/so/far", validationMessage.getPath());
        String expected = "Object does not match tuple: [magnitude, units] ∈ {\n" +
                "\t[{|0.0..20.0|}, {\"m/s\"}],\n" +
                "\t[{|0.0..72.0|}, {\"km/h\"}]\n" +
                "}";
        assertEquals(expected, validationMessage.getMessage());
    }
}
