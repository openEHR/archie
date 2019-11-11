package com.nedap.archie.rminfo;

import java.util.LinkedHashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;

/**
 * Created by pieter.bos on 25/03/16.
 */
public class RMTypeInfo {

    private final String rmName;
    private final Class javaClass;
    //only direct parent classes here
    private final Set<RMTypeInfo> parentClasses = new LinkedHashSet<>();
    //only direct descendant classes here
    private final Set<RMTypeInfo> descendantClasses = new LinkedHashSet<>();
    private Map<String, RMAttributeInfo> attributes = new HashMap<>();

    public RMTypeInfo(Class clazz, String rmName) {
        this.javaClass = clazz;
        this.rmName = rmName;
    }

    public String getRmName() {
        return rmName;
    }

    public Class getJavaClass() {
        return javaClass;
    }

    public Map<String, RMAttributeInfo> getAttributes() {
        return attributes;
    }

    public void addAttribute(RMAttributeInfo attribute) {
        attributes.put(attribute.getRmName(), attribute);
    }

    public RMAttributeInfo getAttribute(String attributeName) {
        return attributes.get(attributeName);
    }

    public void addDirectParentClass(RMTypeInfo parent) {
        parentClasses.add(parent);
    }

    public Set<RMTypeInfo> getDirectParentClasses() {
        return parentClasses;
    }

    public void addDirectDescendantClass(RMTypeInfo parent) {
        descendantClasses.add(parent);
    }

    public Set<RMTypeInfo> getDirectDescendantClasses() {
        return descendantClasses;
    }

    public Set<RMTypeInfo> getAllDescendantClasses() {
        Stack<RMTypeInfo> workList = new Stack<>();
        Set<RMTypeInfo> result = new LinkedHashSet<>();

        workList.addAll(descendantClasses);
        while(!workList.isEmpty()) {
            RMTypeInfo descendant = workList.pop();
            workList.addAll(descendant.getDirectDescendantClasses());
            result.add(descendant);
        }
        return result;
    }

    public Set<RMTypeInfo> getAllParentClasses() {
        Stack<RMTypeInfo> workList = new Stack<>();
        Set<RMTypeInfo> result = new LinkedHashSet<>();

        workList.addAll(parentClasses);
        while(!workList.isEmpty()) {
            RMTypeInfo parent = workList.pop();
            workList.addAll(parent.getDirectParentClasses());
            result.add(parent);
        }
        return result;
    }

    public boolean isParentOf(RMTypeInfo other) {
        return getAllDescendantClasses().contains(other);
    }

    public boolean isDescendantOf(RMTypeInfo other) {
        return getAllParentClasses().contains(other);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RMTypeInfo that = (RMTypeInfo) o;
        return Objects.equals(rmName, that.rmName) &&
                Objects.equals(javaClass, that.javaClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rmName, javaClass);
    }

    @Override
    public String toString() {
        return rmName;
    }

    public boolean isDescendantOrEqual(RMTypeInfo other) {
        return other.equals(this) || getAllParentClasses().contains(other);
    }
}
