package org.openehr.rm.composition;

import org.openehr.rm.archetyped.Archetyped;
import org.openehr.rm.archetyped.FeederAudit;
import org.openehr.rm.archetyped.Link;
import org.openehr.rm.archetyped.Pathable;
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
 * Created by pieter.bos on 04/11/15.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EVALUATION", propOrder = {
        "data"
})
public class Evaluation extends CareEntry {
    private ItemStructure data;

    public Evaluation() {
    }

    public Evaluation(@Nullable UIDBasedId uid, String archetypeNodeId, DvText name, @Nullable Archetyped archetypeDetails, @Nullable FeederAudit feederAudit, @Nullable List<Link> links, @Nullable Pathable parent, @Nullable String parentAttributeName, CodePhrase language, CodePhrase encoding, PartyProxy subject, @Nullable PartyProxy provider, @Nullable ObjectRef<? extends ObjectId> workflowId, @Nullable List<Participation> otherParticipations, @Nullable ItemStructure protocol, @Nullable ObjectRef<? extends ObjectId> guidelineId, ItemStructure data) {
        super(uid, archetypeNodeId, name, archetypeDetails, feederAudit, links, parent, parentAttributeName, language, encoding, subject, provider, workflowId, otherParticipations, protocol, guidelineId);
        this.data = data;
    }

    public ItemStructure getData() {
        return data;
    }

    public void setData(ItemStructure data) {
        this.data = data;
        setThisAsParent(data, "data");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Evaluation that = (Evaluation) o;

        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), data);
    }
}
