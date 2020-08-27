package com.nedap.archie.aom;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import com.nedap.archie.definitions.VersionStatus;
import com.nedap.archie.rminfo.RMPropertyIgnore;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Created by pieter.bos on 15/10/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ARCHETYPE_HRID", propOrder = {
        "conceptId",
        "namespace",
        "rmPublisher",
        "rmPackage",
        "rmClass",
        "releaseVersion",
        "versionStatus",
        "buildCount"
})
public class ArchetypeHRID extends ArchetypeModelObject {

    private String namespace;
    @XmlAttribute(name="rm_publisher")
    private String rmPublisher;
    @XmlAttribute(name="rm_package")
    private String rmPackage;
    @XmlAttribute(name="rm_class")
    private String rmClass;
    @XmlAttribute(name="concept_id")
    private String conceptId;
    @XmlAttribute(name="release_version")
    private String releaseVersion;
    @XmlAttribute(name="version_status")
    private VersionStatus versionStatus;
    @XmlAttribute(name="build_count")
    private String buildCount;
    //TODO: XML attribute 'physical id', which is the full id

    private static final Pattern namespacePattern = Pattern.compile("((?<namespace>.*)::)?");
    private static final Pattern publisherPattern = Pattern.compile("(?<publisher>[^.-]*)");
    private static final Pattern packagePattern = Pattern.compile("(?<package>[^.-]*)");
    private static final Pattern classPattern = Pattern.compile("(?<class>[^.-]*)");
    private static final Pattern conceptPattern = Pattern.compile("(?<concept>[^.]*)");
    private static final Pattern releaseVersionPattern = Pattern.compile("(\\.v(?<version>[^-+]*))?");
    private static final Pattern versionStatusPattern = Pattern.compile("(?<versionStatus>[^.\\d]*)?");
    private static final Pattern buildStatusPattern = Pattern.compile("(\\.?(?<buildCount>\\d*))");
    private static final Pattern archetypeHRIDPattern = Pattern.compile(""
            + namespacePattern
            + publisherPattern
            + "-" + packagePattern
            + "-" + classPattern
            + "\\." + conceptPattern
            + releaseVersionPattern
            + versionStatusPattern
            + buildStatusPattern
    );

    public ArchetypeHRID() {

    }

    @JsonCreator
    public ArchetypeHRID(String value) {
        Matcher m = archetypeHRIDPattern.matcher(value);

        if(!m.matches()) {
            throw new IllegalArgumentException(value + " is not a valid archetype human readable id");
        }
        namespace = m.group("namespace");
        rmPublisher = m.group("publisher");
        rmPackage = m.group("package");
        rmClass = m.group("class");


        conceptId = m.group("concept");
        releaseVersion = m.group("version");
        String versionStatusMatch = m.group("versionStatus");
        versionStatus = versionStatusMatch == null ? VersionStatus.RELEASED : VersionStatus.getEnum(versionStatusMatch);
        buildCount = m.group("buildCount");
    }

    @JsonCreator
    public ArchetypeHRID(@JsonProperty("namespace") String namespace,
                         @JsonProperty("publisher") String rmPublisher,
                         @JsonProperty("rm_package") String rmPackage,
                         @JsonProperty("rm_class") String rmClass,
                         @JsonProperty("concept_id") String conceptId,
                         @JsonProperty("release_version") String releaseVersion,
                         @JsonProperty("version_status") VersionStatus versionStatus,
                         @JsonProperty("build_count") String buildCount) {
        this.namespace = namespace;
        this.rmPublisher = rmPublisher;
        this.rmPackage = rmPackage;
        this.rmClass = rmClass;
        this.conceptId = conceptId;
        this.releaseVersion = releaseVersion;
        this.versionStatus = versionStatus;
        this.buildCount = buildCount;
    }

    public String getFullId() {
        StringBuilder result = new StringBuilder(30);
        result.append(getIdUpToConcept());
        String versionId = getVersionId();
        if (versionId == null) {
            return result.toString();
        }
        result.append(".v");
        result.append(versionId);
        return result.toString();
    }

