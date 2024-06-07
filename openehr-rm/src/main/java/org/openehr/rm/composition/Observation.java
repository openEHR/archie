package org.openehr.rm.composition;

import org.openehr.rm.archetyped.Archetyped;
import org.openehr.rm.archetyped.FeederAudit;
import org.openehr.rm.archetyped.Link;
import org.openehr.rm.archetyped.Pathable;
import org.openehr.rm.datastructures.History;
import org.openehr.rm.datastructures.ItemStructure;
import org.openehr.rm.datatypes.CodePhrase;
import org.openehr.rm.datavalues.DvText;
import org.openehr.rm.generic.Participation;
import org.openehr.rm.generic.PartyProxy;
import org.openehr.rm.support.identification.ObjectId;
import org.openehr.rm.support.identification.ObjectRef;
import org.openehr.rm.support.identification.UIDBasedId;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;
import java.util.Objects;

/**
 * Created by pieter.bos on 03/11/15.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OBSERVATION", propOrder = {
        "data",
        "state"
})
public class Observation extends CareEntry {

    @Nullable
    private History<ItemStructure> state;
    private History<ItemStructure> data;


    public Observation() {
    }

    public Observation(@Nullable UIDBasedId uid, String archetypeNodeId, DvText name, @Nullable Archetyped archetypeDetails, @Nullable FeederAudit feederAudit, @Nullable List<Link> links, @Nullable Pathable parent, @Nullable String parentAttributeName, CodePhrase language, CodePhrase encoding, PartyProxy subject, @Nullable PartyProxy provider, @Nullable ObjectRef<? extends ObjectId> workflowId, @Nullable List<Participation> otherParticipations, @Nullable ItemStructure protocol, @Nullable ObjectRef<? extends ObjectId> guidelineId, History<ItemStructure> data, @Nullable History<ItemStructure> state) {
        super(uid, archetypeNodeId, name, archetypeDetails, feederAudit, links, parent, parentAttributeName, language, encoding, subject, provider, workflowId, otherParticipations, protocol, guidelineId);
        this.state = state;
        this.data = data;
    }

    @Nullable
    public History<ItemStructure> getState() {
        return state;
    }

    public void setState(@Nullable History<ItemStructure> state) {
        this.state = state;
        setThisAsParent(state, "state");
    }

    public History<ItemStructure> getData() {
        return data;
    }

    public void setData(History<ItemStructure> data) {
        this.data = data;
        setThisAsParent(data, "data");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Observation that = (Observation) o;
        return Objects.equals(state, that.state) &&
                Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), state, data);
    }
}
