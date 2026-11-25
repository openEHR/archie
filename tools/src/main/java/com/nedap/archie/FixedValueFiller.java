package com.nedap.archie;

import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.aom.primitives.COrdered;
import com.nedap.archie.aom.primitives.CString;
import com.nedap.archie.base.Interval;
import com.nedap.archie.query.RMObjectWithPath;
import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.rminfo.RMAttributeInfo;
import com.nedap.archie.rmobjectvalidator.APathQueryCache;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;

/**
 * Fills fixed values from the archetype if the corresponding values are missing in the RM objects
 */
public class FixedValueFiller {

    private final ModelInfoLookup lookup;

    private final APathQueryCache queryCache = new APathQueryCache();

    public FixedValueFiller(ModelInfoLookup lookup) {
        this.lookup = lookup;
    }

    /**
     * Recursively fill fixed values.
     *
     * @param rmObject RM object to fill fixed values in.
     * @param cObject CObject to retrieve the fixed values from.
     */
    public void fillFixedValues(Object rmObject, CObject cObject) {
        // Do not process the constraint if the constraint type is not assignable from the data type
        // Examples:
        // - DV_CODED_TEXT constraint with DV_TEXT data: do not continue
        // - DV_TEXT constraint with DV_TEXT data: continue
        // - DV_TEXT constraint with DV_CODED_TEXT data: continue
        Class<?> rmClassOfCObject = lookup.getClass(cObject.getRmTypeName());
        if(rmClassOfCObject != null && !rmClassOfCObject.isAssignableFrom(rmObject.getClass())) {
            return; //stop filling fixed values, the types do not match
        }

        for (CAttribute cAttribute : cObject.getAttributes()) {
            fillFixedValues(rmObject, cAttribute);
        }
    }

    /**
     * Recursively fill fixed values for an attribute.
     *
     * @param rmObject RM object to fill fixed values in.
     * @param cAttribute CAttribute to retrieve the fixed values from.
     */
    private void fillFixedValues(Object rmObject, CAttribute cAttribute) {
        if(cAttribute.getChildren() == null) {
            return;
        }
        
        String rmAttributeName = cAttribute.getRmAttributeName();
        RMAttributeInfo attributeInfo = lookup.getAttributeInfo(rmObject.getClass(), rmAttributeName);

        if(!attributeInfo.isMultipleValued() && getValue(rmObject, attributeInfo) == null && cAttribute.getChildren().size() == 1) {
            // Fill fixed value (if any) if the attribute is singular, empty and has only one constraint.
            CObject cObject = cAttribute.getChildren().get(0);
            if (cObject instanceof CPrimitiveObject) {
                Object fixedValue = getPrimitiveFixedValue((CPrimitiveObject<?, ?>) cObject);
                if (fixedValue != null) {
                    setValue(rmObject, attributeInfo, fixedValue);
                }
            }
        } else {
            // Recursively fill fixed values for the exising values
            for (CObject cObject : cAttribute.getChildren()) {
                List<RMObjectWithPath> values;
                values = queryCache.getApathQuery("/" + rmAttributeName + "[" + cObject.getNodeId() + "]").findList(lookup, rmObject);

                for (RMObjectWithPath rmObjectWithPath : values) {
                    fillFixedValues(rmObjectWithPath.getObject(), cObject);
                }
            }
        }
    }

    private Object getPrimitiveFixedValue(CPrimitiveObject<?, ?> cobject) {
        Object fixedValue = getFixedValue(cobject);
        if(fixedValue != null) {
            return lookup.convertConstrainedPrimitiveToRMObject(fixedValue);
        } else {
            return null;
        }
    }

    private static Object getFixedValue(CPrimitiveObject<?, ?> cObject) {
        if(cObject instanceof COrdered) {
            return getFixedValueForOrdered((COrdered<?>) cObject);
        } else if (cObject instanceof CString) {
            return getFixedValueForString((CString) cObject);
        }
        return null;
    }

    private static Object getFixedValueForString(CString cString) {
        List<String> constraint = cString.getConstraint();
        if(constraint != null && constraint.size() == 1) {
            String singleConstraint = constraint.get(0);
            if(!CString.isRegexConstraint(singleConstraint)) {
                return singleConstraint;
            }
        }
        return null;
    }

    private static <T> Object getFixedValueForOrdered(COrdered<T> cOrdered) {
        List<Interval<T>> constraint = cOrdered.getConstraint();
        if(constraint != null && constraint.size() == 1) {
            Interval<T> interval = constraint.get(0);
            if(!interval.isLowerUnbounded() && !interval.isUpperUnbounded() &&
                    interval.isLowerIncluded() && interval.isUpperIncluded()
                    && Objects.equals(interval.getLower(), interval.getUpper())) {
                return interval.getLower();
            }
        }
        return null;
    }

    private Object getValue(Object rmObject, RMAttributeInfo attributeInfo) throws IllegalArgumentException {
        try {
            return attributeInfo.getGetMethod().invoke(rmObject);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void setValue(Object rmObject, RMAttributeInfo attributeInfo, Object value) throws IllegalArgumentException {
        try {
            attributeInfo.getSetMethod().invoke(rmObject, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
