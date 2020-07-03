package org.openehr.bmm.core;

/*
 * #%L
 * OpenEHR - Java Model Stack
 * %%
 * Copyright (C) 2020 openEHR Foundation
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
 * Author: Thomas Beale
 */

import java.util.List;

/**
 * Abstract meta-type for types 'defined' in a BMM model, compared to formal parameter types,
 * built-in types. See subtypes.
 *
 */
public abstract class BmmDefinedType extends BmmUnitaryType {

    protected BmmClass baseClass;

    /**
     * Returns the target type; this converts to the first parameter in generic_parameters in BMM_GENERIC_TYPE.
     *
     * @return the base class
     */
    public BmmClass getBaseClass() {
        return baseClass;
    }

    /**
     * Sets the target type; this converts to the first parameter in generic_parameters in BMM_GENERIC_TYPE.
     */
    public void setBaseClass(BmmClass baseClass) {
        this.baseClass = baseClass;
    }

    /**
     * Return the effective conformance type, taking into account formal parameter types.
     *
     * @return
     */
    public BmmDefinedType getConformanceType() {
        return this;
    }

}
