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

import java.util.List;

/**
 * Type reference that specifies containers with one generic parameter.
 *
 * Created by cnanjo on 4/11/16.
 */
public class BmmContainerType extends BmmType {

    /**
     * The type of the container. This converts to the root_type in BMM_GENERIC_TYPE.
     */
    private BmmGenericClass containerType;

    /**
     * The type of the contained item
     */
    private BmmUnitaryType baseType;

    public BmmContainerType (BmmUnitaryType aBaseType, BmmGenericClass aContainerClass) {
        baseType = aBaseType;
        containerType = aContainerClass;
    }

    public BmmContainerType () {}

    /**
     * Returns the type of the container. This converts to the root_type in BMM_GENERIC_TYPE.
     *
     * @return
     */
    public BmmGenericClass getContainerType() {
        return containerType;
    }

    /**
     * Sets the type of the container. This converts to the root_type in BMM_GENERIC_TYPE.
     *
     * @param containerType
     */
    public void setContainerType(BmmGenericClass containerType) {
        this.containerType = containerType;
    }

    /**
     * Returns the target type; this converts to the first parameter in generic_parameters in BMM_GENERIC_TYPE.
     *
     * @return
     */
    public BmmUnitaryType getBaseType() {
        return baseType;
    }

    /**
     * Sets the target type; this converts to the first parameter in generic_parameters in BMM_GENERIC_TYPE.
     *
     * @param baseType
     */
    public void setBaseType(BmmUnitaryType baseType) {
        this.baseType = baseType;
    }

    /**
     * Return full type name, e.g. 'List&lt;ELEMENT&gt;'.
     *
     * @return
     */
    @Override
    public String getTypeName() {
        return containerType.getName() +
                BmmDefinitions.GENERIC_LEFT_DELIMITER + baseType.getTypeName() + BmmDefinitions.GENERIC_RIGHT_DELIMITER;
    }

    /**
     * Returns the completely flattened list of type names, flattening out all generic parameters.
     *
     * @return base class name
     */
    @Override
    public List<String> getFlattenedTypeList() {
        return baseType.getFlattenedTypeList();
    }

    /**
     * Return the effective conformance type, taking into account formal parameter types.
     *
     * @return
     */
    public BmmEffectiveType getEffectiveType() {
        return baseType.getEffectiveType();
    }

    /**
     * Returns the effective unitary type, i.e. abstracting away any containers.
     *
     * @return
     */
    @Override
    public BmmUnitaryType getUnitaryType() {
        return baseType;
    }

    @Override
    public String toDisplayString() {return getTypeName();}
}
