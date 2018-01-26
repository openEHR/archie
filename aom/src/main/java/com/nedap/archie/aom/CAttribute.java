package com.nedap.archie.aom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.nedap.archie.base.Cardinality;
import com.nedap.archie.base.MultiplicityInterval;
import com.nedap.archie.paths.PathSegment;
import com.nedap.archie.query.APathQuery;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pieter.bos on 15/10/15.
 */
@JsonPropertyOrder({"@type", "rm_attribute_name", "path", "logical_path", "differential_path", "multiple", "mandatory", "existence", "cardinality", "children"})
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="C_ATTRIBUTE", propOrder = {
        "existence",
        "differentialPath",
        "multiple",
        "cardinality",
        "children"
})

public class CAttribute extends ArchetypeConstraint {

    @XmlAttribute(name="rm_attribute_name")
    private String rmAttributeName;
    private MultiplicityInterval existence;
    @XmlElement(name="differential_path")
    private String differentialPath;
    @XmlElement(name="is_multiple")
    private boolean multiple;

    private Cardinality cardinality;

    private List<CObject> children = new ArrayList<>();

    public CAttribute() {

    }
    public CAttribute(String rmAttributeName) {
        this.rmAttributeName = rmAttributeName;
    }

    public String getRmAttributeName() {
        return rmAttributeName;
    }

    public void setRmAttributeName(String rmAttributeName) {
        this.rmAttributeName = rmAttributeName;
    }

    public MultiplicityInterval getExistence() {
        return existence;
    }

    public void setExistence(MultiplicityInterval existence) {
        this.existence = existence;
    }

    public String getDifferentialPath() {
        return differentialPath;
    }

