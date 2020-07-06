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


import org.openehr.bmm.persistence.validation.BasicDefinitions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Definition of a generic parameter in a class definition of a generic type.
 *
 * Created by cnanjo on 4/11/16.
 */
public class BmmParameterType extends BmmUnitaryType implements Serializable {

    /**
     * Name of the parameter, e.g. 'T' etc.
     */
    private String name;

    /**
     * Optional conformance constraint that must be another valid class name.
     */
    private BmmDefinedType conformsToType;

    /**
     * If set, is the corresponding generic parameter definition in an ancestor class.
     */
    private BmmParameterType inheritancePrecursor;

    /**
     * set from constructor
     */
    private BmmDefinedType AnyTypeDefinition;

    /**
     * Return type_name.
     *
     * @return the type name
     */
    @Override
    public String getTypeName() {
        return this.name;
    }

    /**
     * Returns the name of the parameter, e.g. 'T' etc.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the parameter, e.g. 'T' etc.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the optional conformance constraint that must be another valid class name.
     *
     * @return
     */
    public BmmDefinedType getConformsToType() {
        return conformsToType;
    }

    /**
     * Sets the optional conformance constraint that must be another valid class name.
     *
     * @param conformsToType
     */
    public void setConformsToType(BmmDefinedType conformsToType) {
        this.conformsToType = conformsToType;
    }

    /**
     * If set, returns the corresponding generic parameter definition in an ancestor class.
     *
     * @return
     */
    public BmmParameterType getInheritancePrecursor() {
        return inheritancePrecursor;
    }

    /**
     * Sets the corresponding generic parameter definition in an ancestor class.
     *
     * @param inheritancePrecursor
     */
    public void setInheritancePrecursor(BmmParameterType inheritancePrecursor) {
        this.inheritancePrecursor = inheritancePrecursor;
    }

    /**
     * Get any ultimate type conformance constraint on this generic parameter due to inheritance.
     *
     * @return
     */
    public BmmDefinedType flattenedConformsToType() {
        if (conformsToType != null)
            return conformsToType;
        else if (inheritancePrecursor != null)
            return inheritancePrecursor.flattenedConformsToType();
        else
            return null;
    }

    /**
     * Return the effective conformance type, taking into account formal parameter types.
     *
     * @return
     */
    @Override
    public BmmDefinedType getConformanceType() {
        BmmDefinedType confType = flattenedConformsToType();
        if (confType != null)
            return confType;
        else
            return anyTypeDefinition;
    }

    /**
     * Signature form of the open type, including constrainer type if there is one, e.g. 'T:Ordered'.
     *
     * @return
     */
    @Override
    public String getTypeSignature() {
        StringBuilder builder = new StringBuilder();
        builder.append(name);
        BmmDefinedType confType = flattenedConformsToType();
        if (confType != null) {
            builder.append(":" + confType.getTypeName());
        }
        return builder.toString();
    }

    /**
     * Returns the completely flattened list of type names, flattening out all generic parameters.
     *
     * @return base class name
     */
    @Override
    public List<String> getFlattenedTypeList() {
        ArrayList<String> result = new ArrayList<>();
        BmmDefinedType confType = flattenedConformsToType();
        if (confType != null)
            result.addAll(confType.getFlattenedTypeList());
        else
            result.add(BasicDefinitions.ANY_TYPE);
        return result;
    }

    @Override
    public String toDisplayString() {
        return null;
    }
}

