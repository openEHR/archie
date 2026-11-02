package com.nedap.archie.aom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nedap.archie.rminfo.RMProperty;
import com.nedap.archie.xml.adapters.StringDictionaryUtil;
import com.nedap.archie.xml.types.StringDictionaryItem;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.*;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * See @Archetype for the fields you might expect here, along with an explanation
 * Created by pieter.bos on 15/10/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="AUTHORED_ARCHETYPE")
@XmlRootElement(name="archetype")
public class AuthoredArchetype extends Archetype {

    @XmlAttribute(name="adl_version")
    @Nullable
    private String adlVersion;

    @XmlElement(name="build_uid")
    private String buildUid;

    @XmlAttribute(name="rm_release")
    private String rmRelease;

    @XmlAttribute(name="is_generated")
    @RMProperty("is_generated")
    private Boolean generated = false;

    //this is a specific map type to make a JAXB-adapter work. ugly jaxb
    //alternative: define an extra field, use hooks to fill it just in time instead
    @XmlTransient
    private Map<String, String> otherMetaData = new LinkedHashMap<>();

    @XmlElement(name="other_meta_data")
    @JsonIgnore
    //this field should be marked transient, but JAXB will not allow it.
    private List<StringDictionaryItem> xmlOtherMetaData;

    // Invoked by Jaxb Marshaller after unmarshalling
    public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        if(xmlOtherMetaData != null) {
            otherMetaData = StringDictionaryUtil.convertStringDictionaryListToStringMap(xmlOtherMetaData);
        }
    }

    // Invoked by Jaxb Marshaller before marshalling
    public boolean beforeMarshal(Marshaller marshaller) {
        if(otherMetaData == null) {
            xmlOtherMetaData = null;
        } else {
            xmlOtherMetaData = StringDictionaryUtil.convertStringMapIntoStringDictionaryList(otherMetaData);
        }
        return true;
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

    public Map<String, String> getOtherMetaData() {
        return otherMetaData;
    }

    public void setOtherMetaData(Map<String, String> otherMetaData) {
        this.otherMetaData = otherMetaData;
    }

    public void addOtherMetadata(String text, String value) {
        if (value != null) {
            otherMetaData.put(text, value);
        }
    }
}
