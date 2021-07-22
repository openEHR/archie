package com.nedap.archie.openapi;

import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.core.BmmProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Detects properties that are redefined in a specialized class - so they exist in the superclass in BMM
 * and in the subclass, but have a different type in the subclass
 *
 * Used because this is not supported by openAPI generators - they should only exist in the subclass!
 */
public class RedefinedProperties {

    Map<String, RedefinedProperty> propertiesByParentClass = new HashMap<>();
    Map<String, RedefinedProperty> propertiesByChildClass = new HashMap<>();

    public RedefinedProperties(BmmModel model) {
        storeProperties(detectProperties(model));

        detectExtraPropertiesToAdd(model);
    }

    private List<RedefinedProperty> detectProperties(BmmModel model) {
        final List<RedefinedProperty> properties = new ArrayList<>();
        model.getClassDefinitions().forEach( (name, bmmClass) -> {
            properties.addAll(detect(model, bmmClass));
        } );
        return properties;
    }

    private void storeProperties(List<RedefinedProperty> properties) {
        properties.forEach( property -> {
            propertiesByParentClass.put(
                    createPropertyName(property.getSuperClass().getName(), property.getSuperClassProperty().getName()),
                    property);
            propertiesByChildClass.put(
                    createPropertyName(property.getSubClass().getName(), property.getSubClassProperty().getName()),
                    property);
        });
    }

    private List<RedefinedProperty> detect(BmmModel model, BmmClass bmmClass) {
        //get all the properties defined directly in this class, so not in the parent
        List<RedefinedProperty> propertiesDefinedInParent = new ArrayList<>();
        Map<String, BmmProperty<?>> properties = bmmClass.getProperties();
        List<BmmClass> ancestors = getAllAncestors(model, bmmClass);


        for(BmmProperty property:properties.values()) {
            ancestors.forEach( (ancestor) -> {
                if(ancestor.getProperties().get(property.getName()) != null) {
                    RedefinedProperty redefinedProperty = new RedefinedProperty(bmmClass, property, ancestor, ancestor.getProperties().get(property.getName()));
                    propertiesDefinedInParent.add(redefinedProperty);
                }
            });
        }

        return propertiesDefinedInParent;
    }

    private List<BmmClass> getAllAncestors(BmmModel model, BmmClass bmmClass) {
        return bmmClass.findAllAncestors().stream()
                .map(a -> model.getClassDefinition(a)
                ).collect(Collectors.toList());
    }

    /**
     * Finds all the properties in all classes that are redefined in one of the subclasses,
     * but not in all subclasses. Since they are removed from the superclass in openapi,
     * they must be added to the other subclasses as well.
     * @param model
     */
    private void detectExtraPropertiesToAdd(BmmModel model) {
        propertiesByParentClass.forEach( (name, property) -> {
            List<BmmClass> descendants = property.getSuperClass().findAllDescendants()
                    .stream()
                    .map(a -> model.getClassDefinition(a)
                    ).collect(Collectors.toList());
            descendants.forEach( descendant -> {
                if(!descendant.isAbstract() &&
                        !descendant.getProperties().containsKey(property.getSuperClassProperty().getName()) &&
                    !descendant.findAllAncestors().contains(property.getSubClass().getName()) &&
                !property.getSuperClassProperty().getName().equals("uid")) {
                    //ok, found one!
                    //technically not correct - this can add things twice in the hierarchy, leading to the same problem again
                    //might be good enough.
                    System.out.println(descendant.getName() + "." + property.getSuperClassProperty().getName());
                }
            });
        });
        model.getClassDefinitions().forEach( (name, bmmClass) -> {
            List<BmmClass> ancestors = getAllAncestors(model, bmmClass);
            ancestors.forEach( ancestor -> {
                ancestor.getProperties().values().forEach( ancestorProperty -> {
                    RedefinedProperty propertyInParent = getPropertyByParent(ancestor.getName(), ancestorProperty.getName());
                    if(propertyInParent != null) {
                        //we found a property in the parent.
                    }
                });
            });
        });
    }

    public RedefinedProperty getPropertyByParent(String parentName, String propertyName) {
        return propertiesByParentClass.get(createPropertyName(parentName, propertyName));
    }

    public RedefinedProperty getPropertyByChild(String parentName, String propertyName) {
        return propertiesByChildClass.get(createPropertyName(parentName, propertyName));
    }

    private String createPropertyName(String parentName, String propertyName) {
        return parentName + "." + propertyName;
    }
}
