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

/**
 * Concrete unitary type, i.e. not a formal parameter type.
 *
 */
public abstract class BmmEffectiveType extends BmmUnitaryType {

    /**
     * Return the effective conformance type, taking into account formal parameter types.
     *
     * @return
     */
    public BmmEffectiveType getEffectiveType() {
        return this;
    }

    /**
     * Return the type base-name (i.e. no generic parameters), which is normally the name
     * of the base class (also with no formal generics), or may be a built-in type name.
     */
    public abstract String typeBaseName();

}
