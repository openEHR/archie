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
import java.util.List;
import java.util.stream.Collectors;

/**
 * Type reference based on a generic class, e.g. 'HashTable &lt;List &lt;Packet&gt;, String&gt;'
 *
 * Created by cnanjo on 4/11/16.
 */
public class BmmGenericType extends BmmType<BmmGenericClass> implements Serializable {

    public BmmGenericType() {
        genericParameters = new ArrayList<>();
    }

    /**
     * Generic parameters of the root_type in this type specifier. The order must match the order of the owning class’s
     * formal generic parameter declarations.
     */
    public List<BmmType> genericParameters;

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
        StringBuilder builder = new StringBuilder();
        if(getBaseClass() != null) {
            builder.append(getBaseClass().getName());
            builder.append("<");
            builder.append(genericParameters.stream().map( t -> t.getTypeName()).collect(Collectors.joining(",")));
            builder.append(">");
        } else {
            builder.append("No base class defined for type");
        }
        return builder.toString();
    }


    @Override
    public String toDisplayString() {return getTypeName();}
}
