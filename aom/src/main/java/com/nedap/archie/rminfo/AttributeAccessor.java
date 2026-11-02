package com.nedap.archie.rminfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * This class provides access to the attributes of AOM and RM objects.
 * <p>
 * In this class, attributes of type array (e.g. {@code byte[]}) are treated as single valued attributes.
 */
public class AttributeAccessor {
    private final ModelInfoLookup modelInfoLookup;

    public AttributeAccessor(ModelInfoLookup modelInfoLookup) {
        this.modelInfoLookup = Objects.requireNonNull(modelInfoLookup);
    }

    /**
     * Add value(s) to the given attribute in the given object.
     * <p>
     * Will add the given value or values to the attribute using the {@link RMAttributeInfo#getAddMethod() add method}
     * of the attribute if available. If the attribute does not have an add method, the value will be added by adding it
     * to the collection obtained via the {@link RMAttributeInfo#getGetMethod() get method} or a new collection will be
     * created via the {@link RMAttributeInfo#getSetMethod() set method} if the attribute is null.
     *
     * @param object        Object to add the values to
     * @param attributeName Name of the attribute of the object
     * @param value         A single value or {@link Collection} of values to add
     * @throws IllegalArgumentException if the attribute is not a valid attribute of the object, the attribute is not
     *                                  a multiple valued attribute or the type of the value does not match the type of
     *                                  the attribute.
     * @throws RuntimeException         if the underlying add, get or set method throws an exception.
     */
    public void addValue(Object object, String attributeName, Object value) throws IllegalArgumentException {
        RMAttributeInfo attributeInfo = getAttributeInfo(object, attributeName);

        if (!attributeInfo.isMultipleValued() || attributeInfo.getType().isArray()) {
            throw new IllegalArgumentException("Attribute " + attributeName + " of object " + object.getClass().getSimpleName() + " is not a multiple valued attribute");
        }

        Collection<?> convertedValue = handleCollection(object, attributeInfo, Objects.requireNonNull(value, "value must not be null"));

        Method addMethod = attributeInfo.getAddMethod();
        if (addMethod != null) {
            try {
                for (Object v : convertedValue) {
                    addMethod.invoke(object, v);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(
                        "Error adding value to attribute " + attributeName + " of object " + object.getClass().getSimpleName(),
                        e
                );
            }
        } else {
            @SuppressWarnings("unchecked") Collection<Object> attributeCollection = (Collection<Object>) getValue(object, attributeName);
            if (attributeCollection == null) {
                attributeCollection = newCollectionInstance(attributeInfo);
                setValue(object, attributeName, attributeCollection);
            }
            attributeCollection.addAll(convertedValue);
        }
    }

    /**
     * Add or set attribute value(s) in the given object.
     * <p>
     * For single valued attributes, the value will be set, overwriting any existing value. This is equivalent to
     * calling {@link #setValue(Object, String, Object)}.
     * <p>
     * For multiple valued attributes, the value will be added to the existing values. This is equivalent to calling
     * {@link #addValue(Object, String, Object)}.
     *
     * @param object        Object to add the values to or set the attribute value of
     * @param attributeName Name of the attribute of the object
     * @param value         Value(s) to add or set, or null to remove the value
     * @throws IllegalArgumentException if the attribute is not a valid attribute of the object, the attribute is a
     *                                  read-only attribute, the type of the value does not match the type of the
     *                                  attribute or multiple values are assigned to a single valued attribute.
     * @throws RuntimeException         if the underlying add, get or set method throws an exception.
     */
    public void addOrSetValue(Object object, String attributeName, Object value) throws IllegalArgumentException {
        RMAttributeInfo attributeInfo = getAttributeInfo(object, attributeName);

        if (attributeInfo.isMultipleValued() && !attributeInfo.getType().isArray()) {
            addValue(object, attributeName, value);
        } else {
            setValue(object, attributeName, value);
        }
    }

    /**
     * Get attribute value in the given object.
     * <p>
     * Will get the given value of the attribute using the {@link RMAttributeInfo#getGetMethod() get method}
     * of the attribute.
     *
     * @param object        Object to get the attribute value of
     * @param attributeName Name of the attribute of the object
     * @throws IllegalArgumentException if the attribute is not a valid attribute of the object.
     * @throws RuntimeException         if the underlying get method throws an exception.
     */
    public Object getValue(Object object, String attributeName) throws IllegalArgumentException {
        RMAttributeInfo attributeInfo = getAttributeInfo(object, attributeName);

        try {
            return attributeInfo.getGetMethod().invoke(object);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(
                    "Error getting value of attribute " + attributeName + " of object " + object.getClass().getSimpleName(),
                    e
            );
        }
    }

    /**
     * Check that given object has an attribute of the given name.
     *
     * @param object        Object to check the attribute of
     * @param attributeName Name of the attribute of the object
     * @return True if the given object has an attribute of the given name
     */
    public boolean hasAttribute(Object object, String attributeName) {
        return modelInfoLookup.getAttributeInfo(
                Objects.requireNonNull(object, "object must not be null").getClass(),
                Objects.requireNonNull(attributeName, "attributeName must not be null")
        ) != null;
    }

    /**
     * Set attribute value(s) in the given object.
     * <p>
     * Will set the given value or values to the attribute using the {@link RMAttributeInfo#getSetMethod() set method}
     * of the attribute, overwriting any existing value.
     * <p>
     * For single valued attributes, this method unwraps single values from a given {@link Collection}.
     * <p>
     * For multiple valued attributes, this method wraps a single value in a new instance of the collection type.
     *
     * @param object        Object to set the attribute value of
     * @param attributeName Name of the attribute of the object
     * @param value         Value(s) to set, or null to remove the value
     * @throws IllegalArgumentException if the attribute is not a valid attribute of the object, the attribute is a
     *                                  read-only attribute, the type of the value does not match the type of the
     *                                  attribute or multiple values are assigned to a single valued attribute.
     * @throws RuntimeException         if the underlying set method throws an exception.
     */
    public void setValue(Object object, String attributeName, Object value) throws IllegalArgumentException {
        RMAttributeInfo attributeInfo = getAttributeInfo(object, attributeName);

        Method setMethod = attributeInfo.getSetMethod();
        if (setMethod == null) {
            throw new IllegalArgumentException(
                    "Attribute " + attributeName + " of object " + object.getClass().getSimpleName() + " is a read-only attribute"
            );
        }

        Object convertedValue;
        if (attributeInfo.isMultipleValued() && !attributeInfo.getType().isArray()) {
            convertedValue = handleCollection(object, attributeInfo, value);
        } else {
            // Handle arrays (byte[]) as a single value
            convertedValue = handleSingleValue(object, attributeInfo, value);
        }

        try {
            setMethod.invoke(object, convertedValue);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(
                    "Error setting value on attribute " + attributeName + " of object " + object.getClass().getSimpleName(),
                    e
            );
        }
    }

    /**
     * Get attribute info for the given object and attribute name.
     *
     * @param object        Object to get the attribute info of
     * @param attributeName Name of the attribute of the object
     * @return Attribute info object
     * @throws NullPointerException     if the object or attribute name is null.
     * @throws IllegalArgumentException if the attribute is not a valid attribute of the object.
     */
    private RMAttributeInfo getAttributeInfo(Object object, String attributeName) {
        RMAttributeInfo attributeInfo = modelInfoLookup.getAttributeInfo(
                Objects.requireNonNull(object, "object must not be null").getClass(),
                Objects.requireNonNull(attributeName, "attributeName must not be null")
        );
        if (attributeInfo == null) {
            throw new IllegalArgumentException(
                    "Attribute " + attributeName + " of object " + object.getClass().getSimpleName() + " is not a valid attribute"
            );
        }
        return attributeInfo;
    }

    /**
     * Validate and convert the given value for assignment to a multiple valued (collection) attribute.
     * <p>
     * Non-collection values will be wrapped in a collection.
     *
     * @param object        Object to set the attribute value of (for exception message)
     * @param attributeInfo Attribute info object
     * @param value         value to validate and convert
     * @return converted value or null if the given value is null
     * @throws IllegalArgumentException if the given value is not assignable to the attribute type or multiple values
     *                                  are assigned to a single valued attribute
     */
    private Collection<?> handleCollection(Object object, RMAttributeInfo attributeInfo, Object value)
            throws IllegalArgumentException {
        String attributeName = attributeInfo.getRmName();
        Class<?> typeInCollection = attributeInfo.getTypeInCollection();
        Collection<?> collection;

        if (value == null) {
            collection = null;
        } else if (value instanceof Collection) {
            collection = (Collection<?>) value;
            validateType(object, attributeName, collection, attributeInfo.getType());

            for (Object element : collection) {
                validateType(object, attributeName, element, typeInCollection);
            }
        } else {
            validateType(object, attributeName, value, typeInCollection);
            List<Object> newCollection = newCollectionInstance(attributeInfo);
            newCollection.add(value);
            collection = newCollection;
        }

        return collection;
    }

    /**
     * Validate and convert the given value for assignment to a single valued attribute.
     * <p>
     * Single valued attributes include byte arrays.
     * <p>
     * This will unpack a collection with zero or one elements.
     *
     * @param object        Object to set the attribute value of (for exception message)
     * @param attributeInfo Attribute info object
     * @param value         value to validate and convert
     * @return converted value or null if the given value is null or an empty collection
     * @throws IllegalArgumentException if the given value is not assignable to the attribute type or the value is a
     *                                  collection with multiple elements.
     */
    private Object handleSingleValue(Object object, RMAttributeInfo attributeInfo, Object value)
            throws IllegalArgumentException {
        if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            if (collection.size() > 1) {
                throw new IllegalArgumentException(
                        "Attribute " + attributeInfo.getRmName() + " of object " + object.getClass().getSimpleName() + " does not support multiple values"
                );
            }
            value = collection.isEmpty() ? null : collection.iterator().next();
        }

        validateType(object, attributeInfo.getRmName(), value, attributeInfo.getType());
        return value;
    }

    /**
     * Validate that the given value is assignable to the given type.
     *
     * @param object        Object to set the attribute value of (for exception message)
     * @param attributeName Name of the attribute of the object (for exception message)
     * @param value         Value to check the type of
     * @param type          The required type for the value
     * @throws IllegalArgumentException if the given value is not assignable to the given type.
     */
    private void validateType(Object object, String attributeName, Object value, Class<?> type)
            throws IllegalArgumentException {
        if (value != null && !type.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException(
                    "Value type " + value.getClass().getTypeName() + " is not a valid type for attribute " + attributeName + " of object " + object.getClass().getSimpleName()
            );
        }
    }

    /**
     * Create new collection instance for the given attribute.
     * <p>
     * Assumes the collection type is List.
     *
     * @param attributeInfo Attribute info object
     * @return Empty list
     * @throws IllegalArgumentException if the attribute type is not List.
     */
    private List<Object> newCollectionInstance(RMAttributeInfo attributeInfo) throws IllegalArgumentException {
        Class<?> type = attributeInfo.getType();
        if (type.equals(List.class)) {
            return new ArrayList<>();
        } else {
            throw new IllegalArgumentException("Unknown collection type " + type.getTypeName());
        }
    }
}
