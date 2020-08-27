package com.nedap.archie.json.flat;

import java.util.Objects;

/**
 * A reference to an attribute in a RM model. Used to ignore specific attributes in the FlatJsonGenerator
 */
public class AttributeReference {
    private String typeName;
    private String attributeName;

    public AttributeReference(String typeName, String attributeName) {
        this.typeName = typeName;
        this.attributeName = attributeName;
    }

    /**
     * Get the typename of the object owning the referred attribute
     * @return the typename
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * Set the typename of the object owning the referred attribute
     * @param typeName the typename to set
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }


    /**
     * Get the attribute name that is referred to
     * @return the attribute name that is referred to
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * Set the attribute name that is refered to
     * @param attributeName the attribute name to set
     */
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttributeReference that = (AttributeReference) o;
        return Objects.equals(typeName, that.typeName) &&
                Objects.equals(attributeName, that.attributeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeName, attributeName);
    }
}
