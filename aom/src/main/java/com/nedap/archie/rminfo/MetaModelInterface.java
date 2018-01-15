package com.nedap.archie.rminfo;

import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.base.MultiplicityInterval;

public interface MetaModelInterface {
    boolean isMultiple(String typeName, String attributeName);

    boolean rmTypesConformant(String childTypeName, String parentTypeName);

    boolean typeNameExists(String typeName);

    boolean attributeExists(String rmTypeName, String propertyName);

    boolean isNullable(String typeId, String attributeName);

    boolean typeConformant(String rmTypeName, String rmAttributeName, String childConstraintTypeName);

    boolean hasReferenceModelPath(String rmTypeName, String path);

    MultiplicityInterval referenceModelPropMultiplicity(String rmTypeName, String rmAttributeName);

    boolean validatePrimitiveType(String rmTypeName, String rmAttributeName, CPrimitiveObject cObject);
}
