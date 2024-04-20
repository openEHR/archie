package org.openehr.rm.ehr;

/**
 * Created by pieter.bos on 08/07/16.
 */


import com.fasterxml.jackson.annotation.JsonProperty;

import org.openehr.rm.datastructures.ItemStructure;
import org.openehr.rm.datavalues.DvText;
import org.openehr.rm.generic.PartySelf;
import org.openehr.rm.support.identification.UIDBasedId;
import com.nedap.archie.rminfo.RMProperty;
import org.openehr.rm.archetyped.*;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="EHR_STATUS")
public class EhrStatus extends Locatable {

    private PartySelf subject;
    @XmlElement(name="is_queryable")
    @RMProperty("is_queryable")
    private boolean isQueryable;
    @XmlElement(name="is_modifiable")
    @RMProperty("is_modifiable")
    private boolean isModifiable;
    @Nullable
    @XmlElement(name="other_details")
    private ItemStructure otherDetails;


    public EhrStatus() {
    }

    public EhrStatus(String archetypeNodeId, DvText name, PartySelf subject, boolean isQueryable, boolean isModifiable, @Nullable ItemStructure otherDetails) {
        super(archetypeNodeId, name);
        this.subject = subject;
        this.isQueryable = isQueryable;
        this.isModifiable = isModifiable;
        this.otherDetails = otherDetails;
    }

    public EhrStatus(@Nullable UIDBasedId uid, String archetypeNodeId, DvText name, @Nullable Archetyped archetypeDetails, @Nullable FeederAudit feederAudit, @Nullable List<Link> links, @Nullable Pathable parent, @Nullable String parentAttributeName, PartySelf subject, boolean isQueryable, boolean isModifiable, @Nullable ItemStructure otherDetails) {
        super(uid, archetypeNodeId, name, archetypeDetails, feederAudit, links, parent, parentAttributeName);
        this.subject = subject;
        this.isQueryable = isQueryable;
        this.isModifiable = isModifiable;
        this.otherDetails = otherDetails;
    }

    public PartySelf getSubject() {
        return subject;
    }

    public void setSubject(PartySelf subject) {
        this.subject = subject;
    }

    @JsonProperty(value = "is_queryable")
    public boolean isQueryable() {
        return isQueryable;
    }

    public void setQueryable(boolean queryable) {
        isQueryable = queryable;
    }

    @JsonProperty(value = "is_modifiable")
    public boolean isModifiable() {
        return isModifiable;
    }

    public void setModifiable(boolean modifiable) {
        isModifiable = modifiable;
    }

    @Nullable
    public ItemStructure getOtherDetails() {
        return otherDetails;
    }

    public void setOtherDetails(@Nullable ItemStructure otherDetails) {
        this.otherDetails = otherDetails;
        setThisAsParent(otherDetails, "other_details");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EhrStatus ehrStatus = (EhrStatus) o;
        return isQueryable == ehrStatus.isQueryable &&
                isModifiable == ehrStatus.isModifiable &&
                Objects.equals(subject, ehrStatus.subject) &&
                Objects.equals(otherDetails, ehrStatus.otherDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subject, isQueryable, isModifiable, otherDetails);
    }
}
