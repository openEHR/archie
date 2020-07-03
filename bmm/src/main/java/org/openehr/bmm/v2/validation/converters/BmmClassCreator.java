package org.openehr.bmm.v2.validation.converters;

import org.openehr.bmm.core.*;
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
        if(pBmmClass instanceof PBmmEnumerationString)
            bmmClass = new BmmEnumerationString(pBmmClass.getName(), pBmmClass.getDocumentation(), pBmmClass.isAbstract());
        else if (pBmmClass instanceof PBmmEnumerationInteger)
            bmmClass = new BmmEnumerationInteger(pBmmClass.getName(), pBmmClass.getDocumentation(), pBmmClass.isAbstract());
        else if (pBmmClass.getGenericParameterDefs() != null && pBmmClass.getGenericParameterDefs().size() > 0)
            bmmClass = new BmmGenericClass(pBmmClass.getName(), pBmmClass.getDocumentation(), pBmmClass.isAbstract());
        else
            bmmClass = new BmmSimpleClass(pBmmClass.getName(), pBmmClass.getDocumentation(), pBmmClass.isAbstract());

        bmmClass.setDocumentation(pBmmClass.getDocumentation());
        bmmClass.setAbstract(pBmmClass.isAbstract() == null ? false : pBmmClass.isAbstract());
        bmmClass.setOverride(pBmmClass.isOverride() == null ? false : pBmmClass.isOverride());
        bmmClass.setSourceSchemaId(pBmmClass.getSourceSchemaId());
        return bmmClass;
    }

    public void populateBmmClass(PBmmClass pBmmClass, BmmModel schema) {

        BmmClass bmmClass = schema.getClassDefinition(pBmmClass.getName());
        if (bmmClass != null) {

            for (String ancestorTypeName : pBmmClass.getAncestorTypeNames()) {
                // FIXME: typeName will have generics included. BMM 2 does not support generics as ancestors,
                // so just throw away this information until migration to BMM 3
                BmmClass classDefinition = schema.getClassDefinition(BmmDefinitions.typeNameToClassKey(ancestorTypeName));
                if (classDefinition != null)
                    bmmClass.addAncestor(classDefinition);
                else
                    throw new RuntimeException("Error retrieving class definition for " + ancestorTypeName);
            }

            if (bmmClass instanceof BmmGenericClass && pBmmClass.getGenericParameterDefs() != null)
                for (PBmmGenericParameter param : pBmmClass.getGenericParameterDefs().values()) {
                    BmmParameterType bmmParameterType = createBmmGenericParameter(param, schema);
                    ((BmmGenericClass) bmmClass).addGenericParameter(bmmParameterType);
                }

            if (pBmmClass.getProperties() != null)
                for (PBmmProperty pBmmProperty: pBmmClass.getProperties().values()) {
                    BmmProperty propertyDef = new BmmPropertyCreator().createBmmProperty(pBmmProperty, schema, bmmClass);
                    bmmClass.addProperty(propertyDef);
                }
        }
        else
            throw new RuntimeException("The class " + pBmmClass.getName() + " is null. It may have been defined as a class or a primitive but not included in a package");

        if(pBmmClass instanceof PBmmEnumerationString)
            populateStringEnumeration((PBmmEnumerationString) pBmmClass, (BmmEnumerationString) bmmClass);
        else if (pBmmClass instanceof PBmmEnumerationInteger)
            populateIntegerEnumeration((PBmmEnumerationInteger) pBmmClass, (BmmEnumerationInteger) bmmClass);

    }

    private void populateIntegerEnumeration(PBmmEnumerationInteger pBmmClass, BmmEnumerationInteger bmmClass) {
        bmmClass.setItemNames(pBmmClass.getItemNames());
        bmmClass.setItemValues(pBmmClass.getItemValues());
        if (bmmClass.getItemValues() == null || bmmClass.getItemValues().isEmpty()) {
            //documentation says: for integers, the values 0, 1, 2, etc are assumed. I'm adding 'unless otherwise specified' here
            List<Integer> itemValues = new ArrayList<>();
            for(int i = 0; i < bmmClass.getItemNames().size(); i++) {
                itemValues.add(i);
            }
            bmmClass.setItemValues(itemValues);
        }
    }

    private void populateStringEnumeration(PBmmEnumerationString pBmmClass, BmmEnumerationString bmmClass) {
        bmmClass.setItemNames(pBmmClass.getItemNames());
        bmmClass.setItemValues(pBmmClass.getItemValues());
    }


    private BmmParameterType createBmmGenericParameter(PBmmGenericParameter pBmmParam, BmmModel bmmSchema) {
        BmmParameterType result = new BmmParameterType();
        result.setName (pBmmParam.getName());
        result.setDocumentation (pBmmParam.getDocumentation());

        if (pBmmParam.getConformsToType() != null) {
            BmmClass conformsToTypeClass = bmmSchema.getClassDefinition (pBmmParam.getConformsToType());
            if (conformsToTypeClass != null)
                result.setConformsToType (conformsToTypeClass.getType());
        }
        else {
            result.setBaseClass (bmmSchema.getAnyClassDefinition());
            result.setConformsToType(null);
        }

        return result;
    }


}
