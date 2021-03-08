package org.openehr.bmm.v2.validation.validators;

import org.openehr.bmm.persistence.validation.BmmDefinitions;
import org.openehr.bmm.v2.persistence.PBmmClass;
import org.openehr.bmm.v2.persistence.PBmmContainerProperty;
import org.openehr.bmm.v2.persistence.PBmmGenericProperty;
import org.openehr.bmm.v2.persistence.PBmmProperty;
import org.openehr.bmm.v2.persistence.PBmmSchema;
import org.openehr.bmm.v2.persistence.PBmmSingleProperty;
import org.openehr.bmm.v2.persistence.PBmmSinglePropertyOpen;

import java.util.List;

public class ConformanceChecker {

    /**
     * True if `a_child_prop' conforms to `a_parent_prop' such that it could be used to override it; same types are not considered conforming
     *
     * @param aChildProperty
     * @param aParentProperty
     * @return
     */
    public boolean propertyConformsTo(PBmmSchema schema, PBmmProperty<?> aChildProperty, PBmmProperty<?> aParentProperty) {
        if(aParentProperty instanceof PBmmSingleProperty && ((PBmmSingleProperty) aParentProperty).getTypeRef().getType().equalsIgnoreCase(BmmDefinitions.ANY_TYPE)) {
            return true;
        }
        else if (aChildProperty.getName().equalsIgnoreCase(aParentProperty.getName())) {
            //Properties names are the same
            if (aChildProperty instanceof PBmmSingleProperty && aParentProperty instanceof PBmmSingleProperty) {
                PBmmSingleProperty aChildSingleProperty = (PBmmSingleProperty)aChildProperty;
                PBmmSingleProperty aParentSingleProperty = (PBmmSingleProperty)aParentProperty;
                return typeStrictlyConformsTo(schema, aChildSingleProperty.getTypeRef().getType(), aParentSingleProperty.getTypeRef().getType());
            }
            else if (aParentProperty instanceof PBmmSingleProperty) {
                if (aChildProperty instanceof PBmmSinglePropertyOpen) {
                    //If both properties have the same name and are both open properties, then they do not conform.
                    return false;
                }
                else if(aChildProperty instanceof PBmmSingleProperty) {
                    return true;
                    //TODO FIXME: proper type conformance to constraining generic type needs to be checked here
                }
            }
            else if (aChildProperty instanceof PBmmContainerProperty && aParentProperty instanceof PBmmContainerProperty) {
                PBmmContainerProperty aChildContainerProperty = (PBmmContainerProperty)aChildProperty;
                PBmmContainerProperty aParentContainerProperty = (PBmmContainerProperty)aParentProperty;
                return typeStrictlyConformsTo(schema, aChildContainerProperty.getTypeRef().asTypeString(), aParentContainerProperty.getTypeRef().asTypeString());
            }
            else if (aChildProperty instanceof PBmmGenericProperty && aParentProperty instanceof PBmmGenericProperty) {
                PBmmGenericProperty aChildGenericProperty = (PBmmGenericProperty)aChildProperty;
                PBmmGenericProperty aParentGenericProperty = (PBmmGenericProperty)aParentProperty;
                return typeStrictlyConformsTo(schema, aChildGenericProperty.getTypeRef().asTypeString(), aParentGenericProperty.getTypeRef().asTypeString());
            }
        }
        return false;
    }

    /**
     * check if type 1 and type 2 are identical; each type may be generic
     *
     * @param type1
     * @param type2
     * @return
     */
    public boolean typeSameAs(String type1, String type2) {
        return BmmDefinitions.typeNameAsFlatList(type1).toString().equalsIgnoreCase(BmmDefinitions.typeNameAsFlatList(type2).toString());
    }

    /**
     * check conformance of type 1 to type 2 for substitutability; each type may be generic
     *
     * @param type1
     * @param type2
     * @return
     */
    public boolean typeConformsTo(PBmmSchema schema, String type1, String type2) {
        List<String> typeList1, typeList2;
        typeList1 = BmmDefinitions.typeNameAsFlatList(type1);
        typeList2 = BmmDefinitions.typeNameAsFlatList(type2);
        int index = 0;

        while (index < typeList1.size() && index < typeList2.size() &&
                schema.hasClassOrPrimitiveDefinition(typeList1.get(index)) &&
                schema.hasClassOrPrimitiveDefinition(typeList2.get(index))) {
            String typePart1 = typeList1.get(index);
            String typePart2 = typeList2.get(index);
            if (!(type1.equalsIgnoreCase(typePart2) || isAncestor(schema, typePart1, typePart2))) {
                return false;
            }
            index++;

        }
        return true;
    }

    /**
     * Return true if typePart2 is an ancestor of typePart1 in the given schema
     * @param schema
     * @param typePart1
     * @param typePart2
     * @return
     */
    public boolean isAncestor(PBmmSchema schema, String typePart1, String typePart2) {
        PBmmClass classDefinition = schema.getClassDefinition(typePart1);
        List<String> ancestors = classDefinition.getAncestorTypeNames();
        if (ancestors.contains(typePart2)) { //direct ancestor
            return true;
        }
        for (String ancestor:ancestors) {
            if (isAncestor(schema, typePart1, ancestor)) { //recursive check
                return true;
            }
        }
        return false;
    }

    /**
     * check conformance of type 1 to type 2 for redefinition; each type may be generic
     *
     * @param type1
     * @param type2
     * @return
     */
    public boolean typeStrictlyConformsTo(PBmmSchema schema, String type1, String type2) {
        return typeSameAs(type1,type2) || typeConformsTo(schema, type1, type2);
    }
}
