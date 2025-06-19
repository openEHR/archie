package org.openehr.bmm.core;

import org.openehr.bmm.persistence.validation.BmmDefinitions;

public class BmmIndexedContainerType extends BmmContainerType {

    /**
     * The type of the index
     */
    private BmmSimpleType indexType;
    public BmmIndexedContainerType (BmmUnitaryType aBaseType, BmmSimpleType anIndexType, BmmGenericClass aContainerClass) {
        super (aBaseType, aContainerClass);
        indexType = anIndexType;
    }
    public BmmSimpleType getIndexType() { return indexType; }

    @Override
    public String getTypeName() {
        return getContainerType().getName() +
                BmmDefinitions.GENERIC_LEFT_DELIMITER +
                indexType.getTypeName() + BmmDefinitions.GENERIC_SEPARATOR +
                getBaseType().getTypeName() +
                BmmDefinitions.GENERIC_RIGHT_DELIMITER;
    }

}
