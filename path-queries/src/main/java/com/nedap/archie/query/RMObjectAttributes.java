package com.nedap.archie.query;

import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.rminfo.RMAttributeInfo;

import java.lang.reflect.InvocationTargetException;

/**
 * @deprecated Use {@link com.nedap.archie.rminfo.AttributeAccessor}
 */
@Deprecated
public class RMObjectAttributes {
    /**
     * Get the value of an attribute of a RM object.
     *
     * @param object        RM object.
     * @param attributeName The name of the attribute.
     * @return The value of the attribute.
     * @throws IllegalArgumentException When no attribute exists with the given attribute name.
     * @deprecated Use {@link com.nedap.archie.rminfo.AttributeAccessor#getValue(Object, String)}
     */
    @Deprecated
    public static Object getAttributeValueFromRMObject(Object object, String attributeName, ModelInfoLookup modelInfoLookup) {
        Object result;

        RMAttributeInfo attributeInfo = modelInfoLookup.getAttributeInfo(object.getClass(), attributeName);

        if (attributeInfo == null) {
            throw new IllegalArgumentException("Attribute does not exist in RM object");
        }

        try {
            result = attributeInfo.getGetMethod().invoke(object);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
