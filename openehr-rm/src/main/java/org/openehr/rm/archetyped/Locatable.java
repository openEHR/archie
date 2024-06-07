package org.openehr.rm.archetyped;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nedap.archie.apath.PathSegment;
import org.openehr.rm.datavalues.DvText;
import org.openehr.rm.support.identification.UIDBasedId;
import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.rminfo.RMPropertyIgnore;
import com.nedap.archie.openehr.rmutil.InvariantUtil;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LOCATABLE", propOrder = {
        "name",
        "uid",
        "links",
        "archetypeDetails",
        "feederAudit"
})
public abstract class Locatable extends Pathable {

    @XmlElement
    private DvText name;
    @XmlAttribute(name = "archetype_node_id")
    private String archetypeNodeId;
    @Nullable
    private UIDBasedId uid;

    @XmlElement(name = "archetype_details")
    @Nullable
    private Archetyped archetypeDetails;

    @XmlElement(name = "feeder_audit")
    @Nullable
    private FeederAudit feederAudit;

    @Nullable
    private List<Link> links = new ArrayList<>();


    public Locatable() {
    }

    public Locatable(String archetypeNodeId, DvText name) {
        this.name = name;
        this.archetypeNodeId = archetypeNodeId;
    }

    public Locatable(@Nullable UIDBasedId uid, String archetypeNodeId, DvText name, @Nullable Archetyped archetypeDetails, @Nullable FeederAudit feederAudit, @Nullable List<Link> links, @Nullable Pathable parent, @Nullable String parentAttributeName) {
        super(parent, parentAttributeName);
        this.name = name;
        this.archetypeNodeId = archetypeNodeId;
        this.uid = uid;
        this.archetypeDetails = archetypeDetails;
        this.feederAudit = feederAudit;
        this.links = links;
    }

    public DvText getName() {
        return name;
    }

    public void setName(DvText name) {
        this.name = name;
    }

    /**
     * convenience method
     */
    public void setNameAsString(String name) {
        this.name = new DvText(name);
    }


    public String getArchetypeNodeId() {
        return archetypeNodeId;
    }

    public void setArchetypeNodeId(String archetypeNodeId) {
        this.archetypeNodeId = archetypeNodeId;
    }

    public UIDBasedId getUid() {
        return uid;
    }

    public void setUid(UIDBasedId uid) {
        this.uid = uid;
    }

    public Archetyped getArchetypeDetails() {
        return archetypeDetails;
    }

    public void setArchetypeDetails(Archetyped archetypeDetails) {
        this.archetypeDetails = archetypeDetails;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> linked) {
        this.links = linked;
    }

    public void addLink(Link link) {
        this.links.add(link);
    }

    @Override
    @JsonIgnore
    @RMPropertyIgnore
    public List<PathSegment> getPathSegments() {
        Pathable parent = getParent();
        if (parent == null) {
            return new ArrayList<>();
        }

        List<PathSegment> segments = parent.getPathSegments();
        segments.add(new PathSegment(getParentAttributeName(), archetypeNodeId));
        return segments;
    }

    @JsonIgnore
    @RMPropertyIgnore
    public String getNameAsString() {
        return name == null ? null : name.getValue();
    }

    @Nullable
    public FeederAudit getFeederAudit() {
        return feederAudit;
    }

    public void setFeederAudit(@Nullable FeederAudit feederAudit) {
        this.feederAudit = feederAudit;
    }

    @JsonIgnore
    @RMPropertyIgnore
    @XmlTransient
    public boolean isArchetypeRoot() {
        return archetypeDetails != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Locatable locatable = (Locatable) o;
        return Objects.equals(name, locatable.name) &&
                Objects.equals(archetypeNodeId, locatable.archetypeNodeId) &&
                Objects.equals(uid, locatable.uid) &&
                Objects.equals(archetypeDetails, locatable.archetypeDetails) &&
                Objects.equals(feederAudit, locatable.feederAudit) &&
                Objects.equals(links, locatable.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, archetypeNodeId, uid, archetypeDetails, feederAudit, links);
    }

    @Invariant(value="Links_valid", ignored = true)
    public boolean linksValid() {
        return InvariantUtil.nullOrNotEmpty(links);
    }

    @Invariant(value="Archetyped_valid", ignored = true)
    public boolean archetypedValid() {
        return isArchetypeRoot()^ archetypeDetails != null; //this is not a data validation, again, and pretty much useless
    }

    @Invariant("Archetype_node_id_valid")
    public boolean archetypeNodeIdValid() {
        return InvariantUtil.nullOrNotEmpty(archetypeNodeId);
    }
}
