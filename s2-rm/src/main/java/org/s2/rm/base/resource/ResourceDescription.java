package org.s2.rm.base.resource;


import javax.annotation.Nullable;;
import java.util.*;
import javax.xml.bind.annotation.*;

/**
* BMM name: Resource_description
* isAbstract: false | isPrimitiveType: false | isOverride: false
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Resource_description", propOrder = {
  "originalAuthor",
  "originalNamespace",
  "originalPublisher",
  "otherContributors",
  "custodianNamespace",
  "custodianOrganisation",
  "copyright",
  "licence",
  "lifecycleState",
  "resourcePackageUri",
  "ipAcknowledgements",
  "references",
  "conversionDetails",
  "otherDetails",
  "parentResource",
  "details"
})
public class ResourceDescription {
  /**
  * BMM name: original_author | BMM type: Hash<String,String>
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "original_author")
  private Map<String, String> originalAuthor;

  /**
  * BMM name: original_namespace | BMM type: String
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "original_namespace")
  private @Nullable String originalNamespace;

  /**
  * BMM name: original_publisher | BMM type: String
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "original_publisher")
  private @Nullable String originalPublisher;

  /**
  * BMM name: other_contributors | BMM type: List<String>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "other_contributors")
  private @Nullable List<String> otherContributors;

  /**
  * BMM name: custodian_namespace | BMM type: String
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "custodian_namespace")
  private @Nullable String custodianNamespace;

  /**
  * BMM name: custodian_organisation | BMM type: String
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "custodian_organisation")
  private @Nullable String custodianOrganisation;

  /**
  * BMM name: copyright | BMM type: String
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "copyright")
  private @Nullable String copyright;

  /**
  * BMM name: licence | BMM type: String
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "licence")
  private @Nullable String licence;

  /**
  * BMM name: lifecycle_state | BMM type: String
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "lifecycle_state")
  private String lifecycleState;

  /**
  * BMM name: resource_package_uri | BMM type: String
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "resource_package_uri")
  private @Nullable String resourcePackageUri;

  /**
  * BMM name: ip_acknowledgements | BMM type: Hash<String,String>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "ip_acknowledgements")
  private @Nullable Map<String, String> ipAcknowledgements;

  /**
  * BMM name: references | BMM type: Hash<String,String>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "references")
  private @Nullable Map<String, String> references;

  /**
  * BMM name: conversion_details | BMM type: Hash<String,String>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "conversion_details")
  private @Nullable Map<String, String> conversionDetails;

  /**
  * BMM name: other_details | BMM type: Hash<String,String>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "other_details")
  private @Nullable Map<String, String> otherDetails;

  /**
  * BMM name: parent_resource | BMM type: Authored_resource
  * isMandatory: true | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 1..1
  */
  @XmlElement(name = "parent_resource")
  private AuthoredResource parentResource;

  /**
  * BMM name: details | BMM type: Hash<String,Resource_description_item>
  * isMandatory: false | isComputed: false | isImRuntime: false | isImInfrastructure: false | existence: 0..1
  */
  @XmlElement(name = "details")
  private @Nullable Map<String, ResourceDescriptionItem> details;

  public ResourceDescription() {}

  public ResourceDescription(Map<String, String> originalAuthor, String lifecycleState, AuthoredResource parentResource) {
    this.originalAuthor = originalAuthor;
    this.lifecycleState = lifecycleState;
    this.parentResource = parentResource;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    ResourceDescription otherAsResourceDescription = (ResourceDescription) other;
    return Objects.equals(originalAuthor, otherAsResourceDescription.originalAuthor) &&
      Objects.equals(originalNamespace, otherAsResourceDescription.originalNamespace) &&
      Objects.equals(originalPublisher, otherAsResourceDescription.originalPublisher) &&
      Objects.equals(otherContributors, otherAsResourceDescription.otherContributors) &&
      Objects.equals(custodianNamespace, otherAsResourceDescription.custodianNamespace) &&
      Objects.equals(custodianOrganisation, otherAsResourceDescription.custodianOrganisation) &&
      Objects.equals(copyright, otherAsResourceDescription.copyright) &&
      Objects.equals(licence, otherAsResourceDescription.licence) &&
      Objects.equals(lifecycleState, otherAsResourceDescription.lifecycleState) &&
      Objects.equals(resourcePackageUri, otherAsResourceDescription.resourcePackageUri) &&
      Objects.equals(ipAcknowledgements, otherAsResourceDescription.ipAcknowledgements) &&
      Objects.equals(references, otherAsResourceDescription.references) &&
      Objects.equals(conversionDetails, otherAsResourceDescription.conversionDetails) &&
      Objects.equals(otherDetails, otherAsResourceDescription.otherDetails) &&
      Objects.equals(parentResource, otherAsResourceDescription.parentResource) &&
      Objects.equals(details, otherAsResourceDescription.details);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), originalAuthor, originalNamespace, originalPublisher, custodianNamespace, custodianOrganisation, copyright, licence, lifecycleState, resourcePackageUri, ipAcknowledgements, references, conversionDetails, otherDetails, parentResource, details);
    result = otherContributors == null ? 0 : 31 * result + otherContributors.hashCode();
    return result;
  }

  public Map<String, String> getOriginalAuthor() {
    return originalAuthor;
  }

  public void setOriginalAuthor(Map<String, String> originalAuthor) {
    this.originalAuthor = originalAuthor;
  }

  public @Nullable String getOriginalNamespace() {
    return originalNamespace;
  }

  public void setOriginalNamespace(@Nullable String originalNamespace) {
    this.originalNamespace = originalNamespace;
  }

  public @Nullable String getOriginalPublisher() {
    return originalPublisher;
  }

  public void setOriginalPublisher(@Nullable String originalPublisher) {
    this.originalPublisher = originalPublisher;
  }

  public @Nullable List<String> getOtherContributors() {
    return otherContributors;
  }

  public void setOtherContributors(@Nullable List<String> otherContributors) {
    this.otherContributors = otherContributors;
  }

  public @Nullable String getCustodianNamespace() {
    return custodianNamespace;
  }

  public void setCustodianNamespace(@Nullable String custodianNamespace) {
    this.custodianNamespace = custodianNamespace;
  }

  public @Nullable String getCustodianOrganisation() {
    return custodianOrganisation;
  }

  public void setCustodianOrganisation(@Nullable String custodianOrganisation) {
    this.custodianOrganisation = custodianOrganisation;
  }

  public @Nullable String getCopyright() {
    return copyright;
  }

  public void setCopyright(@Nullable String copyright) {
    this.copyright = copyright;
  }

  public @Nullable String getLicence() {
    return licence;
  }

  public void setLicence(@Nullable String licence) {
    this.licence = licence;
  }

  public String getLifecycleState() {
    return lifecycleState;
  }

  public void setLifecycleState(String lifecycleState) {
    this.lifecycleState = lifecycleState;
  }

  public @Nullable String getResourcePackageUri() {
    return resourcePackageUri;
  }

  public void setResourcePackageUri(@Nullable String resourcePackageUri) {
    this.resourcePackageUri = resourcePackageUri;
  }

  public @Nullable Map<String, String> getIpAcknowledgements() {
    return ipAcknowledgements;
  }

  public void setIpAcknowledgements(@Nullable Map<String, String> ipAcknowledgements) {
    this.ipAcknowledgements = ipAcknowledgements;
  }

  public @Nullable Map<String, String> getReferences() {
    return references;
  }

  public void setReferences(@Nullable Map<String, String> references) {
    this.references = references;
  }

  public @Nullable Map<String, String> getConversionDetails() {
    return conversionDetails;
  }

  public void setConversionDetails(@Nullable Map<String, String> conversionDetails) {
    this.conversionDetails = conversionDetails;
  }

  public @Nullable Map<String, String> getOtherDetails() {
    return otherDetails;
  }

  public void setOtherDetails(@Nullable Map<String, String> otherDetails) {
    this.otherDetails = otherDetails;
  }

  public AuthoredResource getParentResource() {
    return parentResource;
  }

  public void setParentResource(AuthoredResource parentResource) {
    this.parentResource = parentResource;
  }

  public @Nullable Map<String, ResourceDescriptionItem> getDetails() {
    return details;
  }

  public void setDetails(@Nullable Map<String, ResourceDescriptionItem> details) {
    this.details = details;
  }

  public String bmmClassName() {
    return "Resource_description";
  }

  @Override
  public String toString() {
    return "Resource_description";
  }
}
