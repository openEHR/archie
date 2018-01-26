package org.openehr.bmm.persistence;

/*
 * #%L
 * OpenEHR - Java Model Stack
 * %%
 * Copyright (C) 2016 - 2017  Cognitive Medical Systems, Inc (http://www.cognitivemedicine.com).
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
import org.openehr.bmm.core.*;
import org.openehr.bmm.persistence.validation.BmmDefinitions;
import org.openehr.odin.CompositeOdinObject;
import org.openehr.odin.OdinAttribute;
import org.openehr.odin.OdinObject;
import org.openehr.odin.StringObject;
import org.openehr.odin.utils.OdinSerializationUtils;
import org.openehr.utils.common.CloneUtils;

import java.io.Serializable;
import java.util.*;

/**
 * Definition of persistent form of BMM_CLASS for serialisation to ODIN, JSON, XML etc.
 *
 * Created by cnanjo on 4/11/16.
 */
public class PersistedBmmClass extends PersistedBmmModelElement implements Serializable {

    /**
     * Name of this class.
     */
    private String name;
    /**
     * List of immediate inheritance parents.
     */
    private List<String> ancestors;
    /**
     * List of attributes defined in this class.
     */
    private Map<String, PersistedBmmProperty> properties;
    /**
     * True if this class is abstract in its model.
     */
    private boolean isAbstract;
    /**
     * True if this definition overrides a class of the same name in an included schema.
     */
    private boolean isOverride;
    /**
     * List of generic parameter definitions.
     */
    private Map<String, PersistedBmmGenericParameter> genericParameterDefinitions;
    /**
     * Reference to original source schema defining this class. Set during BMM_SCHEMA materialise. Useful for GUI tools
     * to enable user to edit the schema file containing a given class (i.e. taking into account that a class may be
     * in any of the schemas in a schema inclusion hierarchy).
     *
     */
    private String sourceSchemaId;
    /**
     * BMM_CLASS object build by create_bmm_class_definition and populate_bmm_class_definition.
     */
    private transient BmmClass bmmClass;
    /**
     * Unique id generated for later comparison during merging, in order to detect if two classes are the same.
     * Assigned in post-load processing.
     */
    private Integer uid;

    public PersistedBmmClass() {
        properties = new LinkedHashMap<>();
        ancestors = new ArrayList<>();
        genericParameterDefinitions = new LinkedHashMap<>();
    }