    public void setDifferentialPath(String differentialPath) {
        this.differentialPath = differentialPath;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public CObject getChild(String nodeId) {
        for(CObject child:children) {
            if(nodeId.equals(child.getNodeId())) {
                return child;
            } else if(child instanceof CArchetypeRoot) {
                if (((CArchetypeRoot) child).getArchetypeRef().equals(nodeId)) {
                    return child;
                }
            } else if(child instanceof CComplexObjectProxy) {
                String targetPath = ((CComplexObjectProxy) child).getTargetPath();
                APathQuery aPathQuery = new APathQuery(targetPath);
                PathSegment lastPathSegment = aPathQuery.getPathSegments().get(aPathQuery.getPathSegments().size() - 1);
                if(lastPathSegment.hasIdCode() && lastPathSegment.getNodeId().equals(nodeId)) {
                    return child;
                }
            }
        }
        return null;
    }

    public CObject getChildByMeaning(String meaning) {
        meaning = meaning.toLowerCase();
        for(CObject child:children) {
            String childMeaning = child.getMeaning();
            if(childMeaning != null) {
                childMeaning = childMeaning.toLowerCase();
                if(meaning.equals(childMeaning)){
                    return child;
                }
            }

        }
        return null;
    }

    public List<CObject> getChildren() {
        return children;
    }

    public void setChildren(List<CObject> children) {
        if(children == null) {
            this.children = new ArrayList<>();
        } else {
            this.children = children;

            for(CObject child:children) {
                child.setParent(this);
            }
        }
    }

    /**
     * Add a child at the last position of the children list
     * @param child
     */
    public void addChild(CObject child) {
        children.add(child);
        child.setParent(this);
    }

    /**
     * Add a child at the given sibling order. Useful in the flattener
     *
     * @param child
     * @param order
     */
    public void addChild(CObject child, SiblingOrder order) {
        if(order != null && order.getSiblingNodeId() != null) {
            //TODO: this can be a specialized sibling node id as well!
            CObject sibling = getChild(order.getSiblingNodeId());
            int siblingIndex = getChildren().indexOf(sibling);
            if(siblingIndex > -1) {
                if (!order.isBefore()) {
                    siblingIndex++;
                }
                children.add(siblingIndex, child);
            } else{
                children.add(child);
            }
        } else {
            children.add(child);
        }
        child.setParent(this);
    }


    public void replaceChild(String nodeId, CObject constraint) {

        int index = getIndexOfChildWithNodeId(nodeId);
        if(index > -1) {
            children.set(index, constraint);
            constraint.setParent(this);
        } else {
            addChild(constraint);
        }

    }

    public void removeChild(String nodeId) {
        int index = getIndexOfChildWithNodeId(nodeId);
        if(index > -1) {
            children.remove(index);
        }
    }

    /**
     * Replace the child at node nodeId with all the objects from the parameter newChildren.
     * If keepOriginal is true, it will not replace the original, but keep it in place
     * and add the new elements directly after it
     * Useful operation for flattening
     */
    public void replaceChildren(String nodeId, List<CObject> newChildren, boolean keepOriginal) {
        int index = getIndexOfChildWithNodeId(nodeId);
        if(index > -1) {
            List<CObject> childrenBefore = children.subList(0, index+1);
            if(!keepOriginal) {
                childrenBefore.remove(index);
            }
            childrenBefore.addAll(newChildren);
            for(CObject constraint:newChildren) {
                constraint.setParent(this);
            }
        } else {
            for(CObject constraint:newChildren) {
                addChild(constraint);
            }
        }

    }

    public int getIndexOfChildWithNodeId(String nodeId) {
        for(int i = 0; i < children.size(); i++) {
            CObject child = children.get(i);
            if(nodeId.equals(child.getNodeId())) {
                return i;
            }
        }
        return -1;
    }

    public Cardinality getCardinality() {
        return cardinality;
    }

    public void setCardinality(Cardinality cardinality) {
        this.cardinality = cardinality;
    }

    @Override
    public String toString() {
        return "Cattribute: " + rmAttributeName + ", " + children.size() + " children";
    }

    public List<PathSegment> getPathSegments() {
        CObject parent = getParent();
        if(parent == null) {
            return new ArrayList<>();
        }
        List<PathSegment> segments = parent.getPathSegments();
        if(differentialPath == null) {
            segments.add(new PathSegment(getRmAttributeName()));
        } else {
            segments.addAll(new APathQuery(differentialPath).getPathSegments());
        }
        return segments;
    }

    @Override
    public CObject getParent() {
        return (CObject) super.getParent();
    }

    public String getLogicalPath() {
        String path = "/" + rmAttributeName;
        if(getParent() != null) {
            path = getParent().getLogicalPath() + path;
        }
        if(path.startsWith("//")) {
            return path.substring(1);
        }
        return path;
    }

    public CAttribute clone() {
        return (CAttribute) super.clone();
    }


    /* Operations defined by UML */

    @JsonIgnore
    @XmlTransient
    public boolean isSingle() {
        return !multiple;
    }

    public boolean isMandatory() {
        if(existence != null) {
            return existence.isMandatory();
        }
        return false;
    }

    public boolean anyAllowed() {
        return children.isEmpty() && !isProhibited();
    }


    public boolean isProhibited() {
        if(existence != null) {
            return existence.isProhibited();
        }
        return false;
    }

    //TODO: congruent and conforms to?


    @Override
    public boolean isLeaf() {
        return children != null && children.size() > 0;
    }

    /**
     * True if constraints represented by this node contain no further redefinitions
     * with respect to the node other, with the exception of node_id redefnition in
     * C_OBJECT nodes. Typically used to test if an inherited node locally contains
     * any constraints.
     *
     * @param other
     * @return
     */
    //@Override
    public Boolean cCongruentTo(CAttribute other) {
        //True if this node on its own (ignoring any subparts) expresses no additional constraints than `other'.
        if(other == null) {
            return false;
        }

        return existence == null && ((isSingle() && other.isSingle()) || (isMultiple() && other.isMultiple() && cardinality == null));
    }

    /**
     * True if constraints represented by this node, ignoring any sub-parts,
     * are narrower or the same as other. Typically used during validation of
     * specialised archetype nodes.
     *
     * @param other
     * @return
     */
   // @Override
    public Boolean cConformsTo(CAttribute other) {
        //True if this node on its own (ignoring any subparts) expresses the same or narrower constraints as `other'.
        // Returns False if any of the following is incompatible:
        //	 * cardinality
        //   * existence
        if(other == null) {
            return false;
        }

        return existenceConformsTo(other) && ((isSingle() && other.isSingle()) || (isMultiple() && cardinalityConformsTo(other)));
    }

    public Boolean existenceConformsTo(CAttribute other) {
        //True if the existence of this node conforms to other.existence
        if(other == null) {
            return false;
        }
        if(existence != null && other.existence != null) {
            return other.existence.contains(existence);
        } else {
            return true;
        }
    }

    public Boolean cardinalityConformsTo(CAttribute other) {
        //True if the cardinality of this node conforms to other.cardinality, if it exists
        if(other == null) {
            return false;
        }
        if(cardinality != null && other.cardinality != null) {
            return other.cardinality.contains(cardinality);
        } else {
            return true;
        }
    }

    public boolean isSecondOrderConstrained() {
        return getSocParent() != null || (getParent() != null && getParent().getSocParent() != null);
    }


    /**
     * Get the sum of all occurrences of all direct children of this c_attribute
     * calculates sum of all occurrences lower bounds; where no occurrences are stated, 0 is assumed
     * @return
     */
    public int getAggregateOccurrencesLowerSum() {
        int sum = 0;
        for(CObject cObject:getChildren()) {
            if(cObject.getOccurrences() != null) {
                sum+= cObject.getOccurrences().getLower();
            }
        }
        return sum;
    }

    /**
     *  calculate minimum number of child objects that must occur in data; count 1 for every mandatory
     *  object, and 1 for all optional objects
     * @return
     */
    public int getMinimumChildCount() {
        int result = 0;
        boolean foundOptional = false;
        for(CObject cObject:getChildren()) {
            if(cObject.isRequired()) {
                result++;
            } else if(cObject.isAllowed()) {
                foundOptional = true;
            }
        }
        if(foundOptional) {
            result++;
        }
        return result;
    }

}
