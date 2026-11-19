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

import com.nedap.archie.adlparser.antlr.odinParser;

import java.io.Serializable;

/**
 * Created by cnanjo on 4/8/16.
 */
public class IntegerObject extends PrimitiveObject<String> implements Serializable {

    public IntegerObject() {
        super();
    }

    public IntegerObject(Integer value) {
        this();
        setValue("" + value.toString());
    }

    public IntegerObject(String value) {
        this();
        setValue(value);
    }

    public Integer getAsInteger() {
        if(getValue() !=  null) {
            return Integer.parseInt(getValue());
        } else {
            return null;
        }

    }

    public static IntegerObject extractIntegerObject(odinParser.Integer_valueContext ctx) {
        String value = ctx.getText();
        IntegerObject cInteger = new IntegerObject();
        cInteger.setValue(value);
        return cInteger;
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        } else if(!(obj instanceof IntegerObject)) {
            return false;
        }
        IntegerObject other = (IntegerObject)obj;
        if(this == other) {
            return true;
        } else return this.getValue() != null && other.getValue() != null && this.getValue().equals(other.getValue());
    }
}
