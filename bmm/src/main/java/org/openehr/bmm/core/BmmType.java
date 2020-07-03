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

import java.util.List;

/**
 * Abstract idea of specifying a type in some context. This is not the same as 'defining' a class. A type specification
 * is essentially a reference of some kind, that defines the type of an attribute, or function result or argument.
 * It may include generic parameters that might or might not be bound. See subtypes.
 *
 * Created by cnanjo on 4/11/16.
 */
public abstract class BmmType extends BmmEntity {

    /**
     * Main design class for this type, from which properties etc can be extracted.
     */
    private BmmClass baseClass;

    /**
     * Returns the base class
     *
     * @return
     */
    public BmmClass getBaseClass() {
        return baseClass;
    }

    /**
     * Sets the base class
     *
     * @param baseClass
     */
    public void setBaseClass(BmmClass baseClass) {
        this.baseClass = baseClass;
    }

    /**
     * Returns the formal string form of the type as per UML.
     *
     * @return a formal type name
     */
    public abstract String getTypeName();

    /**
     * Returns the signature form of the type, which for generics includes generic parameter constrainer types
     * E.g. Interval&lt;T:Ordered&gt;
     *
     * @return
     */
    public String getTypeSignature() {
        return getTypeName();
    }

    /**
     * Return the effective conformance type, taking into account formal parameter types.
     *
     * @return
     */
    public abstract BmmDefinedType getConformanceType();

    /**
     * Returns the effective unitary type, i.e. abstracting away any containers.
     *
     * @return
     */
    public abstract BmmUnitaryType getUnitaryType();

    /**
     * Returns the completely flattened list of type names, flattening out all generic parameters.
     * @return
     */
    public abstract List<String> getFlattenedTypeList();

    public abstract String toDisplayString();

}
