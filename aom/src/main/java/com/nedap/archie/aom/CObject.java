package com.nedap.archie.aom;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nedap.archie.ArchieLanguageConfiguration;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.aom.utils.ConformanceCheckResult;
import com.nedap.archie.archetypevalidator.ErrorType;
import com.nedap.archie.base.MultiplicityInterval;
import com.nedap.archie.apath.PathSegment;
import com.nedap.archie.rminfo.RMProperty;
import org.openehr.utils.message.I18n;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Constraint on an object.
 *
 * Slightly deviates from the openEHR Archetype Model by including the getAttributes() and getAttribute() methods here
 * This enables one to type: archetype.getDefinition().getAttribute("context").getChild("id13").getAttribute("value")
 * without casting.
 *
 * Created by pieter.bos on 15/10/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="C_OBJECT", propOrder= {
        "occurrences",
        "siblingOrder"
})
public abstract class CObject extends ArchetypeConstraint {

    @XmlAttribute(name="rm_type_name")
    protected String rmTypeName;
    @XmlElement(name="occurrences")
    @Nullable
    private MultiplicityInterval occurrences;
    @XmlAttribute(name="node_id")
    private String nodeId;
    @XmlAttribute(name="is_deprecated")
    @RMProperty("is_deprecated")
    @Nullable
    private Boolean deprecated;

    @XmlElement(name="sibling_order")
    @Nullable
    private SiblingOrder siblingOrder;


    public String getRmTypeName() {
        return rmTypeName;
    }

    public void setRmTypeName(String rmTypeName) {
        this.rmTypeName = rmTypeName;
    }

    public MultiplicityInterval getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(MultiplicityInterval occurrences) {
        this.occurrences = occurrences;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    @JsonAlias("is_deprecated")
    public Boolean getDeprecated() {
        return deprecated;
    }

    public SiblingOrder getSiblingOrder() {
        return siblingOrder;
    }

    public void setSiblingOrder(SiblingOrder siblingOrder) {
        this.siblingOrder = siblingOrder;
    }

    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }

    @Override
    public List<PathSegment> getPathSegments() {
        CAttribute parent = getParent();
        if(parent == null) {
            return new ArrayList<>();
        }
        List<PathSegment> segments = parent.getPathSegments();
        if(!segments.isEmpty()) {
            segments.get(segments.size()-1).setNodeId(getNodeId());
        }
        return segments;
    }

    /**
     * Get the archetype term, in the defined meaning and description language
     */
    public ArchetypeTerm getTerm() {
        if(nodeId == null) {
            return null;
        }
        Archetype archetype = getArchetype();
        if(archetype == null) {
            return null;
        }
        ArchetypeTerm result = archetype.getTerm(this, ArchieLanguageConfiguration.getMeaningAndDescriptionLanguage());
        if(result == null) {
            //no translation in the given language. Fall back to the default.
            result = archetype.getTerm(this, ArchieLanguageConfiguration.getDefaultMeaningAndDescriptionLanguage());
        }
        if(result == null && archetype.getOriginalLanguage() != null && archetype.getOriginalLanguage().getCodeString() != null) {
            result = archetype.getTerm(this, archetype.getOriginalLanguage().getCodeString());
        }
        return result;
    }

    private void setTerm(ArchetypeTerm term) {
        //hack to get Jackson to work for now
    }

    /**
     * Get the meaning of this CObject in the defined meaning and description language.
     * See ArchieLanguageConfiguation
     */
    @JsonIgnore
    @XmlTransient
    public String getMeaning() {
        ArchetypeTerm termDefinition = getTerm();
        if(termDefinition!=null && termDefinition.getText()!=null) {
            return termDefinition.getText();
        }
        return null;
    }

    /**
     * Get the meaning of this CObject in the defined meaning and description language.
     * See ArchieLanguageConfiguation
     */
    @JsonIgnore
    @XmlTransient
    public String getDescription() {
        ArchetypeTerm termDefinition = getTerm();
        if(termDefinition!=null && termDefinition.getDescription()!=null) {
            return termDefinition.getDescription();
        }
        return null;
    }

    private String getLogicalPathMeaning() {
        if(nodeId == null) {
            return null;
        }
        String meaning = null;
        Archetype archetype = getArchetype();
        if(archetype == null) {
            return null;
        }
        ArchetypeTerm termDefinition = archetype.getTerm(this, ArchieLanguageConfiguration.getLogicalPathLanguage());
        if(termDefinition!=null && termDefinition.getText()!=null) {
            meaning = termDefinition.getText();
        }
        return meaning;
    }


    public String getLogicalPath() {
        //TODO: this can cause name clashes. Solve them!
        //TODO: the text can contain []-characters. Replace them?
        //TODO: lowercase and replace spaces with underscores?
        if(getParent() == null) {
            return "/";
        }

        String nodeName = getLogicalPathMeaning();
        if(nodeName == null) {
            nodeName = nodeId;
        }
        String path = getParent().getLogicalPath();
        //TODO: this is a bit slow because we have to walk the tree to the archetype every single time
        if(nodeName != null) {
            path += "[" + nodeName + "]";
        }
        if(path.startsWith("//")) {
            return path.substring(1);
        }
        return path;
    }

    public boolean isAllowed() {
        if(occurrences == null) {
            return true;
        }
        return occurrences.isUpperUnbounded() || occurrences.getUpper() > 0;
    }

    @Override
    public CAttribute getParent() {
        return (CAttribute) super.getParent();
    }