    public PersistedBmmClass(String name) {
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
    public List<String> getAncestors() {
        return ancestors;
    }

    /**
     * Sets the list of immediate inheritance parents.
     *
     * @param ancestors
     */
    public void setAncestors(List<String> ancestors) {
        this.ancestors = ancestors;
    }

    /**
     * Method adds ancestor to class.
     *
     * @param name
     */
    public void addAncestor(String name) {
        ancestors.add(name);
    }

    /**
     * Returns the list of attributes defined in this class.
     *
     * @return
     */
    public Map<String, PersistedBmmProperty> getProperties() {
        return properties;
    }

    /**
     * Sets the list of attributes defined in this class.
     *
     * @param properties
     */
    public void setProperties(Map<String, PersistedBmmProperty> properties) {
        this.properties = properties;
    }

    /**
     * Method adds property to class.
     *
     * @param property
     */
    public void addProperty(PersistedBmmProperty property) {
        if(properties.containsKey(property.getName())) { //Should this be uppercased?
            throw new IllegalArgumentException("Class already contains a property with name " + property.getName());
        } else {
            properties.put(property.getName(), property);
        }
    }

    /**
     * Returns the list of attributes defined in this class.
     *
     * @return
     */
    public PersistedBmmProperty getPropertyByName(String propertyName) {
        return properties.get(propertyName);
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
     * Returns list of generic parameter definitions.
     *
     * @return
     */
    public Map<String, PersistedBmmGenericParameter> getGenericParameterDefinitions() {
        return genericParameterDefinitions;
    }

    /**
     * Sets generic parameter definitions map.
     *
     * @param genericParameterDefinitions
     */
    public void setGenericParameterDefinitions(Map<String, PersistedBmmGenericParameter> genericParameterDefinitions) {
        this.genericParameterDefinitions = genericParameterDefinitions;
    }

    /**
     * Sets list of generic parameter definitions. The name of the parameter will be used as the key
     * in the underlying map entry.
     *
     * @param genericParameterDefinitions
     */
    public void setGenericParameterDefinitions(List<PersistedBmmGenericParameter> genericParameterDefinitions) {
        genericParameterDefinitions.forEach(E -> {
            this.genericParameterDefinitions.put(E.getName(), E);
        });
    }

    /**
     * Adds a generic parameter to this class.
     *
     * @param parameter
     */
    public void addGenericParameterDefinition(PersistedBmmGenericParameter parameter) {
        this.genericParameterDefinitions.put(parameter.getName(), parameter);
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
     * Returns BMM_CLASS object build by create_bmm_class_definition and populate_bmm_class_definition.
     *
     * @return
     */
    public BmmClass getBmmClass() {
        return bmmClass;
    }

    /**
     * Sets BMM_CLASS object built by create_bmm_class_definition and populate_bmm_class_definition.
     *
     * @param bmmClass
     */
    public void setBmmClass(BmmClass bmmClass) {
        this.bmmClass = bmmClass;
    }

    /**
     * Returns unique id generated for later comparison during merging, in order to detect if two classes are the same.
     * Assigned in post-load processing.
     *
     * @return
     */
    public Integer getUid() {
        return uid;
    }

    /**
     * Sets unique id generated for later comparison during merging, in order to detect if two classes are the same.
     * Assigned in post-load processing.
     *
     * @param uid
     */
    public void setUid(Integer uid) {
        this.uid = uid;
    }

    /**
     * True if this class is a generic class.
     *
     * @return
     */
    public boolean isGeneric() {
        return this.genericParameterDefinitions != null && this.genericParameterDefinitions.size() > 0;
    }

    /**
     * Create bmm_class_definition.
     *
     * @return
     */
    public void createBmmClass() {
        if(genericParameterDefinitions != null && genericParameterDefinitions.size() > 0) {
            bmmClass = new BmmGenericClass(name);
        } else {
            bmmClass = new BmmClass(name);
        }
        bmmClass.setDocumentation(getDocumentation());
        bmmClass.setAbstract(isAbstract());
    }

    /**
     * Add remaining model elements from Current to `bmm_class_definition'.
     *
     * @param schema
     */
    public void populateBmmClass(BmmModel schema) {
        if(bmmClass != null) {
            if(ancestors != null) {
                ancestors.forEach(ancestor -> {
                    if(schema.getClassDefinition(ancestor) != null) {
                        bmmClass.addAncestor(schema.getClassDefinition(ancestor));
                    } else {
                        throw new RuntimeException("Error retrieving class definition for " + ancestor);
                    }
                });
            }
            if(bmmClass instanceof BmmGenericClass && genericParameterDefinitions != null) {
                genericParameterDefinitions.values().forEach(param ->{
                    param.createBmmGenericParameter(schema);
                    if(param.getBmmGenericParameter() != null) {
                        ((BmmGenericClass) bmmClass).addGenericParameter(param.getBmmGenericParameter());
                    }
                });
            }
            if(bmmClass.getProperties() != null) {
                properties.values().forEach(property -> {
                    property.createBmmProperty(schema, getBmmClass());
                    BmmProperty propertyDef = property.getBmmProperty();
                    if(propertyDef != null) {
                        bmmClass.addProperty(propertyDef);
                    } else {
                        throw new RuntimeException("Property could not be created for " + property.getName() + " in class " + getBmmClass().getName());
                    }
                });
            }
        } else {
            throw new RuntimeException("The class " + getName() + " is null. It may have been defined as a class or a primitive but not included in a package");
        }
    }

    public PersistedBmmClass deepClone() {
        return (PersistedBmmClass) CloneUtils.deepClone(this);
    }

    public String toString() {
        return name;
    }
}
