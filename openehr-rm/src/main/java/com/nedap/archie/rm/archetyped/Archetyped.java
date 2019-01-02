package com.nedap.archie.rm.archetyped;

import com.nedap.archie.aom.ArchetypeHRID;
import com.nedap.archie.rm.RMObject;
import com.nedap.archie.rm.support.identification.ArchetypeID;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * TODO: templateId should be the class TemplateId. but that's not really well defined in the specs, so for now
 * left it as an ArchetypeId
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ARCHETYPED", propOrder = {
        "archetypeId",
        "templateId",
        "rmVersion"
})
public class Archetyped extends RMObject {

    @XmlElement(name="archetype_id")
    private ArchetypeID archetypeId; //TODO: this is a different class in the RM. why?!
    @XmlElement(name="template_id")
    @Nullable
    private ArchetypeID templateId; //not sure if this is still required in AOM/ADL 2
    @XmlElement(name="rm_version")
    private String rmVersion;
    
    public ArchetypeID getArchetypeId() {
        return archetypeId;
    }

    public void setArchetypeId(ArchetypeID archetypeId) {
        this.archetypeId = archetypeId;
    }

    @Nullable
    public ArchetypeID getTemplateId() {
        return templateId;
    }

    public void setTemplateId(@Nullable ArchetypeID templateId) {
        this.templateId = templateId;
    }
    
    public String getRmVersion() {
        return rmVersion;
    }

    public void setRmVersion(String rmVersion) {
        this.rmVersion = rmVersion;
    }
}
