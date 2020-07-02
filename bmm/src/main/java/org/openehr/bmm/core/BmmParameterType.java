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


import java.io.Serializable;

/**
 * Definition of a generic parameter in a class definition of a generic type.
 *
 * Created by cnanjo on 4/11/16.
 */
public class BmmParameterType extends BmmTypeElement implements Serializable {

    /**
     * Name of the parameter, e.g. 'T' etc.
     */
    private String name;
    /**
     * Optional conformance constraint that must be another valid class name.
     */
    private BmmClass conformsToType;
    /**
     * If set, is the corresponding generic parameter definition in an ancestor class.
     */
    private BmmParameterType inheritancePrecursor;

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
    public BmmClass getConformsToType() {
        return conformsToType;
    }

    /**
     * Sets the optional conformance constraint that must be another valid class name.
     *
     * @param conformsToType
     */
    public void setConformsToType(BmmClass conformsToType) {
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
    public String flattenedConformsToType() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Generate ultimate conformance type, which is either from `conforms_to_type' or if not set, 'Any'.
     *
     * @return
     */
    public BmmClass effectiveConformsToType() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Result is name of conformance type, else 'Any'.
     *
     * @return
     */
    public String getConformanceTypeName() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Signature form of the open type, including constrainer type if there is one, e.g. 'T:Ordered'.
     *
     * @return
     */
    public String getTypeSignature() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

