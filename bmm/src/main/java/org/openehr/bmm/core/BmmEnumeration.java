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
import java.util.List;

/**
 * Definition of an enumeration type. In the BMM system, an 'enumeration' type is understood as an underlying basic type
 * and a set of named constants of that type. It is designed so that the default type is Integer, and the default
 * constants are numbered 0, 1, …​ Optional model elements can be specified to override the values and / or the type.
 *
 * Created by cnanjo on 4/11/16.
 */
public class BmmEnumeration<T> extends BmmSimpleClass implements Serializable {

    /**
     * The list of names of the enumeration. If no values are supplied, the integer values 0, 1, 2, …​ are assumed.
     */
    private List<String> itemNames;
    /**
     * Optional list of specific values. Must be 1:1 with `items_names' list.
     */
    private List<T> itemValues;
    /**
     * Name of type any concrete BMM_ENUMERATION_* sub-type is based on, i.e. the name of type bound to 'T' in a
     * declared use of this type.
     */
    private String underlyingTypeName;

    public BmmEnumeration(String aName, String aDocumentation, Boolean abstractFlag) {
        super(aName, aDocumentation, abstractFlag);
    }

    public BmmEnumeration() {
        super();
    }

    /**
     * Returns the list of names of the enumeration. If no values are supplied, the integer values 0, 1, 2, …​ are assumed.
     *
     * @return
     */
    public List<String> getItemNames() {
        return itemNames;
    }

    /**
     * Sets the list of names of the enumeration. If no values are supplied, the integer values 0, 1, 2, …​ are assumed.
     *
     * @param itemNames
     */
    public void setItemNames(List<String> itemNames) {
        this.itemNames = itemNames;
    }

    /**
     * Returns optional list of specific values. Must be 1:1 with `items_names' list.
     *
     * @return
     */
    public List<T> getItemValues() {
        return itemValues;
    }

    /**
     * Sets optional list of specific values. Must be 1:1 with `items_names' list.
     *
     * @param itemValues
     */
    public void setItemValues(List<T> itemValues) {
        this.itemValues = itemValues;
    }

    /**
     * Returns name of type any concrete BMM_ENUMERATION_* sub-type is based on, i.e. the name of type bound to 'T' in a
     * declared use of this type.
     *
     * @return
     */
    public String getUnderlyingTypeName() {
        return underlyingTypeName;
    }

    /**
     * Sets the name of type any concrete BMM_ENUMERATION_* sub-type is based on, i.e. the name of type bound to 'T' in a
     * declared use of this type.
     *
     * @param underlyingTypeName
     */
    public void setUnderlyingTypeName(String underlyingTypeName) {
        this.underlyingTypeName = underlyingTypeName;
    }
}
