package org.openehr.bmm.v2.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.openehr.bmm.BmmConstants;
import org.openehr.bmm.core.*;
import org.openehr.bmm.v2.validation.converters.BmmClassProcessor;

import java.util.ArrayList;
import java.util.List;

public class PBmmContainerType extends PBmmType<BmmContainerType> {

    private String containerType;
    private PBmmUnitaryType typeDef;
    private String type;

    public String getContainerType() {
        return containerType;
    }

    public void setContainerType(String containerType) {
        this.containerType = containerType;
    }

    public PBmmUnitaryType getTypeDef() {
        return typeDef;
    }

    public void setTypeDef(PBmmUnitaryType typeDef) {
        this.typeDef = typeDef;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Effective unitary type, ignoring containers and also generic parameters
     */
    @Override
    public String baseType() {
        if (type != null)
            return type;
        else
            return typeDef.baseType();
    }

    /**
     * Formal name of the type for display.
     *
     * @return
     */
    @Override
    public String asTypeString() {
        return containerType + BmmConstants.Generic_left_delim + getTypeRef().asTypeString() + BmmConstants.Generic_right_delim;
    }

    @Override
    public List<String> flattenedTypeList() {
        List<String> retVal = new ArrayList<>();
        retVal.add(containerType);
        if (getTypeRef() != null) {
            retVal.addAll(getTypeRef().flattenedTypeList());
        }
        return retVal;
    }

    /**
     * Get the type reference to the contained type
     * @return
     */
    @JsonIgnore
    public PBmmUnitaryType getTypeRef() {
        if (typeDef != null)
            return typeDef;
        else if (type != null) {
            if (BmmConstants.formalGenericParameterName(type))
                return new PBmmOpenType(type);
            else
                return new PBmmSimpleType(type);
        }
        else
            return null;
    }

    @Override
    public BmmContainerType createBmmType(BmmModel bmmModel, BmmClass classDefinition) {
        BmmClass containerClassDef = bmmModel.getClassDefinition(containerType);
        PBmmUnitaryType containedType = getTypeRef();
        if (containerClassDef instanceof BmmGenericClass && containedType != null) {
            BmmType containedBmmType = containedType.createBmmType(bmmModel, classDefinition);
            if (containedBmmType instanceof BmmUnitaryType) {
                return new BmmContainerType((BmmUnitaryType) containedBmmType, (BmmGenericClass) containerClassDef);
            }
        }

        throw new RuntimeException("BmmClass " + containerClassDef.getName() + " is not defined in this model or not a generic type");
    }

}
