package com.nedap.archie.rm.composition;

import com.nedap.archie.rm.archetyped.Archetyped;
import com.nedap.archie.rm.archetyped.FeederAudit;
import com.nedap.archie.rm.archetyped.Link;
import com.nedap.archie.rm.archetyped.Pathable;
import com.nedap.archie.rm.datastructures.ItemStructure;
import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.generic.Participation;
import com.nedap.archie.rm.generic.PartyProxy;
import com.nedap.archie.rm.support.identification.ObjectRef;
import com.nedap.archie.rm.support.identification.UIDBasedId;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;
import java.util.Objects;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CARE_ENTRY", propOrder = {
        "protocol",
        "guidelineId"
})
public abstract class CareEntry extends Entry {

    @Nullable
    private ItemStructure protocol;
    @Nullable
    @XmlElement(name = "guideline_id")
    private ObjectRef guidelineId;

    public CareEntry() {
    }

    public CareEntry(@Nullable UIDBasedId uid, String archetypeNodeId, DvText name, @Nullable Archetyped archetypeDetails, @Nullable FeederAudit feederAudit, @Nullable List<Link> links, @Nullable Pathable parent, @Nullable String parentAttributeName, CodePhrase language, CodePhrase encoding, PartyProxy subject, @Nullable PartyProxy provider, @Nullable ObjectRef workflowId, @Nullable List<Participation> otherParticipations, @Nullable ItemStructure protocol, @Nullable ObjectRef guidelineId) {
        super(uid, archetypeNodeId, name, archetypeDetails, feederAudit, links, parent, parentAttributeName, language, encoding, subject, provider, workflowId, otherParticipations);
        this.protocol = protocol;
        this.guidelineId = guidelineId;
    }

    @Nullable
    public ItemStructure getProtocol() {
        return protocol;
    }

    public void setProtocol(@Nullable ItemStructure protocol) {
        this.protocol = protocol;
        setThisAsParent(protocol, "protocol");
    }

    @Nullable
    public ObjectRef getGuidelineId() {
        return guidelineId;
    }

    public void setGuidelineId(@Nullable ObjectRef guidelineId) {
        this.guidelineId = guidelineId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CareEntry careEntry = (CareEntry) o;
        return Objects.equals(protocol, careEntry.protocol) &&
                Objects.equals(guidelineId, careEntry.guidelineId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), protocol, guidelineId);
    }
}
