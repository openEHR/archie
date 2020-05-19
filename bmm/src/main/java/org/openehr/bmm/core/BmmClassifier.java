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
 * Abstract idea of specifying a type either by definition or by reference.
 *
 * Created by cnanjo on 4/11/16.
 */
public abstract class BmmClassifier<T extends BmmClass> extends BmmModelElement {

    /**
     * Formal string form of the type as per UML.
     */
    private String typeName;
    /**
     * Generate a type category of main target type from Type_category_xx values.
     *
     */
    private String typeCategory;
    /**
     * Signature form of the type, which for generics includes generic parameter constrainer types E.g. Interval&lt;T:Ordered&gt;
     */
    private String typeSignature;
    /**
     * Name of the this type in form allowing other type to be RT-conformance tested against it; 'RT' conformance means
     * 'relation-target' conformance, which abstracts away container types like List&lt;&gt;, Set&lt;&gt; etc and compares the
     * dynamic type with the relation target type in the UML sense, i.e. regardless of whether there is single or
     * multiple containment.
     *
     */
    private String conformanceTypeName;
    /**
     * Main design class for this type, from which properties etc can be extracted.
     */
    private T baseClass;
    /**
     * Completely flattened list of type names, flattening out all generic parameters.
     */
    private List<String> flattenedTypeList;

    /**
     * Returns the formal string form of the type as per UML.
     *
     * @return
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * Sets the formal string form of the type as per UML.
     *
     * @param typeName
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * Returns the type category of main target type from Type_category_xx values.
     *
     * @return
     */
    public String getTypeCategory() {
        return typeCategory;
    }

    /**
     * Sets the type category of main target type from Type_category_xx values.
     *
     * @param typeCategory
     */
    public void setTypeCategory(String typeCategory) {
        this.typeCategory = typeCategory;
    }

    /**
     * Returns the signature form of the type, which for generics includes generic parameter constrainer types
     * E.g. Interval&lt;T:Ordered&gt;
     *
     * @return
     */
    public String getTypeSignature() {
        return typeSignature;
    }

    /**
     * Sets the signature form of the type, which for generics includes generic parameter constrainer types
     * E.g. Interval&lt;T:Ordered&gt;
     *
     * @param typeSignature
     */
    public void setTypeSignature(String typeSignature) {
        this.typeSignature = typeSignature;
    }

    /**
     * Returns the name of the this type in form allowing other type to be RT-conformance tested against it; 'RT' conformance means
     * 'relation-target' conformance, which abstracts away container types like List&lt;&gt;, Set&lt;&gt; etc and compares the
     * dynamic type with the relation target type in the UML sense, i.e. regardless of whether there is single or
     * multiple containment.
     *
     * @return
     */
    public String getConformanceTypeName() {
        return conformanceTypeName;
    }

    /**
     * Sets the name of the this type in form allowing other type to be RT-conformance tested against it; 'RT' conformance means
     * 'relation-target' conformance, which abstracts away container types like List&lt;&gt;, Set&lt;&gt; etc and compares the
     * dynamic type with the relation target type in the UML sense, i.e. regardless of whether there is single or
     * multiple containment.
     *
     * @param conformanceTypeName
     */
    public void setConformanceTypeName(String conformanceTypeName) {
        this.conformanceTypeName = conformanceTypeName;
    }

    /**
     * Returns the base class
     *
     * @return
     */
    public T getBaseClass() {
        return baseClass;
    }

    /**
     * Sets the base class
     *
     * @param baseClass
     */
    public void setBaseClass(T baseClass) {
        this.baseClass = baseClass;
    }

    /**
     * Returns the completely flattened list of type names, flattening out all generic parameters.
     * @return
     */
    public List<String> getFlattenedTypeList() {
        return flattenedTypeList;
    }

    /**
     * Sets the completely flattened list of type names, flattening out all generic parameters.
     *
     * @param flattenedTypeList
     */
    public void setFlattenedTypeList(List<String> flattenedTypeList) {
        this.flattenedTypeList = flattenedTypeList;
    }
}
