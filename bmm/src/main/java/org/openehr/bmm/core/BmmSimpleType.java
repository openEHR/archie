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

/**
 * Type reference to a single type i.e. not generic or container type.
 *
 * Created by cnanjo on 4/11/16.
 */
public class BmmSimpleType extends BmmDefinedType implements Serializable {

    public BmmSimpleType (BmmSimpleClass baseClass) {
        super(baseClass);
    }

    @Override
    public BmmSimpleClass getBaseClass() {
        return (BmmSimpleClass) super.getBaseClass();
    }

    /**
     * Return base_class.type_name.
     *
     * @return the type name
     */
    @Override
    public String getTypeName() {
        return getBaseClass().getName();
    }

    /**
     * Returns the completely flattened list of type names, flattening out all generic parameters.
     *
     * @return base class name
     */
    @Override
    public List<String> getFlattenedTypeList() {
        ArrayList<String> result = new ArrayList<>();
        result.add(getBaseClass().getName());
        return result;
    }

    @Override
    public String toDisplayString() {return getTypeName();}
}
