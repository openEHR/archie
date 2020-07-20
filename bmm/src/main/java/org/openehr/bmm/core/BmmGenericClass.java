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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Definition of a generic class in an object model.
 *
 * Created by cnanjo on 4/11/16.
 */
public class BmmGenericClass extends BmmClass implements Serializable {

    /**
     * List of generic parameter definitions, keyed by name of generic parameter; these are defined either directly on
     * this class or by the addition of an ancestor class which is generic.
     */
    private Map<String, BmmParameterType> genericParameters;

    public BmmGenericClass(String aName, String aDocumentation, Boolean abstractFlag) {
        super(aName, aDocumentation, abstractFlag);
        genericParameters = new LinkedHashMap<>();
    }

    public BmmGenericClass() {
        super();
    }

    /**
     * Returns shallow cloned list of generic parameter definitions; these are defined either directly on
     * this class or by the addition of an ancestor class which is generic.
     *
     * @return
     */
    public List<BmmParameterType> getGenericParametersList() {
        return new ArrayList<>(genericParameters.values());
    }

    /**
     * Returns shallow cloned list of generic parameter definitions; these are defined either directly on
     * this class or by the addition of an ancestor class which is generic.
     *
     * @return
     */
    public Map<String, BmmParameterType> getGenericParameters() {
        return genericParameters;
    }

    /**
     * Returns the internal bmm generic property map for this class.
     *
     * @return
     */
    public Map<String, BmmParameterType> getGenericParameterIndex() {
        return genericParameters;
    }

    /**
     * Sets list of generic parameter definitions, keyed by name of generic parameter; these are defined either directly
     * on this class or by the addition of an ancestor class which is generic.
     *
     *
     * @param parameters
     */
    public void setGenericParameters(Map<String, BmmParameterType> parameters) {
        genericParameters = parameters;
    }

    /**
     * Adds generic parameter definition to this generic class.
     *
     * @param genericParameter
     */
    public void addGenericParameter(BmmParameterType genericParameter) {
        genericParameters.put(genericParameter.getName().toUpperCase(), genericParameter);
    }

    /**
     * Returns the generic parameter with the name provided.
     *
     * @param genericParameterName
     */
    public BmmParameterType getGenericParameter(String genericParameterName) {
        return genericParameters.get(genericParameterName.toUpperCase());
    }

    /**
     * Removes the generic parameter from the set of generic parameters defined for this class if
     * the parameter exists in the collection.
     *
     * @param genericParameter
     * @return
     */
    public BmmParameterType removeGenericParameter(BmmParameterType genericParameter) {
        return genericParameters.remove(genericParameter.getName().toUpperCase());
    }

    /**
     * Removes a generic parameter of that name from the set of generic parameters defined for this class if a parameter
     * entry of that name exists in the collection.
     *
     * @param parameterName
     * @return
     */
    public BmmParameterType removeGenericParameter(String parameterName) {
        return genericParameters.remove(parameterName.toUpperCase());
    }

    /**
     * Returns a type object corresponding to this class.
     *
     * @return
     */
    @Override
    public BmmGenericType getType() {
        BmmGenericType result = new BmmGenericType(this);
        genericParameters.forEach((paramName, param) -> {
            result.addGenericParameter(param);
        });
        return result;
    }

    /**
     * Add suppliers from generic parameters.
     *
     * @return
     */
    public List<String> getSuppliers() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     *
     * @return
     */
    @Override
    public BmmGenericClass duplicate() {
        BmmGenericClass target = (BmmGenericClass)super.duplicate();
        target.setGenericParameters (this.getGenericParameters());
        return target;
    }
}
