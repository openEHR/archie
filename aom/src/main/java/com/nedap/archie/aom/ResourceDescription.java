package com.nedap.archie.aom;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nedap.archie.json.LifecycleStateDeserializer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by pieter.bos on 15/10/15.
 */

public class ResourceDescription extends ArchetypeModelObject {

    private Map<String, String> originalAuthor = new ConcurrentHashMap<>();
    @Nullable
    private String originalNamespace;
    @Nullable
    private String originalPublisher;
    @Nullable
    private List<String> otherContributors = new ArrayList<>();
    @JsonDeserialize(using = LifecycleStateDeserializer.class)
    private String lifecycleState;
    @Nullable
    private String custodianNamespace;
    @Nullable
    private String custodianOrganisation;
    @Nullable
    private String copyright;
    @Nullable
    private String licence;
    @Nullable
    private Map<String, String> ipAcknowledgements = new ConcurrentHashMap<>();
    @Nullable
    private Map<String, String> references = new ConcurrentHashMap<>();
    @Nullable
    private String resourcePackageUri;
    @Nullable
    private Map<String, String> conversionDetails = new ConcurrentHashMap<>();
    @Nullable
    private Map<String, String> otherDetails = new ConcurrentHashMap<>();
    @Nullable
    private Map<String, ResourceDescriptionItem>  details = new ConcurrentHashMap<>();

    public Map<String, String> getOriginalAuthor() {
        return originalAuthor;
    }

    public void setOriginalAuthor(Map<String, String> originalAuthor) {
        this.originalAuthor = originalAuthor;
    }

    public String getOriginalNamespace() {
        return originalNamespace;
    }

    public void setOriginalNamespace(String originalNamespace) {
        this.originalNamespace = originalNamespace;
    }

    public String getOriginalPublisher() {
        return originalPublisher;
    }

    public void setOriginalPublisher(String originalPublisher) {
        this.originalPublisher = originalPublisher;
    }

    public List<String> getOtherContributors() {
        return otherContributors;
    }

    public void setOtherContributors(List<String> otherContributors) {
        this.otherContributors = otherContributors;
    }

    public String getLifecycleState() {
        return lifecycleState;
    }

    public void setLifecycleState(String lifecycleState) {
        this.lifecycleState = lifecycleState;
    }

    public String getCustodianNamespace() {
        return custodianNamespace;
    }

    public void setCustodianNamespace(String custodianNamespace) {
        this.custodianNamespace = custodianNamespace;
    }

    public String getCustodianOrganisation() {
        return custodianOrganisation;
    }

    public void setCustodianOrganisation(String custodianOrganisation) {
        this.custodianOrganisation = custodianOrganisation;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    @JsonAlias("license")
    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public Map<String, String> getIpAcknowledgements() {
        return ipAcknowledgements;
    }

    public void setIpAcknowledgements(Map<String, String> ipAcknowledgements) {
        this.ipAcknowledgements = ipAcknowledgements;
    }

    public Map<String, String> getReferences() {
        return references;
    }

    public void setReferences(Map<String, String> references) {
        this.references = references;
    }

    public String getResourcePackageUri() {
        return resourcePackageUri;
    }

    public void setResourcePackageUri(String resourcePackageUri) {
        this.resourcePackageUri = resourcePackageUri;
    }

    public Map<String, String> getConversionDetails() {
        return conversionDetails;
    }

    public void setConversionDetails(Map<String, String> conversionDetails) {
        this.conversionDetails = conversionDetails;
    }

    public Map<String, String> getOtherDetails() {
        return otherDetails;
    }

    public void setOtherDetails(Map<String, String> otherDetails) {
        this.otherDetails = otherDetails;
    }

    public Map<String, ResourceDescriptionItem> getDetails() {
        return details;
    }

    public void setDetails(Map<String, ResourceDescriptionItem> details) {
        this.details = details;
    }
}