    public String getSemanticId() {
        return getIdUpToConcept() + ((releaseVersion == null) ? "" : ".v" + ((releaseVersion.isEmpty()) ? "" : getMajorVersion()));
    }

    @JsonIgnore
    public String getVersionId() {
        StringBuilder result = new StringBuilder();
        if (releaseVersion == null) {
            return null;
        } else if (releaseVersion.startsWith("v")) {
            result.append(releaseVersion.substring(1));
        } else {
            result.append(releaseVersion);
        }
        if (versionStatus == null || versionStatus.equals(VersionStatus.RELEASED)) {
            return result.toString();
        } else if (!versionStatus.equals(VersionStatus.BUILD)) {
            result.append("-");
        }
        result.append(versionStatus.getValue());
        if (buildCount == null || buildCount.equals("")) {
            return result.toString();
        } else if (!versionStatus.equals(VersionStatus.BUILD)) {
            result.append(".");
        }
        result.append(buildCount);
        return result.toString();
    }

    public String getMajorVersion() {
        return (releaseVersion == null || releaseVersion.isEmpty()) ? null : releaseVersion.split("\\.")[0];
    }

    public String getMinorVersion() {
        if (releaseVersion == null) return null;
        String[] splitVersion = releaseVersion.split("\\.");
        return (splitVersion.length >= 2) ? splitVersion[1] : null;
    }

    public String getPatchVersion() {
        if (releaseVersion == null) return null;
        String[] splitVersion = releaseVersion.split("\\.|\\-");
        return (splitVersion.length >= 3) ? splitVersion[2] : null;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getRmPublisher() {
        return rmPublisher;
    }

    public void setRmPublisher(String rmPublisher) {
        this.rmPublisher = rmPublisher;
    }

    public String getRmPackage() {
        return rmPackage;
    }

    public void setRmPackage(String rmPackage) {
        this.rmPackage = rmPackage;
    }

    public String getRmClass() {
        return rmClass;
    }

    public void setRmClass(String rmClass) {
        this.rmClass = rmClass;
    }

    public String getConceptId() {
        return conceptId;
    }

    public void setConceptId(String conceptId) {
        this.conceptId = conceptId;
    }

    public String getReleaseVersion() {
        return releaseVersion;
    }

    public void setReleaseVersion(String releaseVersion) {
        this.releaseVersion = releaseVersion;
    }

    public VersionStatus getVersionStatus() {
        return versionStatus;
    }

    public void setVersionStatus(VersionStatus versionStatus) {
        this.versionStatus = versionStatus;
    }

    public String getBuildCount() {
        return buildCount;
    }

    public void setBuildCount(String buildCount) {
        this.buildCount = buildCount;
    }

    @Override
    public String toString() {
        return getFullId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArchetypeHRID other = (ArchetypeHRID) o;
        return Objects.equals(namespace,other.namespace) &&
                Objects.equals(rmPublisher,other.rmPublisher) &&
                Objects.equals(rmPackage, other.rmPackage) &&
                Objects.equals(rmClass,other.rmClass) &&
                Objects.equals(conceptId,other.conceptId) &&
                Objects.equals(releaseVersion,other.releaseVersion) &&
                Objects.equals(versionStatus,other.versionStatus) &&
                Objects.equals(buildCount,other.buildCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, rmPublisher, rmPackage, rmClass, conceptId, releaseVersion, versionStatus, buildCount);
    }

    @RMPropertyIgnore
    public String getIdUpToConcept() {
        StringBuilder result = new StringBuilder(30);
        if(!Strings.isNullOrEmpty(namespace)) {
            result.append(namespace);
            result.append("::");
        }
        result.append(rmPublisher);
        result.append("-");
        result.append(rmPackage);
        result.append("-");
        result.append(rmClass);
        result.append(".");
        result.append(conceptId);
        return result.toString();
    }

}
