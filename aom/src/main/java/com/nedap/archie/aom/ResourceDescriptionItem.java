package com.nedap.archie.aom;

import com.nedap.archie.base.terminology.TerminologyCode;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Created by pieter.bos on 01/11/15.
 */
public class ResourceDescriptionItem extends ArchetypeModelObject {
    private TerminologyCode language;
    private String purpose;
    @Nullable
    private List<String> keywords;
    @Nullable
    private String use;
    @Nullable
    private String misuse;
    @Nullable
    private String copyright;
    @Nullable
    private Map<String, URI> originalResourceUri;
    @Nullable
    private Map<String, String> otherDetails;//TODO: string -> object?


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

    public Map<String, URI> getOriginalResourceUri() {
        return originalResourceUri;
    }

    public void setOriginalResourceUri(Map<String, URI> originalResourceUri) {
        this.originalResourceUri = originalResourceUri;
    }

    public Map<String, String> getOtherDetails() {
        return otherDetails;
    }

    public void setOtherDetails(Map<String, String> otherDetails) {
        this.otherDetails = otherDetails;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

}
