package com.nedap.archie.rmobjectvalidator;

import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CAttributeTuple;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.aom.CPrimitiveTuple;
import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.rminfo.RMAttributeInfo;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * Helper class for validation of primitive object constaints and attribute tuple constraints.
 */
public class ValidationHelper {
    private final ModelInfoLookup lookup;
    private final PrimitiveObjectConstraintHelper primitiveObjectConstraintHelper;

    public ValidationHelper(ModelInfoLookup lookup, ValidationConfiguration validationConfiguration) {
        this.lookup = lookup;
        this.primitiveObjectConstraintHelper = new PrimitiveObjectConstraintHelper(validationConfiguration);
    }

    /**
     * True if the given value is a valid value for this constraint
     * first Converts the value to a checkable value using the given ModelInfoLookup
     * For example when it is an interval or pattern
     *
     * @param value
     * @return
     */
    public <ValueType> boolean isValidValue(CPrimitiveObject<?, ValueType> cPrimitiveObject, Object value) {
        Object convertedValue = lookup.convertToConstraintObject(value, cPrimitiveObject);
        return primitiveObjectConstraintHelper.isValidValue(cPrimitiveObject, (ValueType) convertedValue);
    }

    /**
     * Given a hashmap of attribute names mapping to its values, check the validity of this set of values
     * return true if and only if the given values are valid.
     */
    boolean isValid(CAttributeTuple cAttributeTuple, HashMap<String, Object> values) {
        for(CAttribute attribute:cAttributeTuple.getMembers()) {
            if(!values.containsKey(attribute.getRmAttributeName())) {
                return false;
            }
        }

        for(CPrimitiveTuple tuple:cAttributeTuple.getTuples()) {
            if (isValid(cAttributeTuple, tuple, values)) {
                return true;
            }
        }
        return false;
    }


    private boolean isValid(CAttributeTuple cAttributeTuple, CPrimitiveTuple tuple, HashMap<String, Object> values) {

        int index = 0;
        for(CAttribute attribute:cAttributeTuple.getMembers()) {
            String attributeName = attribute.getRmAttributeName();

            CPrimitiveObject<?, ?> cPrimitiveObject = tuple.getMembers().get(index);
            Object value = values.get(attributeName);
            if(value == null) {
                return false;
                //alternatively, look at occurrences or parent attribute existence?
                //not sure if we should in a tuple - a constrained value that is null is generally an error
            }
            if(!isValidValue(cPrimitiveObject, value)) {
                return false;
            }
            index++;
        }
        return true;
    }

    /**
     * Given a reference model object, check if it is valid
     * return true if and only if the given values are valid.
     */
    boolean isValid(CAttributeTuple cAttributeTuple, Object value) {

        HashMap<String, Object> members = new HashMap<>();
        for(CAttribute attribute:cAttributeTuple.getMembers()) {
            RMAttributeInfo attributeInfo = lookup.getAttributeInfo(value.getClass(), attribute.getRmAttributeName());
            try {
                if (attributeInfo != null && attributeInfo.getGetMethod() != null) {
                    members.put(attribute.getRmAttributeName(), attributeInfo.getGetMethod().invoke(value));
                } else {
                    //warn? throw exception?
                }
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return isValid(cAttributeTuple, members);
    }
}
