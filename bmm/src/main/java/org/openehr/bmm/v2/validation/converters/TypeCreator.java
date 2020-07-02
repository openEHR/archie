package org.openehr.bmm.v2.validation.converters;

import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmContainerType;
import org.openehr.bmm.core.BmmGenericClass;
import org.openehr.bmm.core.BmmParameterType;
import org.openehr.bmm.core.BmmGenericType;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.core.BmmOpenType;
import org.openehr.bmm.core.BmmSimpleType;
import org.openehr.bmm.core.BmmType;
import org.openehr.bmm.v2.persistence.PBmmContainerType;
import org.openehr.bmm.v2.persistence.PBmmGenericType;
import org.openehr.bmm.v2.persistence.PBmmOpenType;
import org.openehr.bmm.v2.persistence.PBmmSimpleType;
import org.openehr.bmm.v2.persistence.PBmmType;

public class TypeCreator {

    public BmmType createBmmType(PBmmType typeDef, BmmModel schema, BmmClass bmmClass) {
        if(typeDef == null) {
            return null;
        }
        if(typeDef instanceof PBmmSimpleType) {
            return createSimpleType((PBmmSimpleType) typeDef, schema);
        } else if (typeDef instanceof PBmmGenericType) {
            return createGenericType((PBmmGenericType) typeDef, schema);
        } else if (typeDef instanceof PBmmContainerType) {
            return createContainerType((PBmmContainerType) typeDef, schema, bmmClass);
        } else if (typeDef instanceof PBmmOpenType) {
            return createOpenType((PBmmOpenType) typeDef, schema, bmmClass);
        } else {
            throw new RuntimeException("unknown type found: " + typeDef.getClass().getName());
        }
    }

    private BmmType createOpenType(PBmmOpenType typeDef, BmmModel schema, BmmClass bmmClass) {
        BmmParameterType genericParameter = ((BmmGenericClass) bmmClass).getGenericParameter(typeDef.getType());
        if(bmmClass instanceof BmmGenericClass && genericParameter != null) {
            BmmOpenType openType = new BmmOpenType();
            openType.setGenericConstraint(genericParameter);
            return openType;
        } else {
            throw new RuntimeException("Unable to initialize BmmOpenType of type " + typeDef.getType() + ". Have you defined generic parameters in the class definition " + bmmClass.getName() + " for type " + typeDef.getType() + "?");
        }
    }

    private BmmType createContainerType(PBmmContainerType typeDef, BmmModel schema, BmmClass bmmClass) {
        PBmmContainerType containerType = typeDef;
        BmmContainerType bmmContainerType = new BmmContainerType();
        BmmType containedType = createBmmType(containerType.getTypeRef(), schema, bmmClass);

        BmmClass containerClass = schema.getClassDefinition(containerType.getContainerType());
        if(containerClass == null) {
            throw new RuntimeException("Container type is null for " + containerType.getContainerType());
        } else {
            bmmContainerType.setContainerType(containerClass);
        }
        bmmContainerType.setBaseType(containedType);
        return bmmContainerType;

    }

    private BmmType createSimpleType(PBmmSimpleType typeDef, BmmModel schema) {
        BmmClass baseClass = schema.getClassDefinition(typeDef.getType());
        if(baseClass == null) {
            //Shouldn't happen: validation already tests this, so runtime exception is fine!
            throw new RuntimeException("BmmClass " + typeDef.getType() + " is not defined in this model");
        } else {
            BmmSimpleType simpleType = new BmmSimpleType();
            simpleType.setBaseClass(baseClass);
            return simpleType;
        }
    }

    private BmmType createGenericType(PBmmGenericType typeDef, BmmModel schema) {
        PBmmGenericType pGenericType = typeDef;
        BmmGenericType genericType = new BmmGenericType();
        BmmClass classDefinition = schema.getClassDefinition(pGenericType.getRootType());
        if(classDefinition != null && classDefinition instanceof BmmGenericClass) {
            BmmGenericClass baseClass = (BmmGenericClass)schema.getClassDefinition(pGenericType.getRootType());
            genericType.setBaseClass(baseClass);
            for(PBmmType param: pGenericType.getGenericParameterDefs().values()) {

                BmmType paramBmmType = createBmmType(param, schema, classDefinition);
                genericType.addGenericParameter(paramBmmType);
            }
        } else {
            throw new RuntimeException("BmmClass " + pGenericType.getRootType() + " is not defined in this model or not a generic type");
        }
        return genericType;
    }
}
