package com.nedap.archie.xml.types;

import com.nedap.archie.base.terminology.TerminologyCode;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Created by pieter.bos on 01/08/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RESOURCE_DESCRIPTION_ITEM", propOrder = {
        "language",
        "purpose",
        "keywords",
        "use",
        "misuse",
        "originalResourceUri",
        "otherDetails"
})
public class XmlResourceDescriptionItem
{
    @XmlElement(required = true)
    protected TerminologyCode language;
    @XmlElement(required = true)
    protected String purpose;
    protected List<String> keywords;
    protected String use;
    protected String misuse;
    @XmlElement(name = "original_resource_uri")
    protected List<StringDictionaryItem> originalResourceUri;
    @XmlElement(name = "other_details")
    protected List<StringDictionaryItem> otherDetails;

    public TerminologyCode getLanguage() {
        return language;
    }

    public void setLanguage(TerminologyCode language) {
        this.language = language;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public String getMisuse() {
        return misuse;
    }

    public void setMisuse(String misuse) {
        this.misuse = misuse;
    }

    public List<StringDictionaryItem> getOriginalResourceUri() {
        return originalResourceUri;
    }

    public void setOriginalResourceUri(List<StringDictionaryItem> originalResourceUri) {
        this.originalResourceUri = originalResourceUri;
    }

    public List<StringDictionaryItem> getOtherDetails() {
        return otherDetails;
    }

    public void setOtherDetails(List<StringDictionaryItem> otherDetails) {
        this.otherDetails = otherDetails;
    }
}
