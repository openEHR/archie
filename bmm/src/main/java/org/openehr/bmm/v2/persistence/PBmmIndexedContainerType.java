package org.openehr.bmm.v2.persistence;

import org.openehr.bmm.core.*;
import org.openehr.bmm.persistence.validation.BmmDefinitions;
import org.openehr.bmm.v2.validation.converters.BmmClassProcessor;

import java.util.ArrayList;
import java.util.List;

public class PBmmIndexedContainerType extends PBmmContainerType {

    private String indexType;

    public String getIndexType() {
        return indexType;
    }

    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    @Override
    public BmmIndexedContainerType createBmmType(BmmClassProcessor processor, BmmClass classDefinition) {
        BmmClass containerClassDef = processor.getClassDefinition (getContainerType());
        BmmClass indexClassDef = processor.getClassDefinition (indexType);
        PBmmUnitaryType containedType = getTypeRef(); //get the actual typeref for conversion
        if (containerClassDef instanceof BmmGenericClass &&
                indexClassDef instanceof BmmSimpleClass &&
                containedType != null)
        {
            BmmType containedBmmType = containedType.createBmmType(processor, classDefinition);
            if (containedBmmType instanceof BmmUnitaryType) {
                return new BmmIndexedContainerType((BmmUnitaryType) containedBmmType,
                        ((BmmSimpleClass) indexClassDef).getType(),
                        (BmmGenericClass) containerClassDef);
            }
        }

        throw new RuntimeException("BmmClass " + containerClassDef.getName() + " is not defined in this model or not an indexed container type");
    }
    @Override
    public String asTypeString() {
        return getContainerType() + BmmDefinitions.GENERIC_LEFT_DELIMITER +
                indexType + BmmDefinitions.GENERIC_SEPARATOR + getTypeRef().asTypeString() +
                BmmDefinitions.GENERIC_RIGHT_DELIMITER;
    }
    @Override
    public List<String> flattenedTypeList() {
        List<String> result = new ArrayList<>();
        result.add(getContainerType());
        result.add(indexType);
        if (getTypeRef() != null) {
            result.addAll(getTypeRef().flattenedTypeList());
        }
        return result;
    }
}