    public boolean isRequired() {
        if(occurrences == null) {
            return false;
        }
        return occurrences.getLower() > 0;
    }

    /**
     * Return the named attribute if this is a constrained complex object. Return null if there is no such named attribute,
     * or this is not a CComplexObject
     *
     * @param name
     * @return
     */
    public CAttribute getAttribute(String name) {
        return null;
    }

    /**
     * Get the underlying attributes of this CObject. From this class always returns an empty list. Overriden with
     * different implementations in subclasses.
     *
     * @return
     */
    public List<CAttribute> getAttributes() {
        return Collections.emptyList();
    }

    /**
     * Return true if and only if this is a root node. Implemented in CComplexObject
     * @return
     */
    @JsonIgnore
    public boolean isRootNode() {
        return false;
    }

    /**
     * Level of specialisation of this archetype node, based on its node_id. The value 0 corresponds to non-specialised,
     * 1 to first-level specialisation and so on. The level is the same as the number of ‘.’ characters in the node_id
     * code. If node_id is not set, the return value is -1, signifying that the specialisation level should be determined
     * from the nearest parent C_OBJECT node having a node_id.
     *
     * @return
     */
    public Integer specialisationDepth() {
        return AOMUtils.getSpecializationDepthFromCode(nodeId);
    }

    @Override
    public String toString() {
        return "CObject: " + getRmTypeName() + "[" + getNodeId() + "]";
    }

    public boolean isProhibited() {
        return occurrences != null && occurrences.isProhibited();
    }

    /**
     * True if constraints represented by this node, ignoring any sub-parts, are narrower or the same as other. Typically used during validation of special-ised archetype nodes.
     * @param other
     * @param rmTypesConformant
     * @return
     */
    public ConformanceCheckResult cConformsTo(CObject other, BiFunction<String, String, Boolean> rmTypesConformant) {
        if(!nodeIdConformsTo(other)) {
            return ConformanceCheckResult.fails(ErrorType.VSONI, I18n.t("Node id {0} does not conform to {1}", this.getNodeId(), other.getNodeId()));
        }
        if(!typeNameConformsTo(other, rmTypesConformant)) {
            return ConformanceCheckResult.fails(ErrorType.VSONCT, I18n.t("type name {0} does not conform to {1}", this.getRmTypeName(), other.getRmTypeName()));
        }
        return ConformanceCheckResult.conforms();
    }

    public boolean typeNameConformsTo(CObject other, BiFunction<String, String, Boolean> rmTypesConformant) {
        if(other.getRmTypeName() == null || getRmTypeName() == null) {
            return true;//these are not nullable, but we're not throwing exceptions here
        }
        if(other.getRmTypeName().equalsIgnoreCase(getRmTypeName())) {
            return true;
        }
        return rmTypesConformant.apply(getRmTypeName(), other.getRmTypeName());
    }

    /**
     * True if this node id conforms to other.node_id, which includes the ids being identical; other is assumed to be in a flat archetype.
     * @param other
     * @return
     */
    public boolean nodeIdConformsTo(CObject other) {
        return AOMUtils.codesConformant(this.getNodeId(), other.getNodeId());
    }

    public boolean occurrencesConformsTo(CObject other) {
        if(occurrences != null && other.occurrences != null) {
            return other.occurrences.contains(occurrences);
        } else {
            return true;
        }
    }


    /**
     * Calculate the effective occurrences of this CObject. If occurrences has not been set explicitly, get it from the
     * reference model using the supplied function
     *
     * @param referenceModelPropMultiplicity a function to retrieve the reference model multiplicity from the rm model, given the type name and the attribute path
     * @return
     */
    public MultiplicityInterval effectiveOccurrences(BiFunction<String, String, MultiplicityInterval> referenceModelPropMultiplicity) {
        if(getOccurrences() != null) {
            return getOccurrences();
        }
        return getDefaultRMOccurrences(referenceModelPropMultiplicity);
    }

    /**
     * Calculate the occurrences from the RM, ignoring any occurrences in this CObject. Note that is only useful
     * when editing this archetype, not in general use.
     * @param referenceModelPropMultiplicity
     * @return
     */
    public MultiplicityInterval getDefaultRMOccurrences(BiFunction<String, String, MultiplicityInterval> referenceModelPropMultiplicity) {
        CAttribute parent = getParent();
        if(parent != null) {
            if(parent.getCardinality() != null && parent.getCardinality().getInterval() != null) { //technically a cardinality without interval is an error, but let's handle it correctly
                if(parent.getCardinality().getInterval().isUpperUnbounded()) {
                    return MultiplicityInterval.createOpen();
                } else {
                    return MultiplicityInterval.createBounded(0, parent.getCardinality().getInterval().getUpper());
                }
            } else if(parent.getParent() != null) {
                MultiplicityInterval multiplicity = referenceModelPropMultiplicity.apply(parent.getParent().getRmTypeName(), parent.getDifferentialPath() == null ? parent.getRmAttributeName() : parent.getDifferentialPath());
                if(multiplicity == null) {
                    return null;
                } else if (multiplicity.isUpperUnbounded()) {
                    return MultiplicityInterval.createOpen();
                } else {
                    return MultiplicityInterval.createBounded(0, multiplicity.getUpper());
                }
            } else {
                return MultiplicityInterval.createOpen();
            }
        } else {
            return MultiplicityInterval.createOpen();
        }
    }

}
