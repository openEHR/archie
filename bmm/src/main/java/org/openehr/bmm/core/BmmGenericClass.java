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
import java.util.stream.Collectors;

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
    private Map<String, BmmGenericParameter> genericParameters;

    public BmmGenericClass() {
        super();
        genericParameters = new LinkedHashMap<>();
    }

    public BmmGenericClass(String name) {
        this();
        setName(name);
    }

    /**
     * Returns shallow cloned list of generic parameter definitions; these are defined either directly on
     * this class or by the addition of an ancestor class which is generic.
     *
     * @return
     */
    public List<BmmGenericParameter> getGenericParameters() {
        return new ArrayList<>(genericParameters.values());
    }

    /**
     * Returns the internal bmm generic property map for this class.
     *
     * @return
     */
    public Map<String, BmmGenericParameter> getGenericParameterIndex() {
        return genericParameters;
    }

    /**
     * Sets list of generic parameter definitions, keyed by name of generic parameter; these are defined either directly
     * on this class or by the addition of an ancestor class which is generic.
     *
     *
     * @param parameters
     */
    public void setGenericParameters(List<BmmGenericParameter> parameters) {
        this.genericParameters.clear();
        parameters.forEach(param -> {
            this.genericParameters.put(param.getName().toUpperCase(), param);
        });
    }

    /**
     * Adds generic parameter definition to this generic class.
     *
     * @param genericParameter
     */
    public void addGenericParameter(BmmGenericParameter genericParameter) {
        genericParameters.put(genericParameter.getName().toUpperCase(), genericParameter);
    }

    /**
     * Returns the generic parameter with the name provided.
     *
     * @param genericParameterName
     */
    public BmmGenericParameter getGenericParameter(String genericParameterName) {
        return genericParameters.get(genericParameterName.toUpperCase());
    }

    /**
     * Removes the generic parameter from the set of generic parameters defined for this class if
     * the parameter exists in the collection.
     *
     * @param genericParameter
     * @return
     */
    public BmmGenericParameter removeGenericParameter(BmmGenericParameter genericParameter) {
        return genericParameters.remove(genericParameter.getName().toUpperCase());
    }

    /**
     * Removes a generic parameter of that name from the set of generic parameters defined for this class if a parameter
     * entry of that name exists in the collection.
     *
     * @param parameterName
     * @return
     */
    public BmmGenericParameter removeGenericParameter(String parameterName) {
        return genericParameters.remove(parameterName.toUpperCase());
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
     * Signature form of the type, which for generics includes generic parameter constrainer types
     * E.g. Interval&lt;T:Ordered&gt;
     *
     * @return
     */
    @Override
    public String getTypeSignature() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Formal string form of the type as per UML.
     *
     * @return
     */
    @Override
    public String getTypeName() {
        List<BmmGenericParameter> params = getGenericParameters();
        String paramString = params.stream().map(i -> i.getName()).collect(Collectors.joining(", "));
        StringBuilder builder = new StringBuilder(getName()).append("<").append(paramString).append(">");
        return builder.toString();
    }

    /**
     *
     * @return
     */
    @Override
    public BmmGenericClass duplicate() {
        BmmGenericClass target = (BmmGenericClass)super.duplicate();
        target.setGenericParameters(this.getGenericParameters());
        return target;
    }
}
