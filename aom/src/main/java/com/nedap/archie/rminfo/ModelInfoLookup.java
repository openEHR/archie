package com.nedap.archie.rminfo;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.base.MultiplicityInterval;
import com.nedap.archie.apath.PathSegment;
import com.nedap.archie.apath.APathQuery;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ModelInfoLookup {

    /**
     * Get the java class corresponding to the rm type name for this reference model
     * @param rmTypename the rm type name
     * @return the class in the implementation
     */
    Class<?> getClass(String rmTypename);

    /**
     * Return the default class to be created for a type name
     * If this typename is abstract, MUST return a default class to be created. This allows the RMObjectCreator to work.
     * For the class Event in the OpenEHR RM for example, this can return the POINT_EVENT class
     * @param rmTypename the rm type name
     * @return the class from which an instance should be created
     */
    Class<?> getClassToBeCreated(String rmTypename);

    /**
     * Returns a map from rm type name to java class
     * @return a map from rm type name to java class
     */
    Map<String, Class<?>> getRmTypeNameToClassMap();

    /**
     * Get RM Type info for a specific class
     * @param clazz
     * @return
     */
    RMTypeInfo getTypeInfo(Class<?> clazz);

    /**
     * Get the java reflection Field of an attribute of a certain class
     * @param clazz the class to lookup the attribute for
     * @param attributeName the attribute name
     * @return the java Field, or null if the attribute does not exist
     */
    Field getField(Class<?> clazz, String attributeName);

    /**
     * Get the type info for the given RM Type Name
     * @param rmTypeName
     * @return The type info, or null if the class does not exist in the model
     */
    RMTypeInfo getTypeInfo(String rmTypeName);

    /**
     * Get AttributeInfo for the given class and attribute
     * @param clazz the class
     * @param attributeName the attribute name
     * @return the attribute info, or null if it does not exist
     */
    RMAttributeInfo getAttributeInfo(Class<?> clazz, String attributeName);

    /**
     * Get AttributeInfo for the given typename and attribute
     * @param rmTypeName the reference model type name
     * @param attributeName the attribute name
     * @return the attribute info, or null if either the class or attribute does not exist
     */
    RMAttributeInfo getAttributeInfo(String rmTypeName, String attributeName);

    /**
     * Returns a list of all known types
     * @return a list of all knowns types
     */
    List<RMTypeInfo> getAllTypes();

    /**
     * Returns the naming strategy for the java classes of this model
     * @return
     */
    ModelNamingStrategy getNamingStrategy();

    /**
     * Converts the given rm object with the constraint in cPrimitiveObject to the corresponding AOM model
     *
     * For example, converts an OpenEHR RM CodePhrase to a TerminologyCode
     *
     * @param object the rm object
     * @param cPrimitiveObject the AOM constraint
     * @return the rm object converted to the corresponding AOM object
     */
    Object convertToConstraintObject(Object object, CPrimitiveObject<?, ?> cPrimitiveObject);


    /**
     * Converts from an AOM object to the corresponding reference model object
     * For example, converts a TerminologyCode to a CodePhrase
     * @param object
     * @return
     */
    Object convertConstrainedPrimitiveToRMObject(Object object);

    /**
     * Callback after an RM Object has been created based on a constraint. Can for example be used
     * to set names or archetype ID Node values
     */
    void processCreatedObject(Object createdObject, CObject constraint);


    /**
     * Get the archetype node id from a reference model object. Used in for example path queries.
     * Returns null if the reference model object does not have a node id.
     * @param rmObject
     * @return
     */
    String getArchetypeNodeIdFromRMObject(Object rmObject);

    String getArchetypeIdFromArchetypedRmObject(Object rmObject);

    /**
     * Get the name/meaning from the rmObject. Used in path queries with names instead of id codes.
     * Return null if no name is available
     * @param rmObject
     * @return
     */
    String getNameFromRMObject(Object rmObject);

    /**
     * Deeply clone the given RM Object
     * @param rmObject
     * @return
     */
    Object clone(Object rmObject);

    /**
     * Perform any actions necessary if the value at the given path has just been updated
     * For example, if an ordinal value has been set, this method should also set the symbol.
     *
     * In addition to changing the actual values, it returns which additional paths have been updated as well.
     * For example, if an ordinal's symbol was updated, it will update both the value and the symbol of that ordinal
     * and return the value's path and updated value. This is done to obtain a full set of instructions of what must be
     * changed due to the rule evaluation.
     *
     * This can be the most complex operation of this entire class to implement. If you just throw an exception instead of implementing it
     * everything will work fine except for the rule evaluation.
     *
     * For now this is only needed in the rule evaluation to automatically fix assertions
     *
     * @param rmObject
     * @param archetype
     * @param pathOfParent
     * @param parent
     * @return Each key is a path that was updated as a result of the previously updated path and each corresponding
     * value is this path's updated value
     */
    Map<String, Object> pathHasBeenUpdated(Object rmObject, Archetype archetype, String pathOfParent, Object parent);

    /**
     * True if the given attribute at given type is ok for given CPrimitiveObject, false otherwise
     * TODO: this should be solved with AOM_PROFILE
     * @param rmTypeName the type name of the type to checjk
     * @param rmAttributeName the attribute name of the attribute to check
     * @param cObject the primitive object to validate
     * @return true if valid, false if not valid
     */
    boolean validatePrimitiveType(String rmTypeName, String rmAttributeName, CPrimitiveObject<?, ?> cObject);

    Collection<RMPackageId> getId();

    /**
     * Pass this method to cObject.effectiveOccurrences to get the reference model property multiplicity
     * @param rmTypeName the type name of the type to get occurrences for
     * @param rmAttributeNameOrPath The rm attribute name or path lookup to an rm attribute to get the occurrences for
     * @return
     */
    default MultiplicityInterval referenceModelPropMultiplicity(String rmTypeName, String rmAttributeNameOrPath) {
        RMTypeInfo typeInfo = this.getTypeInfo(rmTypeName);
        String rmAttributeName = rmAttributeNameOrPath;
        if(rmAttributeNameOrPath.contains("[") || rmAttributeNameOrPath.contains("/")) {
            APathQuery aPathQuery = new APathQuery(rmAttributeNameOrPath);
            for(int i = 0; i < aPathQuery.getPathSegments().size()-1; i++) {
                PathSegment segment = aPathQuery.getPathSegments().get(i);
                RMAttributeInfo attributeInfo = typeInfo.getAttribute(segment.getNodeName());
                typeInfo = this.getTypeInfo(attributeInfo.getTypeNameInCollection());
            }
            rmAttributeName = aPathQuery.getPathSegments().get(aPathQuery.getPathSegments().size()-1).getNodeName();
        }
        RMAttributeInfo attributeInfo = typeInfo.getAttribute(rmAttributeName);
        if(attributeInfo.isMultipleValued()) {
            return MultiplicityInterval.createUpperUnbounded(0);
        } else {
            if(attributeInfo.isNullable()) {
                return MultiplicityInterval.createBounded(0, 1);
            } else {
                return MultiplicityInterval.createBounded(1, 1);
            }
        }
    }

    /**
     * Pass this method to cObject.cConformsTo to enable it to check if the two type names are conformant
     * @param childType
     * @param parentType
     * @return
     */
    default Boolean rmTypesConformant(String childType, String parentType) {
        RMTypeInfo parentTypeInfo = getTypeInfo(parentType);
        RMTypeInfo childTypeInfo = getTypeInfo(childType);
        if (childTypeInfo == null || parentTypeInfo == null) {
            return true;//cannot check with RM types, will validate elsewhere
        }
        return childTypeInfo.isDescendantOrEqual(parentTypeInfo);
    }
}
