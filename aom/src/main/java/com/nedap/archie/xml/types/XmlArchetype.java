package com.nedap.archie.xml.types;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeHRID;
import com.nedap.archie.aom.AuthoredResource;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.RulesSection;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.xml.adapters.ArchetypeTerminologyAdapter;
import com.nedap.archie.xml.adapters.StringDictionaryAdapter;
import com.nedap.archie.xml.adapters.StringDictionaryUtil;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement(name="archetype")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AUTHORED_ARCHETYPE", propOrder = {
        "archetypeId",
        //"differential",
        "parentArchetypeId",
        "definition",
        "terminology",
        "rules",
        "buildUid",
        "rmRelease",
        "generated",
        "otherMetaData"
})
public class XmlArchetype extends AuthoredResource {

    @XmlElement(name="parent_archetype_id")
    private String parentArchetypeId;
    @XmlAttribute(name="is_differential")
    private boolean differential = false;
    @XmlElement(name = "archetype_id")
    private ArchetypeHRID archetypeId;

    private CComplexObject definition;
    @XmlJavaTypeAdapter(ArchetypeTerminologyAdapter.class)
    private ArchetypeTerminology terminology;
    private RulesSection rules = null;

    @XmlAttribute(name="adl_version")
    private String adlVersion;
    @XmlElement(name="build_uid")
    private String buildUid;
    @XmlAttribute(name="rm_release")
    private String rmRelease;
    @XmlAttribute(name="is_generated")
    private Boolean generated = false;

    @XmlElement(name="other_meta_data")
    //TODO: this probably requires a custom XmlAdapter
    private List<StringDictionaryItem> otherMetaData = new ArrayList<>();

    public XmlArchetype() {

    }

    public XmlArchetype(Archetype archetype) {
        this.terminology = archetype.getTerminology();
        this.archetypeId = archetype.getArchetypeId();
        this.adlVersion = archetype.getAdlVersion();
        this.buildUid = archetype.getBuildUid();
        this.definition = archetype.getDefinition();
        this.differential = archetype.isDifferential();
        this.generated = archetype.getGenerated();
        this.otherMetaData = StringDictionaryUtil.convertStringMapIntoStringDictionaryList(archetype.getOtherMetaData());
        this.parentArchetypeId = archetype.getParentArchetypeId();
        this.rmRelease = archetype.getRmRelease();
        this.rules = archetype.getRules();
        this.setAnnotations(archetype.getAnnotations());
        this.setAuthoredResourceContent(archetype.getAuthoredResourceContent());
        this.setDescription(archetype.getDescription());
        this.setTranslations(archetype.getTranslations());
        this.setControlled(archetype.getControlled());
        this.setUid(archetype.getUid());
        this.setOriginalLanguage(archetype.getOriginalLanguage());
    }

    public String getParentArchetypeId() {
        return parentArchetypeId;
    }

    public void setParentArchetypeId(String parentArchetypeId) {
        this.parentArchetypeId = parentArchetypeId;
    }

    public boolean isDifferential() {
        return differential;
    }

    public void setDifferential(boolean differential) {
        this.differential = differential;
    }

    public ArchetypeHRID getArchetypeId() {
        return archetypeId;
    }

    public void setArchetypeId(ArchetypeHRID archetypeId) {
        this.archetypeId = archetypeId;
    }

    public CComplexObject getDefinition() {
        return definition;
    }

    public void setDefinition(CComplexObject definition) {
        this.definition = definition;
    }

    public ArchetypeTerminology getTerminology() {
        return terminology;
    }

    public void setTerminology(ArchetypeTerminology terminology) {
        this.terminology = terminology;
    }

    public RulesSection getRules() {
        return rules;
    }

    public void setRules(RulesSection rules) {
        this.rules = rules;
    }

    public String getAdlVersion() {
        return adlVersion;
    }

    public void setAdlVersion(String adlVersion) {
        this.adlVersion = adlVersion;
    }

    public String getBuildUid() {
        return buildUid;
    }

    public void setBuildUid(String buildUid) {
        this.buildUid = buildUid;
    }

    public String getRmRelease() {
        return rmRelease;
    }

    public void setRmRelease(String rmRelease) {
        this.rmRelease = rmRelease;
    }

    public Boolean getGenerated() {
        return generated;
    }

    public void setGenerated(Boolean generated) {
        this.generated = generated;
    }

    public List<StringDictionaryItem> getOtherMetaData() {
        return otherMetaData;
    }

    public void setOtherMetaData(List<StringDictionaryItem> otherMetaData) {
        this.otherMetaData = otherMetaData;
    }
}
