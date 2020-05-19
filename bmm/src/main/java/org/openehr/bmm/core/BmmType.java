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
public abstract class BmmType<T extends BmmClass> extends BmmTypeElement<T> {

    public static final String BMM_SIMPLE_TYPE = "BMM_SIMPLE_TYPE";
    public static final String BMM_SIMPLE_TYPE_OPEN = "BMM_SIMPLE_TYPE_OPEN";
    public static final String BMM_CONTAINER_TYPE = "BMM_CONTAINER_TYPE";
    public static final String BMM_GENERIC_TYPE = "BMM_GENERIC_TYPE";
    public static final String BMM_GENERIC_PARAMETER = "BMM_GENERIC_PARAMETER";

    public static final String P_BMM_SIMPLE_TYPE = "P_BMM_SIMPLE_TYPE";
    public static final String P_BMM_SIMPLE_TYPE_OPEN = "P_BMM_SIMPLE_TYPE_OPEN";
    public static final String P_BMM_CONTAINER_TYPE = "P_BMM_CONTAINER_TYPE";
    public static final String P_BMM_GENERIC_TYPE = "P_BMM_GENERIC_TYPE";
    public static final String P_BMM_GENERIC_PARAMETER = "P_BMM_GENERIC_PARAMETER";

    /**
     * Determine if there are any type substitutions.
     *
     * @return
     */
    public boolean hasTypeSubstitutions() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * List of type substitutions if any available for this type within the current BMM model.
     *
     * @return
     */
    public List<String> getTypeSubstitutions() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public abstract String toDisplayString();

}
