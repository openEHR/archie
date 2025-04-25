package com.nedap.archie.rminfo;

import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.base.MultiplicityInterval;

/**
 * @deprecated Use {@link MetaModel} instead.
 */
@Deprecated
public interface MetaModelInterface {
    /**
     * determine if a property on a type is multiple or not. If the property cannot be found, returns false.
     * Works both on properties on a type, or on path based lookup.
     */
    boolean isMultiple(String typeName, String rmAttributeNameOrPath);

    boolean rmTypesConformant(String childTypeName, String parentTypeName);

    boolean typeNameExists(String typeName);

    boolean attributeExists(String rmTypeName, String propertyName);

    boolean isNullable(String typeId, String attributeName);

    boolean typeConformant(String rmTypeName, String rmAttributeName, String childConstraintTypeName);

    boolean hasReferenceModelPath(String rmTypeName, String path);

    MultiplicityInterval referenceModelPropMultiplicity(String rmTypeName, String rmAttributeName);

    boolean validatePrimitiveType(String rmTypeName, String rmAttributeName, CPrimitiveObject<?, ?> cObject);

    boolean isOrdered(String rmTypeName, String rmAttributeName);
}
