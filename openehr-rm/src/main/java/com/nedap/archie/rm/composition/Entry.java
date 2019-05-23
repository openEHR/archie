package com.nedap.archie.rm.composition;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.nedap.archie.rm.archetyped.Archetyped;
import com.nedap.archie.rm.archetyped.FeederAudit;
import com.nedap.archie.rm.archetyped.Link;
import com.nedap.archie.rm.archetyped.Pathable;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ENTRY", propOrder = {
        "language",
        "encoding",
        "subject",
        "provider",
        "otherParticipations",
        "workflowId"
})
public abstract class Entry extends ContentItem {

    private CodePhrase language;
    private CodePhrase encoding;
    @Nullable

    @XmlElement(name = "workflow_id")
    private ObjectRef workflowId;
    private PartyProxy subject;
    @Nullable
    private PartyProxy provider;

    @Nullable
    @XmlElement(name = "other_participations")
    private List<Participation> otherParticipations = new ArrayList<>();

    public Entry() {
    }

    public Entry(@Nullable UIDBasedId uid, String archetypeNodeId, DvText name, @Nullable Archetyped archetypeDetails, @Nullable FeederAudit feederAudit, @Nullable List<Link> links, @Nullable Pathable parent, @Nullable String parentAttributeName, CodePhrase language, CodePhrase encoding, PartyProxy subject, @Nullable PartyProxy provider, @Nullable ObjectRef workflowId, @Nullable List<Participation> otherParticipations) {
        super(uid, archetypeNodeId, name, archetypeDetails, feederAudit, links, parent, parentAttributeName);
        this.language = language;
        this.encoding = encoding;
        this.workflowId = workflowId;
        this.subject = subject;
        this.provider = provider;
        this.otherParticipations = otherParticipations;
    }

    public PartyProxy getSubject() {
        return subject;
    }

    public void setSubject(PartyProxy subject) {
        this.subject = subject;
    }

    @Nullable
    public PartyProxy getProvider() {
        return provider;
    }

    public void setProvider(@Nullable PartyProxy provider) {
        this.provider = provider;
    }
    
    public List<Participation> getOtherParticipations() {
        return otherParticipations;
    }

    public void setOtherParticipations(List<Participation> otherParticipations) {
        this.otherParticipations = otherParticipations;
    }

    public void addOtherParticipant(Participation participant) {
        otherParticipations.add(participant);
    }

    public CodePhrase getLanguage() {
        return language;
    }

    public void setLanguage(CodePhrase language) {
        this.language = language;
    }

    public CodePhrase getEncoding() {
        return encoding;
    }

    public void setEncoding(CodePhrase encoding) {
        this.encoding = encoding;
    }

    @Nullable
    public ObjectRef getWorkflowId() {
        return workflowId;
    }

    @JsonAlias({"work_flow_id"})
    public void setWorkflowId(@Nullable ObjectRef workflowId) {
        this.workflowId = workflowId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Entry entry = (Entry) o;

        if (language != null ? !language.equals(entry.language) : entry.language != null) return false;
        if (encoding != null ? !encoding.equals(entry.encoding) : entry.encoding != null) return false;
        if (workflowId != null ? !workflowId.equals(entry.workflowId) : entry.workflowId != null) return false;
        if (subject != null ? !subject.equals(entry.subject) : entry.subject != null) return false;
        if (provider != null ? !provider.equals(entry.provider) : entry.provider != null) return false;
        return otherParticipations != null ? otherParticipations.equals(entry.otherParticipations) : entry.otherParticipations == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (language != null ? language.hashCode() : 0);
        result = 31 * result + (encoding != null ? encoding.hashCode() : 0);
        result = 31 * result + (workflowId != null ? workflowId.hashCode() : 0);
        result = 31 * result + (subject != null ? subject.hashCode() : 0);
        result = 31 * result + (provider != null ? provider.hashCode() : 0);
        result = 31 * result + (otherParticipations != null ? otherParticipations.hashCode() : 0);
        return result;
    }
}
