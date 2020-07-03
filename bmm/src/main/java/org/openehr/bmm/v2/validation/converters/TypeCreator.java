package org.openehr.bmm.v2.validation.converters;

import org.openehr.bmm.core.*;
import org.openehr.bmm.v2.persistence.PBmmContainerType;
import org.openehr.bmm.v2.persistence.PBmmGenericType;
import org.openehr.bmm.v2.persistence.PBmmOpenType;
import org.openehr.bmm.v2.persistence.PBmmSimpleType;
import org.openehr.bmm.v2.persistence.PBmmType;

public class TypeCreator {

    /*
     * TODO: This should probably be refactored into the various PBmmXxxType classes
     */
    public BmmType createBmmType(PBmmType typeDef, BmmModel schema, BmmClass bmmClass) {
        if (typeDef == null)
            return null;

        if (typeDef instanceof PBmmSimpleType)
            return createSimpleType((PBmmSimpleType) typeDef, schema);
        else if (typeDef instanceof PBmmGenericType)
            return createGenericType((PBmmGenericType) typeDef, schema);
        else if (typeDef instanceof PBmmContainerType)
            return createContainerType((PBmmContainerType) typeDef, schema, bmmClass);
        else if (typeDef instanceof PBmmOpenType)
            return createParameterType((PBmmOpenType) typeDef, schema, bmmClass);
        else
            throw new RuntimeException("unknown type found: " + typeDef.getClass().getName());
    }

    private BmmUnitaryType createParameterType(PBmmOpenType typeDef, BmmModel schema, BmmClass bmmGenericClass) {
        if(bmmGenericClass instanceof BmmGenericClass) {
            BmmParameterType genericParameter = ((BmmGenericClass) bmmGenericClass).getGenericParameter(typeDef.getType());
                return genericParameter;
        } else {
            throw new RuntimeException("Unable to initialize BmmParameterType of type " + typeDef.getType() +
                    ". Have you defined generic parameters in the class definition " + bmmGenericClass.getName() +
                    " for type " + typeDef.getType() + "?");
        }
    }

    private BmmContainerType createContainerType(PBmmContainerType pContainerType, BmmModel schema, BmmClass bmmClass) {
        BmmContainerType bmmContainerType = new BmmContainerType();
        BmmUnitaryType containedType = (BmmUnitaryType) createBmmType(pContainerType.getTypeRef(), schema, bmmClass);

        BmmClass containerClass = schema.getClassDefinition(pContainerType.getContainerType());
        if(containerClass == null) {
            throw new RuntimeException("Container type is null for " + pContainerType.getContainerType());
        } else {
            bmmContainerType.setContainerType(containerClass);
        }
        bmmContainerType.setBaseType(containedType);
        return bmmContainerType;

    }

    private BmmSimpleType createSimpleType(PBmmSimpleType typeDef, BmmModel schema) {
        BmmClass baseClass = schema.getClassDefinition(typeDef.getType());
        if (baseClass instanceof BmmSimpleClass) {
            BmmSimpleType result = new BmmSimpleType((BmmSimpleClass) baseClass);
            result.setBaseClass(baseClass);
            return result;
        }
        else {
            //Shouldn't happen: validation already tests this, so runtime exception is fine!
            throw new RuntimeException("BmmClass " + typeDef.getType() + " is not defined in this model");
        }
    }

    private BmmGenericType createGenericType(PBmmGenericType typeDef, BmmModel schema) {
        BmmClass classDefinition = schema.getClassDefinition(typeDef.getRootType());
        if (classDefinition instanceof BmmGenericClass) {
            BmmGenericClass baseClass = (BmmGenericClass)schema.getClassDefinition(typeDef.getRootType());
            BmmGenericType result = new BmmGenericType(baseClass);
            for (PBmmType param: typeDef.getGenericParameterDefs().values()) {
                BmmType paramBmmType = createBmmType(param, schema, classDefinition);
                result.addGenericParameter(paramBmmType);
            }
            return result;
        }
        else
            throw new RuntimeException("BmmClass " + typeDef.getRootType() + " is not defined in this model or not a generic type");
    }
}
