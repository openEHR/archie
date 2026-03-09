package com.nedap.archie.definitions;

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
 * An enumeration of three values that may commonly occur in constraint models.
 *
 *  Use as the type of any attribute within this model, which expresses constraint on some attribute
 *  in a class in a reference model. For example to indicate validity of Date/Time fields.
 *
 * Created by cnanjo on 5/18/16.
 */
public enum ValidityKind {
    /**
     * Constant to indicate mandatory presence of something.
     */
    MANDATORY,
    /**
     * Constant to indicate optional presence of something.
     */
    OPTIONAL,
    /**
     * Constant to indicate disallowed presence of something.
     */
    PROHIBITED
}