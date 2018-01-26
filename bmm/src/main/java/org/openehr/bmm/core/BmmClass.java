package org.openehr.bmm.core;

/*
 * #%L
 * OpenEHR - Java Model Stack
 * %%
 * Copyright (C) 2016 - 2017 Cognitive Medical Systems
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 * Author: Claude Nanjo
 */

import org.openehr.bmm.BmmConstants;
import org.openehr.bmm.persistence.serializer.Serialize;
import org.openehr.bmm.persistence.validation.BmmDefinitions;
import org.openehr.odin.CompositeOdinObject;
import org.openehr.odin.OdinAttribute;
import org.openehr.odin.OdinObject;
import org.openehr.odin.StringObject;
import org.openehr.odin.utils.OdinSerializationUtils;

import java.io.Serializable;
import java.util.*;

/**
 * Definition of a class in an object model. A class is type that may be open or closed in terms of other types mentioned within.
 * <p>
 * Created by cnanjo on 4/11/16.
 */
public class BmmClass extends BmmClassifier implements Serializable {

    /**
     * Name of this class. Note that unlike UML, names of classes are just the root name, even if the class is generic.
     */
    private String name;
    /**
     * List of immediate inheritance parents.
     */
    private Map<String, BmmClass> ancestors;
    /**
     * Package this class belongs to.
     */
    private BmmPackage bmmPackage;
    /**
     * The BMM Schema containing this class
     */
    private BmmModel bmmModel;
    /**
     * List of attributes defined in this class.
     */
    private Map<String, BmmProperty> properties;
    /**
     * Reference to original source schema defining this class. Useful for UI tools to determine which original schema
     * file to open for a given class for manual editing.
     */
    private String sourceSchemaId;
    /**
     * List of immediate inheritance descendants.
     */
    private List<String> immediateDescendants;
    /**
     * True if this class is abstract in its model.
     */
    private boolean isAbstract;
    /**
     * True if this class is designated a primitive type within the overall type system of the schema.
     */
    private boolean isPrimitiveType;
    /**
     * True if this definition overrides a class of the same name in an included schema.
     */
    private boolean isOverride;
    private BmmClass flattenedClassCache;

    public BmmClass() {
        properties = new LinkedHashMap<String, BmmProperty>();
        ancestors = new LinkedHashMap<String, BmmClass>();
        immediateDescendants = new ArrayList<String>();
        properties = new LinkedHashMap<String, BmmProperty>();
    }

    public BmmClass(String name) {
        this();
        this.name = name;
    }

    /**
     * Returns the name of this class. Note that unlike UML, names of classes are just the root name, even if the class is generic.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this class. Note that unlike UML, names of classes are just the root name, even if the class is generic.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the list of immediate inheritance parents.
     *
     * @return
     */
    public Map<String, BmmClass> getAncestors() {
        return ancestors;
    }

    /**
     * Sets the list of immediate inheritance parents.
     *
     * @param ancestors
     */
    public void setAncestors(Map<String, BmmClass> ancestors) {
        this.ancestors = ancestors;
    }

    /**
     * Method adds ancestor to class.
     *
     * @param name
     * @param ancestor
     */
    public void addAncestor(String name, BmmClass ancestor) {
        ancestors.put(name, ancestor);
    }

    /**
     * Method adds ancestor to class. Ancestor must have a name.
     *
     * @param ancestor
     */
    public void addAncestor(BmmClass ancestor) {
        ancestors.put(ancestor.getName(), ancestor);
    }

    /**
     * Returns the package this class belongs to.
     *
     * @return
     */
    public BmmPackage getPackage() {
        return bmmPackage;
    }

    /**
     * Sets the package this class belongs to.
     *
     * @param bmmPackage
     */
    public void setPackage(BmmPackage bmmPackage) {
        this.bmmPackage = bmmPackage;
    }

    /**
     * Returns the list of attributes defined in this class.
     *
     * @return
     */
    public Map<String, BmmProperty> getProperties() {
        return properties;
    }

    /**
     * Sets the list of attributes defined in this class.
     *
     * @param properties
     */
    public void setProperties(Map<String, BmmProperty> properties) {
        this.properties = properties;
    }

    /**
     * Method adds property to class.
     *
     * @param property
     */
    public void addProperty(BmmProperty property) {
        properties.put(property.getName(), property);
    }

    /**
     * Returns the reference to original source schema defining this class. Useful for UI tools to determine which
     * original schema file to open for a given class for manual editing.
     *
     * @return
     */
    public String getSourceSchemaId() {
        return sourceSchemaId;
    }

    /**
     * Sets the reference to original source schema defining this class. Useful for UI tools to determine which original
     * schema file to open for a given class for manual editing.
     *
     * @param sourceSchemaId
     */
    public void setSourceSchemaId(String sourceSchemaId) {
        this.sourceSchemaId = sourceSchemaId;
    }

    /**
     * Method returns the list of immediate inheritance descendants.
     *
     * @return
     */
    public List<String> getImmediateDescendants() {
        return immediateDescendants;
    }

    /**
     * Method sets the list of immediate inheritance descendants.
     *
     * @param immediateDescendants
     */
    public void setImmediateDescendants(List<String> immediateDescendants) {
        this.immediateDescendants = immediateDescendants;
    }

    /**
     * Method adds immediate descendant for this class.
     *
     * @param immediateDecendant
     */
    public void addImmediateDescendant(String immediateDecendant) {
        this.immediateDescendants.add(immediateDecendant);
    }

    /**
     * returns true if this class is abstract in its model.
     *
     * @return
     */
    public boolean isAbstract() {
        return isAbstract;
    }

