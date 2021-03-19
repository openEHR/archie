package com.nedap.archie.creation;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.rminfo.RMAttributeInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility to create Reference model objects based on their RM name. Also can set attribute values on RM Objects based
 * on their RM Attribute name.
 *
 * Created by pieter.bos on 03/02/16.
 */
public class RMObjectCreator {

    private final ModelInfoLookup modelInfoLookup;

    public RMObjectCreator(ModelInfoLookup lookup) {
        this.modelInfoLookup = lookup;
    }

    public <T> T create(CObject constraint) {
        Class<?> clazz = modelInfoLookup.getClassToBeCreated(constraint.getRmTypeName());
        if(clazz == null) {
            throw new IllegalArgumentException("cannot construct RMObject because of unknown constraint name " + constraint.getRmTypeName() + " full constraint " + constraint);
        }
        try {
            Object result = clazz.newInstance();

            modelInfoLookup.processCreatedObject(result, constraint);
            return (T) result;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("error creating class " + constraint.getRmTypeName(), e);
        }
    }

    public void set(Object object, String rmAttributeName, List<Object> values) {
        try {
            RMAttributeInfo attributeInfo = modelInfoLookup.getAttributeInfo(object.getClass(), rmAttributeName);
            if(attributeInfo == null) {
                throw new IllegalArgumentException(String.format("Attribute %s not known for object %s", rmAttributeName, object.getClass().getSimpleName()));
            }

            Type type = attributeInfo.getType();
            if(type instanceof Class) {
                Class<?> clazz = (Class<?>) type;
                if(attributeInfo.isMultipleValued()) {
                    Collection<Object> collection = (Collection<Object>) newInstance(attributeInfo);
                    if(values != null) {
                        collection.addAll(values);
                    }
                    setField(object, attributeInfo, collection);
                } else {
                    setSingleValuedAttribute(object, rmAttributeName, values, attributeInfo);
                }
            } else {
                //primitive value.
                setSingleValuedAttribute(object, rmAttributeName, values, attributeInfo);
            }

        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }

    private void setSingleValuedAttribute(Object object, String rmAttributeName, List<Object> values, RMAttributeInfo attributeInfo) throws InvocationTargetException, IllegalAccessException {
        if(values == null || values.isEmpty()) {
            setField(object, attributeInfo, null);
        } else if(values.size() > 1) {
            throw new IllegalArgumentException(String.format("trying to set multiple values for a single valued field, class %s field %s",
                    object.getClass().getSimpleName(), rmAttributeName)
            );
        } else {
            setField(object, attributeInfo, values.get(0));
        }
    }

    private Object newInstance(RMAttributeInfo attributeInfo) throws InstantiationException, IllegalAccessException {
        if(attributeInfo.getType().equals(List.class)) {
            return new ArrayList<>();
        } else if (attributeInfo.getType().equals(Set.class)) {
            return new LinkedHashSet<>();
        } else if(attributeInfo.getType() instanceof Class){
            return ((Class<?>)attributeInfo.getType()).newInstance();
        } else {
            throw new IllegalArgumentException("cannot create collection instanceof " + attributeInfo.toString());
        }
    }

    private void setField(Object object, RMAttributeInfo field, Object value) throws InvocationTargetException, IllegalAccessException {
        Method setMethod = field.getSetMethod();
        if(setMethod == null) {
            throw new IllegalArgumentException(String.format("field %s of class %s is not a settable field - it has no set method", field.getRmName(), object.getClass().getSimpleName()));
        }
        try {
            setMethod.invoke(object, value);
        } catch (InvocationTargetException e) {
            Class<?> valueType = value == null ? null : value.getClass();
            throw new InvocationTargetException(e.getTargetException(), "Error setting value '" + value + "' of type '" + valueType + "' using method '" + setMethod + "'");
        }
    }

    public void addElementToList(Object object, RMAttributeInfo attributeInfo, Object element) {
        try {
            if(attributeInfo.getAddMethod() != null) {
                attributeInfo.getAddMethod().invoke(object, element);
            } else {
                Object collectionValue = attributeInfo.getGetMethod().invoke(object);
                if(!(attributeInfo.getType() instanceof Class)) {
                    throw new IllegalArgumentException("trying to add an element to an object with type " + attributeInfo.getType());
                }
                if(Collection.class.isAssignableFrom((Class<?>) attributeInfo.getType())) {
                    if(collectionValue == null) {
                        collectionValue = newInstance(attributeInfo);
                        setField(object, attributeInfo, collectionValue);
                    }
                    Collection collection = (Collection) collectionValue;
                    collection.add(element);
                } else {
                    throw new IllegalArgumentException("trying to add an element to an object with type " + attributeInfo.getType());
                }
            }

        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void addElementToListOrSetSingleValues(Object object, String rmAttributeName, Object element) {
        RMAttributeInfo attributeInfo = this.modelInfoLookup.getAttributeInfo(object.getClass(), rmAttributeName);
        if(attributeInfo == null) {
            throw new IllegalArgumentException(String.format("Attribute %s not known for object %s", rmAttributeName, object.getClass().getSimpleName()));
        }
        if(!attributeInfo.isMultipleValued()) {
            if(element instanceof Collection) {
                set(object, rmAttributeName, new ArrayList<>((Collection<?>) element));
            } else {
                set(object, rmAttributeName, Lists.newArrayList(element));
            }
        } else {
            if(element instanceof Collection) {
                Collection<?> collection = (Collection<?>) element;
                for(Object el:collection) {
                    addElementToList(object, attributeInfo, el);
                }
            } else {
                addElementToList(object, attributeInfo, element);
            }

        }
    }
}
