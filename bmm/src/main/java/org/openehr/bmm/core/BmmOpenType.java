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

/**
 * Open type reference to a single type parameter, i.e. typically 'T', 'V', 'K' etc. The parameter must be in the
 * type declaration of the owning BMM_class.
 *
 * Created by cnanjo on 4/11/16.
 */
public class BmmOpenType extends BmmType implements Serializable {

    /**
     * The generic constraint, which will be 'Any' if nothing set in original model.
     */
    private BmmParameterType genericConstraint;

    /**
     * Returns the generic constraint, or 'Any' if nothing set in original model.
     * @return
     */
    public BmmParameterType getGenericConstraint() {
        return genericConstraint;
    }

    /**
     * Sets the generic constraint
     * @param genericConstraint
     */
    public void setGenericConstraint(BmmParameterType genericConstraint) {
        this.genericConstraint = genericConstraint;
    }

    /**
     * Return generic_constraint.conformance_type_name.
     *
     * @return
     */
    @Override
    public String getConformanceTypeName() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public String toDisplayString() {return genericConstraint.getName();}

    @Override
    public BmmClass getBaseClass() {
        return getGenericConstraint().getConformsToType();
    }
}
