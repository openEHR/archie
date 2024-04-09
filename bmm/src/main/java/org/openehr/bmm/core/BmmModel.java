package org.openehr.bmm.core;

import com.nedap.archie.apath.PathSegment;
import com.nedap.archie.apath.APathQuery;
import org.openehr.bmm.persistence.validation.BasicDefinitions;
import org.openehr.bmm.persistence.validation.BmmDefinitions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Copyright 2017 Cognitive Medical Systems, Inc (http://www.cognitivemedicine.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Created by cnanjo on 1/16/17.
 */

/**
 * Model of a BMM schema (along with what is inherited from BMM_SCHEMA_CORE).
 */
public class BmmModel extends BmmPackageContainer implements IBmmSchemaCore, IBmmPackageContainer, Serializable {

    /**
     * All classes in this schema.
     */
    private Map<String, BmmClass> classDefinitions;
    private IBmmSchemaCore bmmSchemaCore;

    public BmmModel() {
        this.bmmSchemaCore = new BmmSchemaCore();
        this.classDefinitions = new HashMap<>();
    }

    public Map<String, BmmClass> getClassDefinitions() {
        return classDefinitions;
    }

    public void setClassDefinitions(Map<String, BmmClass> classDefinitions) {
        this.classDefinitions = classDefinitions;
    }

    public void addClassDefinition(BmmClass bmmClassDefinition) {
        this.classDefinitions.put(bmmClassDefinition.getName().toUpperCase(), bmmClassDefinition);
    }

    public void addClassDefinition(BmmClass bmmClassDefinition, BmmPackage packageDefinition) {
        if(getClassDefinition(bmmClassDefinition.getName()) == null) {// && recursiveHasPackage(packageDefinition)) {
            addClassDefinition(bmmClassDefinition);
            packageDefinition.addClass(bmmClassDefinition);
            bmmClassDefinition.setBmmPackage(packageDefinition);
            bmmClassDefinition.setBmmModel(this);
        }
    }

    /**
     * Retrieve the class definition corresponding to typeName (which may contain a generic part) or null
     * if the class is not found.
     *
     * @param typeName the typename to get the class definition for
     * @return the found class definition, or null if it cannot be found
     */
    public BmmClass getClassDefinition(String typeName) {
        return this.classDefinitions.get(BmmDefinitions.typeNameToClassKey(typeName));
    }

    public IBmmSchemaCore getBmmSchemaCore() {
        return bmmSchemaCore;
    }

    public void setBmmSchemaCore(IBmmSchemaCore bmmSchemaCore) {
        this.bmmSchemaCore = bmmSchemaCore;
    }

    /**
     * List of keys in `class_definitions' of items marked as primitive types, as defined in input schema.
     *
     * Note: no caching at this time. This list is created from scratch every time.
     *
     * @return a list of keys of primitive types
     */
    public List<String> getPrimitiveTypes() {
        List<String> primitives = new ArrayList<>();
        classDefinitions.forEach( (id, bmmClass) -> {
            if(bmmClass != null && bmmClass.isPrimitiveType()) {
                primitives.add(id);
            }
        });
        return primitives;
    }

    /**
     * List of keys in `class_definitions' of items that are enumeration types, as defined in input schema.
     *
     * Note: no caching at this time. This list is created from scratch every time.
     *
     * @return
     */
    public List<String> getEnumerationTypes() {
        List<String> enumerations = new ArrayList<>();
        classDefinitions.forEach( (id, bmmClass) -> {
            if(bmmClass != null && bmmClass instanceof BmmEnumeration<?>) {
                enumerations.add(id);
            }
        });
        return enumerations;
    }

    /**
     * Retrieve the enumeration definition corresponding to `a_type_name'.
     *
     * @param bmmEnumerationName
     * @return
     */
    public BmmEnumeration<?> getEnumerationDefinition(String bmmEnumerationName) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * True if `a_ms_property_type' is a valid 'MS' dynamic type for `a_property' in BMM type `a_bmm_type_name'.
     * 'MS' conformance means 'model-semantic' conformance, which abstracts away container types like List&lt;&gt;, Set&lt;&gt;
     *  etc and compares the dynamic type with the relation target type in the UML sense, i.e. regardless of whether
     *  there is single or multiple containment.
     *
     * @param bmmTypeName
     * @param bmmPropertyName
     * @param msPropertyName
     * @return
     */
    public boolean isMsConformantPropertyType(String bmmTypeName, String bmmPropertyName, String msPropertyName) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Return true if propertyPath exists within a type, including among flat properties
     * and descendant classes (i.e. compute dynamic attachment possibilities).
     * @param typeName : type within which to find the property
     * @param propertyPath : path
     * @return BmmProperty
     */
    public Boolean hasPropertyAtPath (String typeName, String propertyPath) {
        return propertyAtPath(typeName, propertyPath) != null;
    }

    /**
     * Return BmmProperty object at a path within a type, including among flat properties
     * and descendant classes (i.e. compute dynamic attachment possibilities).
     * precondition hasPropertyAtPath ()
     * @param typeName : type within which to find the property
     * @param propertyPath : path
     * @return BmmProperty
     */
    public BmmProperty<?> propertyAtPath (String typeName, String propertyPath) {
        BmmClass bmmClass = getClassDefinition(typeName);
        if (bmmClass != null) {
            return propertyAtPath (bmmClass, new APathQuery(propertyPath).getPathSegments());
        } else {
            return null;
        }
    }

    private BmmProperty<?> propertyAtPath (BmmClass bmmClass, List<PathSegment> pathSegments) {
        BmmProperty<?> result = null;
        String nodeName = pathSegments.get(0).getNodeName();
        if (bmmClass.hasFlatPropertyWithName (nodeName)) {
            if (pathSegments.size() == 1) {
                result = bmmClass.getFlatProperties().get(nodeName);
            } else {
                BmmClass bmmPropTypeClass = getClassDefinition (bmmClass.getFlatProperties().get(nodeName).getType().getEffectiveType().typeBaseName());
                if (bmmPropTypeClass != null) {
                    result = propertyAtPath (bmmPropTypeClass, pathSegments.subList(1, pathSegments.size()));
                }
            }
        } else {
            for (String descClass : bmmClass.getImmediateDescendants()) {
                BmmProperty<?> descClassProperty = propertyAtPath (getClassDefinition(descClass), pathSegments);
                if (descClassProperty != null) {
                    result = descClassProperty;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Return all ancestor types of `a_class_name' up to root class (usually 'ANY', 'Object' or something similar).
     * Does not include current class. Returns empty list if none.
     *
     * @param bmmClassName
     * @return
     */
    public List<String> getAllAncestorClasses (String bmmClassName) {
        Map<String, BmmClass> classMap = new LinkedHashMap<>();
        BmmClass bmmClass = getClassDefinition (bmmClassName);
        populateAllAncestorClassMap (bmmClass, classMap);
        return new ArrayList<>(classMap.keySet());
    }

    public Map<String, BmmClass> getAllAncestorClassObjects (BmmClass bmmClass) {
        Map<String, BmmClass> result = new LinkedHashMap<>();
        populateAllAncestorClassMap (bmmClass, result);
        return result;
    }

    protected void populateAllAncestorClassMap (BmmClass bmmClass, Map<String, BmmClass> classMap) {
        bmmClass.getAncestors().forEach((className, typeDef) -> {
            classMap.put (className, typeDef.getBaseClass());
            populateAllAncestorClassMap (typeDef.getBaseClass(), classMap);
        });
    }

    public Map<String, BmmClass> getAllDescendantClassObjects (BmmClass bmmClass) {
        Map<String, BmmClass> result = new LinkedHashMap<>();
        getClassDefinitions().forEach((className, classDef) -> {
            Map<String, BmmClass> allAncestors = getAllAncestorClassObjects(classDef);
            if(allAncestors.containsKey(bmmClass.getName())) {
                result.put(classDef.getName(), classDef);
            }
        });
        return result;
    }

    public BmmSimpleClass getAnyClassDefinition() {
        if (getClassDefinition(BasicDefinitions.ANY_TYPE) != null) {
            return (BmmSimpleClass) getClassDefinition(BasicDefinitions.ANY_TYPE);
        }
        BmmSimpleClass result = new BmmSimpleClass(BasicDefinitions.ANY_TYPE, null, false);
        result.setAbstract(true);
        result.setDocumentation("Root class of type system");
        return result;
    }

    public BmmSimpleType getAnyTypeDefinition() {
        return new BmmSimpleType(getAnyClassDefinition());
    }

    /**
     * Check descendant relation
     *
     * @param descendantType
     * @param ancestorType
     * @return
     */
    public boolean descendantOf (String descendantType, String ancestorType) {
        return getClassDefinition (descendantType).findAllAncestors().contains (ancestorType);
    }

    /**
     * Check conformance of `a_desc_type' to `an_anc_type'; the types may be generic, and may contain open generic
     * parameters like 'T' etc. These are replaced with their appropriate constrainer types, or Any during the conformance
     * testing process.
     *
     * Conformance is found if:
     *
     * [base class test] types are non-generic, and either type names are identical, or else `a_desc_type' has
     * `an_anc_type' in its ancestors both types are generic and pass base class test; number of generic params matches,
     * and each generic parameter type, after 'open parameter' substitution, recursively passes `type_name_conforms_to'
     * test descendant type is generic and ancestor type is not, and they pass base classes test
     *
     * @param descendantType
     * @param ancestorType
     * @return
     */
    public boolean doesTypeConformTo (String descendantType, String ancestorType) {
        throw new UnsupportedOperationException("Not yet implememented");
    }

    /**
     * Get names of all immediate supplier classes to the type aTypeName
     * @param typeName
     * @return
     */
    public Set<String> suppliers (String typeName) {
        BmmClass bmmClass = getClassDefinition(typeName);
        Set<String> result = new HashSet<>();

        for (BmmProperty<?> bmmProperty: bmmClass.getFlatProperties().values()) {
            List<String> ftl = bmmProperty.getType().getFlattenedTypeList();
            result.addAll(ftl);
            for (String type: ftl) {
                result.addAll(getClassDefinition(type).getImmediateDescendants());
            }
        }

        return result;
    }

    /**
     * Get names of all supplier classes in reachability closure to the type aTypeName
     * @param typeName
     * @return
     */
    public Set<String> supplierClosure (String typeName) {
        BmmClass bmmClass = getClassDefinition(typeName);
        Set<String> immediateSuppliers;
        Set<String> result = new HashSet<>();

        Set<String> closureTypesDone = new HashSet<>();
        closureTypesDone.add(bmmClass.getName());
        immediateSuppliers = suppliers(typeName);
        result.addAll(immediateSuppliers);

        for (String immSupp: immediateSuppliers) {
            if (!closureTypesDone.contains(immSupp)) {
                result.addAll(supplierClosure(immSupp));
                closureTypesDone.add(immSupp);
            }
        }

        return result;
    }

    public String effectivePropertyType(String typeName, String propertyName) {
        BmmClass bmmClass = getClassDefinition(typeName);
        if(bmmClass == null) {
            return BmmDefinitions.UNKNOWN_TYPE_NAME;
        }
        return bmmClass.effectivePropertyType(propertyName);
    }

    /****************************************************************************
     ***  From Bmm Schema Core
     ****************************************************************************/

    /**
     * Returns the publisher of model expressed in the schema.
     *
     * @return
     */
    @Override
    public String getRmPublisher () {
        return bmmSchemaCore.getRmPublisher();
    }

    /**
     * Sets the publisher of model expressed in the schema.
     *
     * @param rmPublisher
     */
    @Override
    public void setRmPublisher (String rmPublisher) {
        bmmSchemaCore.setRmPublisher(rmPublisher);
    }

    /**
     * Returns the release of model expressed in the schema as a 3-part numeric, e.g. "3.1.0" .
     *
     * @return
     */
    @Override
    public String getRmRelease () {
        return bmmSchemaCore.getRmRelease();
    }

    /**
     * Sets the release of model expressed in the schema as a 3-part numeric, e.g. "3.1.0" .
     *
     * @param rmRelease
     */
    @Override
    public void setRmRelease(String rmRelease) {
        bmmSchemaCore.setRmRelease(rmRelease);
    }

    /**
     * Returns the name of model expressed in schema; a 'schema' usually contains all of the packages of one 'model'
     * of a publisher. A publisher with more than one model can have multiple schemas. Persisted attribute.
     *
     * @return
     */
    @Override
    public String getSchemaName() {
        return bmmSchemaCore.getSchemaName();
    }

    /**
     * Sets the name of model expressed in schema; a 'schema' usually contains all of the packages of one 'model'
     * of a publisher. A publisher with more than one model can have multiple schemas. Persisted attribute.
     *
     * @param schemaName
     */
    @Override
    public void setSchemaName(String schemaName) {
        bmmSchemaCore.setSchemaName(schemaName);
    }

    /**
     * Returns the revision of schema.
     *
     * @return
     */
    @Override
    public String getSchemaRevision() {
        return bmmSchemaCore.getSchemaRevision();
    }

    /**
     * Sets the revision of schema.
     *
     * @param schemaRevision
     */
    @Override
    public void setSchemaRevision(String schemaRevision) {
        bmmSchemaCore.setSchemaRevision(schemaRevision);
    }

    /**
     * Returns the lifecycle state of the schema.
     *
     * @return
     */
    @Override
    public String getSchemaLifecycleState() {
        return bmmSchemaCore.getSchemaLifecycleState();
    }

    /**
     * Sets the lifecycle state of the schema.
     *
     * @param schemaLifecycleState
     */
    @Override
    public void setSchemaLifecycleState(String schemaLifecycleState) {
        bmmSchemaCore.setSchemaLifecycleState(schemaLifecycleState);
    }

    /**
     * Returns the primary author of schema.
     *
     * @return
     */
    @Override
    public String getSchemaAuthor() {
        return bmmSchemaCore.getSchemaAuthor();
    }

    /**
     * Sets the primary author of schema.
     *
     * @param schemaAuthor
     */
    @Override
    public void setSchemaAuthor(String schemaAuthor) {
        bmmSchemaCore.setSchemaAuthor(schemaAuthor);
    }

    /**
     * Returns the description of schema.
     *
     * @return
     */
    @Override
    public String getSchemaDescription() {
        return bmmSchemaCore.getSchemaDescription();
    }

    /**
     * Sets the description of schema.
     *
     * @param schemaDescription
     */
    @Override
    public void setSchemaDescription(String schemaDescription) {
        bmmSchemaCore.setSchemaDescription(schemaDescription);
    }

    /**
     * Returns the contributing authors of schema.
     *
     * @return
     */
    @Override
    public List<String> getSchemaContributors() {
        return bmmSchemaCore.getSchemaContributors();
    }

    /**
     * Sets the contributing authors of schema.
     *
     * @param schemaContributors
     */
    @Override
    public void setSchemaContributors(List<String> schemaContributors) {
        bmmSchemaCore.setSchemaContributors(schemaContributors);
    }

    /**
     * Returns the name of a parent class used within the schema to provide archetype capability, enabling filtering of
     * classes in RM visualisation. If empty, 'Any' is assumed. Persisted attribute.
     *
     * @return
     */
    @Override
    public String getArchetypeParentClass() {
        return bmmSchemaCore.getArchetypeParentClass();
    }

    /**
     * Sets the name of a parent class used within the schema to provide archetype capability, enabling filtering of
     * classes in RM visualisation. If empty, 'Any' is assumed. Persisted attribute.
     *
     * @param archetypeParentClass
     */
    @Override
    public void setArchetypeParentClass(String archetypeParentClass) {
        bmmSchemaCore.setArchetypeParentClass(archetypeParentClass);
    }

    /**
     * Returns the name of a parent class of logical 'data types' used within the schema to provide archetype capability, enabling
     * filtering of classes in RM visualisation. If empty, 'Any' is assumed. Persisted attribute.
     *
     * @return
     */
    @Override
    public String getArchetypeDataValueParentClass() {
        return bmmSchemaCore.getArchetypeDataValueParentClass();
    }

    /**
     * Sets the name of a parent class of logical 'data types' used within the schema to provide archetype capability,
     * enabling filtering of classes in RM visualisation. If empty, 'Any' is assumed. Persisted attribute.
     *
     * @param archetypeDataValueParentClass
     */
    @Override
    public void setArchetypeDataValueParentClass(String archetypeDataValueParentClass) {
        bmmSchemaCore.setArchetypeDataValueParentClass(archetypeDataValueParentClass);
    }

    /**
     * List of top-level package paths that provide the RM 'model' part in archetype identifiers, e.g. the path "org.openehr.ehr"
     * gives "EHR" in "openEHR-EHR". Within this namespace, archetypes can be based on any class reachable from classes defined directly in these packages.
     *
     * @return
     */
    @Override
    public List<String> getArchetypeRmClosurePackages() {
        return bmmSchemaCore.getArchetypeRmClosurePackages();
    }

    /**
     * Returns the list of top-level package paths that provide the RM 'model' part in achetype identifiers, e.g. the path
     * "org.openehr.ehr" gives "EHR" in "openEHR-EHR". Within this namespace, archetypes can be based on any class reachable
     * from classes defined directly in these packages.
     *
     * @param rmClosurePackages
     */
    @Override
    public void setArchetypeRmClosurePackages(List<String> rmClosurePackages) {
        bmmSchemaCore.setArchetypeRmClosurePackages(rmClosurePackages);
    }

    /**
     * Method adds a top-level package paths that provide the RM 'model' part in achetype identifiers, e.g. the path
     * "org.openehr.ehr" gives "EHR" in "openEHR-EHR". Within this namespace, archetypes can be based on any class reachable from classes defined directly in these packages.
     *
     * @param rmClosurePackage
     */
    @Override
    public void addArchetypeRmClosurePackage(String rmClosurePackage) {
        bmmSchemaCore.addArchetypeRmClosurePackage(rmClosurePackage);
    }

    /**
     * Method returns a class whose descendants should be made visible in tree and grid renderings of the archetype
     * definition, if archetype_parent_class is not set, designate . For openEHR and CEN this class is normally the
     * same as the archetype_parent_class, i.e. LOCATABLE and RECORD_COMPONENT respectively. It is typically set for CEN,
     * because archetype_parent_class may not be stated, due to demographic types not inheriting from it.
     *
     * @return
     */
    @Override
    public String getArchetypeVisualizeDescendantsOf() {
        return bmmSchemaCore.getArchetypeVisualizeDescendantsOf();
    }

    /**
     * Method a class whose descendants should be made visible in tree and grid renderings of the archetype
     * definition, if archetype_parent_class is not set, designate . For openEHR and CEN this class is normally the
     * same as the archetype_parent_class, i.e. LOCATABLE and RECORD_COMPONENT respectively. It is typically set for CEN,
     * because archetype_parent_class may not be stated, due to demographic types not inheriting from it.
     *
     * @param archetypeVisualizeDescendantsOf
     */
    @Override
    public void setArchetypeVisualizeDescendantsOf(String archetypeVisualizeDescendantsOf) {
        bmmSchemaCore.setArchetypeVisualizeDescendantsOf(archetypeVisualizeDescendantsOf);
    }

    /**
     * Returns the derived name of schema, based on model publisher, model name, model release.
     *
     * @return
     */
    public String getSchemaId() {
        return bmmSchemaCore.getSchemaId();
    }

    @Override
    public String getModelName() {
        return bmmSchemaCore.getModelName();
    }

    @Override
    public void setModelName(String modelName) {
        this.bmmSchemaCore.setModelName(modelName);
    }

}
