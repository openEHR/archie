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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cnanjo on 3/31/16.
 */
public class CompositeOdinObject extends OdinObject implements Serializable {
    private List<OdinAttribute> attributes;
    private Map<OdinObject, OdinObject> keyedObjects;
    private Map<String, OdinAttribute> attributeIndex;
    private String type;
    private boolean isList = false;

    public CompositeOdinObject() {
        attributes = new ArrayList<OdinAttribute>();
        keyedObjects = new LinkedHashMap<OdinObject,OdinObject>();
        attributeIndex = new LinkedHashMap<String, OdinAttribute>();
    }

    public void addAttribute(OdinAttribute attribute) {
        attributes.add(attribute);
        attributeIndex.put(attribute.getName(), attribute);
    }

    public List<OdinAttribute> getAttributes() {
        List<OdinAttribute> attributeList = new ArrayList<OdinAttribute>();
        attributeList.addAll(attributes);
        return attributeList;
    }

    public OdinObject getKeyedObject(OdinObject key) {
        return keyedObjects.get(key);
    }

    public OdinObject getKeyedObject(String key) {
        return getKeyedObject(new StringObject(key));
    }

    public List<OdinObject> getKeyedObjects() {
        List<OdinObject> keyedObjects = new ArrayList<>();
        keyedObjects.addAll(this.keyedObjects.values());
        return keyedObjects;
    }

    public Map<OdinObject, OdinObject> getKeyedObjectMap() {
        return this.keyedObjects;
    }

    public void addKeyedObject(OdinObject key, OdinObject type) {
        keyedObjects.put(key, type);
    }

    public OdinAttribute retrieveAttributeFromIndex(String attributeName) {
        return attributeIndex.get(attributeName);
    }

    public OdinAttribute getAttributeAtIndex(int index) {
        return attributes.get(index);
    }

    public OdinAttribute getAttribute(String attributeName) { return attributeIndex.get(attributeName); }

    public int getAttributeCount() {
        return attributes.size();
    }

    public int getKeyedObjectCount() {
        return keyedObjects.size();
    }

    public boolean isList() {
        return isList;
    }

    public void setList(boolean list) {
        isList = list;
    }

    public String toString() {
        return attributes.toString() + " -- " + keyedObjects;
    }
}
