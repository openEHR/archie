package com.nedap.archie.rm.archetyped;

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
    private ArchetypeID archetypeId;
    @XmlElement(name="template_id")
    @Nullable
    private TemplateId templateId;
    @XmlElement(name="rm_version")
    private String rmVersion;

    public Archetyped() {
    }

    public Archetyped(ArchetypeID archetypeId, String rmVersion) {
        this.archetypeId = archetypeId;
        this.rmVersion = rmVersion;
    }

    public Archetyped(ArchetypeID archetypeId, @Nullable TemplateId templateId, String rmVersion) {
        this.archetypeId = archetypeId;
        this.templateId = templateId;
        this.rmVersion = rmVersion;
    }



    public ArchetypeID getArchetypeId() {
        return archetypeId;
    }

    public void setArchetypeId(ArchetypeID archetypeId) {
        this.archetypeId = archetypeId;
    }

    @Nullable
    public TemplateId getTemplateId() {
        return templateId;
    }

    public void setTemplateId(@Nullable TemplateId templateId) {
        this.templateId = templateId;
    }
    
    public String getRmVersion() {
        return rmVersion;
    }

    public void setRmVersion(String rmVersion) {
        this.rmVersion = rmVersion;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Archetyped that = (Archetyped) o;

        if (archetypeId != null ? !archetypeId.equals(that.archetypeId) : that.archetypeId != null) return false;
        if (templateId != null ? !templateId.equals(that.templateId) : that.templateId != null) return false;
        return rmVersion != null ? rmVersion.equals(that.rmVersion) : that.rmVersion == null;

    }

    @Override
    public int hashCode() {
        int result = archetypeId != null ? archetypeId.hashCode() : 0;
        result = 31 * result + (templateId != null ? templateId.hashCode() : 0);
        result = 31 * result + (rmVersion != null ? rmVersion.hashCode() : 0);
        return result;
    }
}