    /**
     * Sets true if this class is abstract in its model.
     *
     * @param anAbstract
     */
    public void setAbstract(boolean anAbstract) {
        isAbstract = anAbstract;
    }

    /**
     * Returns True if this class is designated a primitive type within the overall type system of the schema.
     *
     * @return
     */
    public boolean isPrimitiveType() {
        return isPrimitiveType;
    }

    /**
     * Set to True if this class is designated a primitive type within the overall type system of the schema.
     *
     * @param primitiveType
     */
    public void setPrimitiveType(boolean primitiveType) {
        isPrimitiveType = primitiveType;
    }

    /**
     * Returns True if this definition overrides a class of the same name in an included schema.
     *
     * @return
     */
    public boolean isOverride() {
        return isOverride;
    }

    /**
     * Set to True if this definition overrides a class of the same name in an included schema.
     *
     * @param override
     */
    public void setOverride(boolean override) {
        isOverride = override;
    }

    /**
     * Returns list of all inheritance parent class names, recursively.
     *
     * @return
     */
    public List<String> findAllAncestors() {
        List<String> allAncestors = new ArrayList<String>();
        Map<String, BmmClass> ancestors = getAncestors();
        allAncestors.addAll(ancestors.keySet());
        for(BmmClass ancestor:ancestors.values()) {
            allAncestors.addAll(ancestor.findAllAncestors());
        }
        return allAncestors;
    }

    /**
     * Compute all descendants by following immediate_descendants.
     *
     * @return
     */
    public List<String> findAllDescendants() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * List of names of immediate supplier classes, including concrete generic parameters, concrete descendants of
     * abstract statically defined types, and inherited suppliers. (Where generics are unconstrained, no class name is
     * added, since logically it would be 'ANY' and this can always be assumed anyway). This list includes primitive types.
     *
     * @return
     */
    public List<String> findSuppliers() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Same as `suppliers' minus primitive types, as defined in input schema.
     *
     * @return
     */
    public List<String> findSuppliersNonPrimitive() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * List of names of all classes in full supplier closure, including concrete generic parameters; (where generics
     * are unconstrained, no class name is added, since logically it would be 'ANY' and this can always be assumed
     * anyway). This list includes primitive types.
     *
     * @return
     */
    public List<String> findSupplierClosure() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Fully qualified package name, of form: 'package.package'.
     *
     * @return
     */
    public String getPackagePath() {
        if (bmmPackage != null) {
            return bmmPackage.getPath();
        } else {
            return null;
        }
    }

    /**
     * Fully qualified class name, of form: 'package.package.CLASS' with package path in lower-case and class in
     * original case.
     *
     * @return
     */
    public String getClassPath() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * List of all properties due to current and ancestor classes, keyed by property name.
     *
     * @return
     */
    public List<Map<String, BmmProperty<BmmType>>> getFlatProperties() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public BmmClass getBaseClass() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public String getTypeName() {
        return name;
    }

    public BmmPackage getBmmPackage() {
        return bmmPackage;
    }

    public void setBmmPackage(BmmPackage bmmPackage) {
        this.bmmPackage = bmmPackage;
    }

    public BmmModel getBmmModel() {
        return bmmModel;
    }

    public void setBmmModel(BmmModel bmmModel) {
        this.bmmModel = bmmModel;
    }

    /**
     * Method creates a child class that is the flattened version of the hierarchical structure.
     *
     */
    public BmmClass flattenBmmClass() {
        if(this.flattenedClassCache != null) {
            return flattenedClassCache;
        }
        Map<String, BmmClass> ancestorMap = this.getAncestors();
        if (ancestorMap.size() == 0) {
            flattenedClassCache = duplicate();
        } else {
            final BmmClass target = this.duplicate();
            target.setAncestors(new HashMap<String, BmmClass>());//Clear out ancestors since we are flattening the hierarchy.
            ancestorMap.forEach( (ancestorName, ancestor) -> { populateTarget(ancestor, target); });
            flattenedClassCache = target;
        }
        return flattenedClassCache;
    }

    public String effectivePropertyType(String propertyName) {
        BmmProperty property = flattenBmmClass().getProperties().get(propertyName);
        if(property != null) {
            return property.getType().getTypeName();
        } else {
            return BmmDefinitions.UNKNOWN_TYPE_NAME;
        }
    }

    protected void populateTarget(BmmClass source, BmmClass target) {
        Map<String, BmmProperty> propertyMap = source.getProperties();
        propertyMap.values().forEach(property -> handleFlattenedProperty(property, target));
        source.getAncestors().values().forEach(ancestor -> populateTarget(ancestor, target));
    }

    protected void handleFlattenedProperty(BmmProperty property, BmmClass target) {
        if (target.hasPropertyWithName(property.getName())) {
            //this is fine, it has been validated to be conformant and just overrides the old property
        } else {
            target.addProperty(property);
        }
    }

    /**
     * Creates a shallow clone of the class.
     *
     * @return
     */
    public BmmClass duplicate() {
        BmmClass target = null;
        if(this instanceof BmmGenericClass) {
            target = new BmmGenericClass();
        } else {
            target = new BmmClass();
        }
        target.setName(this.getName());
        target.getProperties().putAll(this.getProperties());
        target.setAbstract(this.isAbstract);
        target.setSourceSchemaId(this.getSourceSchemaId());
        target.getAncestors().putAll(this.getAncestors());
        target.setOverride(this.isOverride);
        target.setPrimitiveType(this.isPrimitiveType);
        target.setPackage(this.getPackage());
        return target;
    }

    public Boolean hasPropertyWithName(String propertyName) {
        return properties.get(propertyName) != null;
    }

    public String toString() {
        return name;
    }
}
