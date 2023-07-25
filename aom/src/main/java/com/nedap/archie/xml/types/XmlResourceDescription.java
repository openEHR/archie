package com.nedap.archie.xml.types;

import com.nedap.archie.aom.ResourceDescriptionItem;
import com.nedap.archie.base.terminology.TerminologyCode;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Created by pieter.bos on 01/08/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RESOURCE_DESCRIPTION")
public class XmlResourceDescription {

    @XmlElement(name = "original_author", required = true)
    private List<StringDictionaryItem> originalAuthor;
    @XmlElement(name="original_namespace")
    private String originalNamespace;
    @XmlElement(name="original_publisher")
    private String originalPublisher;
    @XmlElement(name = "other_contributors")
    private List<String> otherContributors;
    @XmlElement(name = "lifecycle_state", required = true)
    private TerminologyCode lifecycleState;
    @XmlElement(name="custodian_namespace")
    private String custodianNamespace;
    @XmlElement(name="custodian_organisation")
    private String custodianOrganisation;
    private String copyright;
    private String licence;
    @XmlElement(name="ip_acknowledgements")
    private List<StringDictionaryItem> ipAcknowledgements;
    @XmlElement(name="references")
    private List<StringDictionaryItem> references;
    @XmlElement(name = "resource_package_uri")
    private String resourcePackageUri;
    @XmlElement(name="conversion_details")
    private List<StringDictionaryItem> conversionDetails;
    @XmlElement(name = "other_details")
    private List<StringDictionaryItem> otherDetails;
    @XmlElement(required = true)
    private List<XmlResourceDescriptionItem> details;

    public List<StringDictionaryItem> getOriginalAuthor() {
        return originalAuthor;
    }

    public void setOriginalAuthor(List<StringDictionaryItem> originalAuthor) {
        this.originalAuthor = originalAuthor;
    }

    public List<String> getOtherContributors() {
        return otherContributors;
    }

    public void setOtherContributors(List<String> otherContributors) {
        this.otherContributors = otherContributors;
    }


    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getResourcePackageUri() {
        return resourcePackageUri;
    }

    public void setResourcePackageUri(String resourcePackageUri) {
        this.resourcePackageUri = resourcePackageUri;
    }

    public List<StringDictionaryItem> getOtherDetails() {
        return otherDetails;
    }

    public void setOtherDetails(List<StringDictionaryItem> otherDetails) {
        this.otherDetails = otherDetails;
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

    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public List<StringDictionaryItem> getIpAcknowledgements() {
        return ipAcknowledgements;
    }

    public void setIpAcknowledgements(List<StringDictionaryItem> ipAcknowledgements) {
        this.ipAcknowledgements = ipAcknowledgements;
    }

    public List<StringDictionaryItem> getReferences() {
        return references;
    }

    public void setReferences(List<StringDictionaryItem> references) {
        this.references = references;
    }

    public List<StringDictionaryItem> getConversionDetails() {
        return conversionDetails;
    }

    public void setConversionDetails(List<StringDictionaryItem> conversionDetails) {
        this.conversionDetails = conversionDetails;
    }

    public List<XmlResourceDescriptionItem> getDetails() {
        return details;
    }

    public void setDetails(List<XmlResourceDescriptionItem> details) {
        this.details = details;
    }

    public TerminologyCode getLifecycleState() {
        return lifecycleState;
    }

    public void setLifecycleState(TerminologyCode lifecycleState) {
        this.lifecycleState = lifecycleState;
    }
}
