package com.nedap.archie.diff;


import com.nedap.archie.aom.CPrimitiveObject;

import java.util.Objects;

public class PrimitiveObjectEqualsChecker {

    public static boolean isEqual(CPrimitiveObject<?, ?> childCObject, CPrimitiveObject<?, ?> childCObjectInParent) {
        if(childCObject == null && childCObjectInParent != null) {
            return false;
        }
        if(childCObjectInParent == null && childCObject != null) {
            return false;
        }
        if(childCObject == null && childCObjectInParent == null) {
            return true;
        }
        if(!Objects.equals(childCObject.getClass(), childCObjectInParent.getClass())) {
            return false;
        }
        return Objects.equals(childCObject.getConstraint(), childCObjectInParent.getConstraint());

    }
}
