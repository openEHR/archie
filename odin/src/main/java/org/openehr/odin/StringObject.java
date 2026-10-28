package org.openehr.odin;

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
 * Created by cnanjo on 4/8/16.
 */
public class StringObject extends PrimitiveObject<String> implements Serializable {

    public StringObject() {
        super();
    }

    public StringObject(String value) {
        this();
        setValue(value);
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        } else if(!(obj instanceof StringObject)) {
            return false;
        }
        StringObject other = (StringObject)obj;
        if(this == other) {
            return true;
        } else return this.getValue() != null && other.getValue() != null && this.getValue().equals(other.getValue());
    }
}
