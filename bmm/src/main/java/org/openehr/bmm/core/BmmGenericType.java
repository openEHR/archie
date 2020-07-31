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

import org.openehr.bmm.persistence.validation.BmmDefinitions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Type reference based on a generic class, e.g. 'HashTable &lt;List &lt;Packet&gt;, String&gt;'
 *
 * Created by cnanjo on 4/11/16.
 */
public class BmmGenericType extends BmmDefinedType implements Serializable {

    /**
     * Generic parameters of the root_type in this type specifier. The order must match the order of the owning class’s
     * formal generic parameter declarations.
     */
    public List<BmmType> genericParameters;

    public BmmGenericType(BmmGenericClass baseClass) {
        super(baseClass);
        genericParameters = new ArrayList<>();
    }

    /**
     * Returns the target type; this converts to the first parameter in generic_parameters in BMM_GENERIC_TYPE.
     *
     * @return the base class
     */
    @Override
    public BmmGenericClass getBaseClass() {
        return (BmmGenericClass) super.getBaseClass();
    }

    /**
     * Returns generic parameters of the root_type in this type specifier. The order must match the order of the owning
     * class’s formal generic parameter declarations.
     *
     * @return
     */
    public List<BmmType> getGenericParameters() {
        return genericParameters;
    }

    /**
     * Sets generic parameters of the root_type in this type specifier. The order must match the order of the owning
     * class’s formal generic parameter declarations.
     *
     * @param genericParameters
     */
    public void setGenericParameters(List<BmmType> genericParameters) {
        this.genericParameters = genericParameters;
    }

    /**
     * Adds a generic parameter to the generic type definition.
     *
     * @param genericParameter
     */
    public void addGenericParameter(BmmType genericParameter) {
        this.genericParameters.add(genericParameter);
    }

    /**
     * Return the full name of the type including generic parameters, e.g. 'DV_INTERVAL&lt;T&gt;', 'TABLE&lt;List&lt;THING&gt;,String&gt;'.
     *
     * @return
     */
    @Override
    public String getTypeName() {
        return getBaseClass().getName() +
                BmmDefinitions.GENERIC_LEFT_DELIMITER +
                genericParameters.stream().map(t -> t.getTypeName()).collect(Collectors.joining(BmmDefinitions.GENERIC_SEPARATOR.toString())) +
                BmmDefinitions.GENERIC_RIGHT_DELIMITER;
    }

    /**
     * Signature form of the open type, including constrainer type if there is one, e.g. 'T:Ordered'.
     *
     * @return
     */
    public String getTypeSignature() {
        return getBaseClass().getName() +
                BmmDefinitions.GENERIC_LEFT_DELIMITER +
                genericParameters.stream().map(t -> t.getTypeSignature()).collect(Collectors.joining(BmmDefinitions.GENERIC_SEPARATOR.toString())) +
                BmmDefinitions.GENERIC_RIGHT_DELIMITER;
    }

    /**
     * Returns the completely flattened list of type names, flattening out all generic parameters.
     *
     * @return
     */
    @Override
    public List<String> getFlattenedTypeList() {
        ArrayList<String> result = new ArrayList<>();
        result.add(getBaseClass().getName());
        for (BmmType g : genericParameters) {
            result.addAll(g.getFlattenedTypeList());
        }
        return result;
    }

    @Override
    public String toDisplayString() {return getTypeName();}
}
