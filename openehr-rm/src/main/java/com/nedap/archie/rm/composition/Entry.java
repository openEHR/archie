package com.nedap.archie.rm.composition;

import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.generic.Participation;
import com.nedap.archie.rm.support.identification.ObjectRef;
import com.nedap.archie.rm.generic.PartyProxy;

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

    public void setWorkflowId(@Nullable ObjectRef workflowId) {
        this.workflowId = workflowId;
    }
}
