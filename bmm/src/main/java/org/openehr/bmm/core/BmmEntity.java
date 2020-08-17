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

/**
 * Abstract idea of specifying a type either by definition or by reference.
 *
 * Created by cnanjo on 4/11/16.
 */
public abstract class BmmEntity extends BmmModelElement {

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
     * Generate a type category of main target type from Type_category_xx values.
     *
     */
    private String entityCategory;

    /**
     * Returns the type category of main target type from Type_category_xx values.
     *
     * @return
     */
    public String getEntityCategory() {
        return entityCategory;
    }

    /**
     * Sets the type category of main target type from Type_category_xx values.
     *
     * @param entityCategory
     */
    public void setEntityCategory(String entityCategory) {
        this.entityCategory = entityCategory;
    }

}
