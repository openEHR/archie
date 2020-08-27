package com.nedap.archie.aom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.nedap.archie.base.OpenEHRBase;
import com.nedap.archie.query.AOMPathQuery;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by pieter.bos on 15/10/15.
 */
@JsonPropertyOrder({"@type", "rm_type_name", "node_id", "path", "logical_path", "term", "required", "allowed", "any_allowed", "occurrences", "root_node", "attributes", "attribute_tuples"})
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="C_COMPLEX_OBJECT", propOrder = {
        "attributes",
        "attributeTuples"
})
public class CComplexObject extends CDefinedObject<OpenEHRBase> {

    private List<CAttribute> attributes = new ArrayList();

    @XmlElement(name="attributeTuples")
    private List<CAttributeTuple> attributeTuples = new ArrayList();

    @JsonIgnore
    protected transient Archetype archetype;

    public boolean isAnyAllowed() {
        return attributes == null || attributes.isEmpty();
    }

    @Override
    public void setParent(ArchetypeConstraint parent) {
        super.setParent(parent);
        archetype = null;
    }


    @Override
    public boolean isRootNode() {
        return this.getParent() == null && this.archetype != null;
    }

    @Override
    public Archetype getArchetype() {
        if(archetype == null) {
            CAttribute parent = getParent();
            return parent == null ? null : parent.getArchetype();
        }
        return archetype;
    }

    /** set the archetype this is used in. Only set for root nodes! */
    public void setArchetype(Archetype archetype) {
        this.archetype = archetype;
    }

    @Override
    public String toString() {
        return "CComplexObject: " + getRmTypeName() + "[" + getNodeId() + "]";
    }

    /** TODO: should this only be on complex objects? */
    public <T extends ArchetypeModelObject> T itemAtPath(String path) {
        return new AOMPathQuery(path).find(this);
    }

    /**
     * get attribute by name or differential path
     * @param nameOrDifferentialpath name of the attribute, or the full differential path of the attribute
     * @return
     */
    @Override
    public CAttribute getAttribute(String nameOrDifferentialpath) {
        for(CAttribute attribute:attributes) {
            if(attribute.getRmAttributeName().equals(nameOrDifferentialpath) && attribute.getDifferentialPath() == null) {
                return attribute;
            } else if(attribute.getDifferentialPath() != null && attribute.getDifferentialPath().equals(nameOrDifferentialpath)) {
                return attribute;
            }
        }
        return null;
    }

    public List<CAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<CAttribute> attributes) {
        if(attributes == null) {
            this.attributes = new ArrayList<>();
        } else {
            this.attributes = attributes;
            for(CAttribute attribute:attributes) {
                attribute.setParent(this);
            }
        }
    }

    public void addAttribute(CAttribute attribute) {
        attribute.setParent(this);
        attributes.add(attribute);
    }

    public void removeAttribute(CAttribute attribute) {
        removeAttribute(attribute, false);
    }

    private void removeAttribute(CAttribute attribute, boolean allowRemovingTupleMembers) {
        int indexOfAttribute = -1;

        for(int i = 0; i < attributes.size(); i++) {
            CAttribute existingAttribute = attributes.get(i);
            if(existingAttribute.getDifferentialPath() != null) {

                if(existingAttribute.getDifferentialPath().equals(attribute.getDifferentialPath())) {
                    indexOfAttribute = i;
                    break;
                }
            } else {
                if(existingAttribute.getRmAttributeName().equals(attribute.getRmAttributeName())) {
                    indexOfAttribute = i;
                }
            }
        }
        if(indexOfAttribute >= 0) {
            CAttribute foundAttribute = attributes.get(indexOfAttribute);
            if(!allowRemovingTupleMembers && foundAttribute.getSocParent() != null) {
                throw new IllegalArgumentException("cannot remove a tuple attribute with removeAttribute, remove the tuple attribute instead and rebuild tuple attributes.");
            } else {
                attributes.remove(indexOfAttribute);
            }
        }
    }

    public List<CAttributeTuple> getAttributeTuples() {
        return attributeTuples;
    }

    public void setAttributeTuples(List<CAttributeTuple> attributeTuples) {
        this.attributeTuples = attributeTuples;
    }

    public void addAttributeTuple(CAttributeTuple tuple) {
        this.attributeTuples.add(tuple);
    }


    /**
     * Replace the existing attribute with the same name as newAttribute with the newAttribute
     * useful in flattening
     * If no attribute with the given name exists, add the new attribute 
     *
     * @param newAttribute the attribute to replace the old attribute with.
     */
    public void replaceAttribute(CAttribute newAttribute) {
        CAttribute oldAttribute = getAttribute(newAttribute.getRmAttributeName());
        if(oldAttribute != null) {
            int index = attributes.indexOf(oldAttribute);
            attributes.set(index, newAttribute);
            newAttribute.setParent(this);
        } else {
            ///...
            addAttribute(newAttribute);
        }

    }

    @Override
    public boolean isLeaf() {
        return (attributes == null || attributes.isEmpty()) && (attributeTuples == null || attributeTuples.isEmpty());
    }

    /**
     * Remove the attribute tuple with the given member names from this CComplexObject
     * @param parameterMemberNames the name of the attribute tuple members to remove
     */
    public void removeAttributeTuple(List<String> parameterMemberNames) {
        int index = getIndexOfMatchingAttributeTuple(parameterMemberNames);
        if(index >= 0) {
            CAttributeTuple tuple = attributeTuples.get(index);
            attributeTuples.remove(index);
            for(CAttribute attribute:tuple.getMembers()) {
                this.removeAttribute(attribute, true);
            }
        }
    }

    public int getIndexOfMatchingAttributeTuple(List<String> parameterMemberNames) {
        for(int i = 0; i < this.getAttributeTuples().size(); i++) {
            CAttributeTuple cAttributeTuple = getAttributeTuples().get(i);
            cAttributeTuple.getMemberNames();
            List<String> memberNames = cAttributeTuple.getMemberNames();
            if(memberNames.equals(parameterMemberNames)) {
                return i;
            }
        }
        return -1;
    }
}
