package com.nedap.archie.rmobjectvalidator.validations;

import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.aom.primitives.CInteger;
import com.nedap.archie.base.Interval;
import com.nedap.archie.query.RMObjectWithPath;
import com.nedap.archie.openehr.rminfo.OpenEhrRmInfoLookup;
import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessage;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RMPrimitiveObjectValidationTest {

    private static OpenEhrRmInfoLookup lookup;

    @BeforeClass
    public static void beforeClass() {
        lookup = OpenEhrRmInfoLookup.getInstance();
    }

    public static List<RMObjectValidationMessage> validate(ModelInfoLookup lookup, List<RMObjectWithPath> rmObjects, String pathSoFar, CPrimitiveObject<?, ?> cobject) {
        return null;
    }

    @Test
    public void testCIntegerValidate() {
        CInteger cInteger = new CInteger();
        cInteger.addConstraint(Interval.upperUnbounded(0L, true));

        List<RMObjectWithPath> rmObjects = new ArrayList<>();
        rmObjects.add(new RMObjectWithPath(-4L, null));

        List<RMObjectValidationMessage> result = RMPrimitiveObjectValidation.validate(lookup, rmObjects, "/path/so/far", cInteger);

        assertEquals(1, result.size());

        RMObjectValidationMessage message = result.get(0);

        assertEquals("/path/so/far", message.getPath());
        String expectedMessage = "The value -4 must be at least 0";
        assertEquals(expectedMessage, message.getMessage());
    }

    @Test
    public void testCIntegerValidate_inner() {
        CInteger cInteger = new CInteger();
        cInteger.addConstraint(Interval.lowerUnbounded(-10L, false));
        cInteger.addConstraint(Interval.upperUnbounded(0L, true));

        List<RMObjectValidationMessage> result = RMPrimitiveObjectValidation.validate_inner(lookup, -4L, "/path/so/far", cInteger);

        assertEquals(1, result.size());

        RMObjectValidationMessage message = result.get(0);

        assertEquals("/path/so/far", message.getPath());
        String expectedMessage = "The value -4 must be one of:\n" +
                " -\tless than -10\n" +
                " -\tat least 0";
        assertEquals(expectedMessage, message.getMessage());
    }
}
