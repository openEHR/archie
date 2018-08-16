package com.nedap.archie.rmobjectvalidator;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.nedap.archie.paths.PathSegment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pieter.bos on 02/02/16.
 */
public class IDTreeElement {
    private static Joiner pathArgumentJoiner = Joiner.on(',').skipNulls();

    private final String id;

    private Integer index;

    private final AttributeTreeElement parent;
    private final List<AttributeTreeElement> attributes = new ArrayList<>();

    private String archetypeId;


    public IDTreeElement(String id, AttributeTreeElement parent) {
        this.id = id;
        this.parent = parent;
    }

    public IDTreeElement(String id, Integer index, AttributeTreeElement parent) {
        this.id = id;
        this.index = index;
        this.parent = parent;
    }

    public void addAttribute(AttributeTreeElement attribute) {
        this.attributes.add(attribute);
    }

    public List<AttributeTreeElement> getAttributes() {
        return attributes;
    }

    public String getId() {
        return id;
    }

    public Integer getOneBasedIndex() {
        return index;
    }

    public Integer getZeroBasedIndex() {
        return index == null ? null : index - 1;
    }

    public void setOneBasedIndex(Integer index) {
        this.index = index;
    }

    public void setZeroBasedIndex(Integer index) {
        this.index = index == null ? null : index + 1;
    }

    public AttributeTreeElement getParent() {
        return parent;
    }

    public AttributeTreeElement getAttribute(String attributeName) {
        for (AttributeTreeElement existingAttribute : attributes) {
            if (existingAttribute.getAttributeName().equals(attributeName)) {
                return existingAttribute;
            }
        }
        return null;
    }


    /**
     * Add attribute with id, and return the next IDTree Element
     * <p>
     * If ID is null, you probably meant to add an object. In that case, this object is returned as the next IDTree element
     * so you can add it to the correct attribute yourself
     */
    public IDTreeElement addAttribute(String attributeName, Integer index, String id, boolean addIdTreeElement) {
        AttributeTreeElement attribute = getAttribute(attributeName);
        if (attribute == null) {
            attribute = new AttributeTreeElement(attributeName, this);
            addAttribute(attribute);
        }
        if (addIdTreeElement || !Strings.isNullOrEmpty(id)) {
            return attribute.addIdTreeElement(index, id);
        } else {
            return this;
        }
    }

    public String getArchetypeId() {
        return archetypeId;
    }

    public void setArchetypeId(String archetypeId) {
        this.archetypeId = archetypeId;
    }

    public String reconstructPath() {
        return reconstructPathInner().toString();
    }

    protected StringBuilder reconstructPathInner() {
        AttributeTreeElement attributeTreeElement = getParent();
        if (attributeTreeElement == null) return new StringBuilder("");

        String pathSegment = new PathSegment(
                attributeTreeElement.getAttributeName(),
                getId(),
                getOneBasedIndex()
        ).toString();

        return attributeTreeElement.getParent().reconstructPathInner().append(pathSegment);
    }

    /**
     * Reconstructs the params under this IDTreeElement. Does not prepend the path of this element, ie
     * acts as if this element is the root of the tree.
     *
     * @return
     */
    public Map<String, Object> reconstructParams() {
        Map<String, Object> result = new HashMap<>();
        String pathSoFar = "";
        reconstructParamsInner(result, pathSoFar);
        return result;
    }

    // TODO: Use PathSegment
    private void reconstructParamsInner(Map<String, Object> result, final String pathSoFar) {
        for (AttributeTreeElement attribute : attributes) {
            String newPath = pathSoFar + "/" + attribute.getAttributeName();
            if (attribute.getValue() != null) {
                result.put(newPath, attribute.getValue());
            }
            boolean addIndex = false;

            if (attribute.getIdTreeElements().size() > 1) {
                addIndex = true;
            }
            int index = 1;
            for (IDTreeElement idElement : attribute.getIdTreeElements()) {
                String newPathWithId = newPath + "[" + idElement.getId();
                if (addIndex) {
                    newPathWithId += "," + index;
                }
                newPathWithId = newPathWithId + "]";

                if (idElement.getArchetypeId() != null) {
                    result.put(newPathWithId, idElement.getArchetypeId());
                }

                idElement.reconstructParamsInner(result, newPathWithId);
                index++;
            }
        }
    }

}
