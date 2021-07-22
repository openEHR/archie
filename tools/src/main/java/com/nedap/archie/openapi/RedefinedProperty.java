package com.nedap.archie.openapi;

import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmProperty;

public class RedefinedProperty {

    private BmmClass subClass;
    private BmmProperty subClassProperty;
    private BmmClass superClass;
    private BmmProperty superClassProperty;

    public RedefinedProperty(BmmClass subClass, BmmProperty subClassProperty, BmmClass superClass, BmmProperty superClassProperty) {
        this.subClass = subClass;
        this.subClassProperty = subClassProperty;
        this.superClass = superClass;
        this.superClassProperty = superClassProperty;
    }

    public BmmClass getSubClass() {
        return subClass;
    }

    public BmmProperty getSubClassProperty() {
        return subClassProperty;
    }

    public BmmClass getSuperClass() {
        return superClass;
    }

    public BmmProperty getSuperClassProperty() {
        return superClassProperty;
    }

    public String toString() {
        return superClass.getName() + "." + superClassProperty.getName()
                + " redefined in " +
                subClass.getName() + "." + subClassProperty.getName();
    }
}
