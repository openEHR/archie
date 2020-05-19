package org.openehr.bmm.v2.validation.converters;

import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmEnumerationInteger;
import org.openehr.bmm.core.BmmEnumerationString;
import org.openehr.bmm.core.BmmGenericClass;
import org.openehr.bmm.core.BmmGenericParameter;
import org.openehr.bmm.core.BmmProperty;
import org.openehr.bmm.persistence.validation.BmmDefinitions;
import org.openehr.bmm.v2.persistence.PBmmClass;
import org.openehr.bmm.v2.persistence.PBmmEnumerationInteger;
import org.openehr.bmm.v2.persistence.PBmmEnumerationString;
import org.openehr.bmm.v2.persistence.PBmmGenericParameter;
import org.openehr.bmm.v2.persistence.PBmmProperty;

import java.util.ArrayList;
import java.util.List;

public class BmmClassCreator {

    public BmmClass createBmmClass(PBmmClass pBmmClass) {
        BmmClass bmmClass;
        if(pBmmClass instanceof PBmmEnumerationString) {
            bmmClass = new BmmEnumerationString(pBmmClass.getName());
        } else if (pBmmClass instanceof PBmmEnumerationInteger) {
            bmmClass = new BmmEnumerationInteger(pBmmClass.getName());
        } else if (pBmmClass.getGenericParameterDefs() != null && pBmmClass.getGenericParameterDefs().size() > 0) {
            bmmClass = new BmmGenericClass(pBmmClass.getName());
        } else {
            bmmClass = new BmmClass(pBmmClass.getName());
        }

        bmmClass.setDocumentation(pBmmClass.getDocumentation());
        bmmClass.setAbstract(pBmmClass.isAbstract() == null ? false : pBmmClass.isAbstract());
        bmmClass.setOverride(pBmmClass.isOverride() == null ? false : pBmmClass.isOverride());
        bmmClass.setSourceSchemaId(pBmmClass.getSourceSchemaId());
        return bmmClass;
    }

    public BmmClass populateBmmClass(PBmmClass pBmmClass, BmmClassProcessor schema) {

        BmmClass bmmClass = schema.getUnprocessedClassDefinition(pBmmClass.getName());
        if (bmmClass != null) {

            for (String ancestorTypeName : pBmmClass.getAncestorTypeNames()) {
                //typeName will have generics included. BMM 2 does not support generics as ancestors, so just throw away this information
                //until migration to BMM 3
                BmmClass classDefinition = schema.getClassDefinition(BmmDefinitions.typeNameToClassKey(ancestorTypeName));
                if (classDefinition != null) {
                    bmmClass.addAncestor(classDefinition);
                } else {
                    throw new RuntimeException("Error retrieving class definition for " + ancestorTypeName);
                }
            }

            if (bmmClass instanceof BmmGenericClass && pBmmClass.getGenericParameterDefs() != null) {
                for (PBmmGenericParameter param : pBmmClass.getGenericParameterDefs().values()) {
                    BmmGenericParameter bmmGenericParameter = createBmmGenericParameter(param, schema);
                    ((BmmGenericClass) bmmClass).addGenericParameter(bmmGenericParameter);
                }
            }
        } else {
            throw new RuntimeException("The class " + pBmmClass.getName() + " is null. It may have been defined as a class or a primitive but not included in a package");
        }

        if(pBmmClass instanceof PBmmEnumerationString) {

            populateStringEnumeration((PBmmEnumerationString) pBmmClass, (BmmEnumerationString) bmmClass);
        } else if (pBmmClass instanceof PBmmEnumerationInteger) {

            populateIntegerEnumeration((PBmmEnumerationInteger) pBmmClass, (BmmEnumerationInteger) bmmClass);
        }
        return bmmClass;
    }

    public BmmClass populateBmmClassProperties(PBmmClass pBmmClass, BmmClassProcessor schema) {
        BmmClass bmmClass = schema.getUnprocessedClassDefinition(pBmmClass.getName());
        if (bmmClass != null) {
            if (pBmmClass.getProperties() != null) {
                for (PBmmProperty property : pBmmClass.getProperties().values()) {
                    BmmProperty propertyDef = new BmmPropertyCreator().createBmmProperty(property, schema, bmmClass);
                    bmmClass.addProperty(propertyDef);
                }
            }
        } else {
            throw new RuntimeException("The class " + pBmmClass.getName() + " is null. It may have been defined as a class or a primitive but not included in a package");
        }
        return bmmClass;
    }

    private void populateIntegerEnumeration(PBmmEnumerationInteger pBmmClass, BmmEnumerationInteger bmmClass) {
        PBmmEnumerationInteger pEnumerationInteger = pBmmClass;
        BmmEnumerationInteger enumeration = bmmClass;
        enumeration.setItemNames(pEnumerationInteger.getItemNames());
        enumeration.setItemValues(pEnumerationInteger.getItemValues());
        if(enumeration.getItemValues() == null || enumeration.getItemValues().isEmpty()) {
            //documentation says: for integers, the values 0, 1, 2, etc are assumed. I'm adding 'unless otherwise specified' here
            List<Integer> itemValues = new ArrayList<>();
            for(int i = 0; i < enumeration.getItemNames().size(); i++) {
                itemValues.add(i);
            }
            enumeration.setItemValues(itemValues);
        }
    }

    private void populateStringEnumeration(PBmmEnumerationString pBmmClass, BmmEnumerationString bmmClass) {
        PBmmEnumerationString pEnumerationString = pBmmClass;
        BmmEnumerationString enumeration = bmmClass;
        enumeration.setItemNames(pEnumerationString.getItemNames());
        enumeration.setItemValues(pEnumerationString.getItemValues());
    }


    private BmmGenericParameter createBmmGenericParameter(PBmmGenericParameter param, BmmClassProcessor bmmSchema) {
        BmmGenericParameter bmmGenericParameter = new BmmGenericParameter();
        bmmGenericParameter.setName(param.getName());
        bmmGenericParameter.setDocumentation(param.getDocumentation());

        if(param.getConformsToType() != null) {
            BmmClass conformsToTypeClass = bmmSchema.getClassDefinition(param.getConformsToType());
            if(conformsToTypeClass != null) {
                bmmGenericParameter.setConformsToType(conformsToTypeClass);
            }
        } else {
            bmmGenericParameter.setBaseClass(bmmSchema.getAnyClassDefinition());
            bmmGenericParameter.setConformsToType(null);
        }

        return bmmGenericParameter;
    }


}
