package org.openehr.rm.support.identification;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nedap.archie.rminfo.RMPropertyIgnore;

import javax.annotation.Nullable;
import javax.xml.bind.Unmarshaller;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pieter.bos on 15/10/15.
 */
public class ArchetypeID extends ObjectId {

    @Nullable
    private String namespace;
    @Nullable
    private String qualifiedRmEntity;
    @Nullable
    private String domainConcept;
    @Nullable
    private String rmOriginator;
    @Nullable
    private String rmName;
    @Nullable
    private String rmEntity;
    @Nullable
    private String specialisation;
    @Nullable
    private String versionId;

    private final static Pattern archetypeIDPattern = Pattern.compile("((?<namespace>.*)::)?(?<publisher>[^.-]*)-(?<package>[^.-]*)-(?<class>[^.-]*)\\.(?<concept>[^.]*)(-(?<specialisation>[^.]*))?(\\.v(?<version>.*))?");

    public ArchetypeID() {
    }

    /**
     * Parse the Archetype id from a string
     *
     * @param value
     */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public ArchetypeID(String value) {

        parseValue(value);

        setValue(value);
    }

    private void parseValue(String value) {
        Matcher m = archetypeIDPattern.matcher(value);

        if (!m.matches()) {
            throw new IllegalArgumentException(value + " is not a valid archetype human readable id");
        }
        namespace = m.group("namespace");
        rmOriginator = m.group("publisher");
        rmName = m.group("package");
        rmEntity = m.group("class");
        buildQualifiedRmEntity();

        specialisation = m.group("specialisation");

        domainConcept = m.group("concept");
        versionId = m.group("version");
    }

    public void buildQualifiedRmEntity() {
        qualifiedRmEntity = rmOriginator + "-" + rmName + "-" + rmEntity;
    }

    /**
     * Constructor for creating the archetype id based on all fields separate in json
     *
     * @param qualifiedRmEntity
     * @param domainConcept
     * @param rmOriginator
     * @param rmName
     * @param rmEntity
     * @param specialisation
     * @param versionId
     */
    @JsonCreator
    public ArchetypeID(@JsonProperty("qualified_rm_entity") String qualifiedRmEntity,
                       @JsonProperty("domain_concept") String domainConcept,
                       @JsonProperty("rm_originator") String rmOriginator,
                       @JsonProperty("rm_name") String rmName,
                       @JsonProperty("rm_entity") String rmEntity,
                       @JsonProperty("specialisation") String specialisation,
                       @JsonProperty("versionId") String versionId,
                       @JsonProperty("value") String value) {
        if (value != null) {
            parseValue(value);
            setValue(value);
        } else {
            this.qualifiedRmEntity = qualifiedRmEntity;
            this.domainConcept = domainConcept;
            this.rmOriginator = rmOriginator;
            this.rmName = rmName;
            this.rmEntity = rmEntity;
            this.specialisation = specialisation;
            this.versionId = versionId;
            setValue(getFullId());
        }

    }

    @RMPropertyIgnore
    public String getFullId() {
        StringBuilder result = new StringBuilder(30);
        if(namespace != null) {
            result.append(namespace);
            result.append("::");
        }
        result.append(rmOriginator);
        result.append("-");
        result.append(rmName);
        result.append("-");
        result.append(rmEntity);
        result.append(".");
        result.append(domainConcept);
        if(specialisation != null) {
            result.append("-");
            result.append(specialisation);
        }
        if (versionId == null) {
            return result.toString();
        } else if (versionId.startsWith("v")) {
            result.append(".");
        } else {
            result.append(".v");
        }
        result.append(versionId);
        return result.toString();
    }

    @RMPropertyIgnore
    public String getSemanticId() {
        StringBuilder result = new StringBuilder();
        if(namespace != null) {
            result.append(namespace);
            result.append("::");
        }
        result.append(rmOriginator);
        result.append("-");
        result.append(rmName);
        result.append("-");
        result.append(rmEntity);
        result.append(".");
        result.append(domainConcept);
        if (versionId == null) {
            return result.toString();
        } else if (versionId.startsWith("v")) {
            result.append(".");
        } else {
            result.append(".v");
        }
        result.append(versionId.split("\\.")[0]);
        return result.toString();

    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getQualifiedRmEntity() {
        return qualifiedRmEntity;
    }

    public void setQualifiedRmEntity(String qualifiedRmEntity) {
        this.qualifiedRmEntity = qualifiedRmEntity;
    }

    public String getDomainConcept() {
        return domainConcept;
    }

    public void setDomainConcept(String domainConcept) {
        this.domainConcept = domainConcept;
    }

    public String getRmOriginator() {
        return rmOriginator;
    }

    public void setRmOriginator(String rmOriginator) {
        this.rmOriginator = rmOriginator;
    }

    public String getRmName() {
        return rmName;
    }

    public void setRmName(String rmName) {
        this.rmName = rmName;
    }

    public String getRmEntity() {
        return rmEntity;
    }

    public void setRmEntity(String rmEntity) {
        this.rmEntity = rmEntity;
    }

    public String getSpecialisation() {
        return specialisation;
    }

    public void setSpecialisation(String specialisation) {
        this.specialisation = specialisation;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    @Override
    public String toString() {
        return getFullId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ArchetypeID that = (ArchetypeID) o;
        return Objects.equals(namespace, that.namespace) &&
                Objects.equals(qualifiedRmEntity, that.qualifiedRmEntity) &&
                Objects.equals(domainConcept, that.domainConcept) &&
                Objects.equals(rmOriginator, that.rmOriginator) &&
                Objects.equals(rmName, that.rmName) &&
                Objects.equals(rmEntity, that.rmEntity) &&
                Objects.equals(specialisation, that.specialisation) &&
                Objects.equals(versionId, that.versionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), namespace, qualifiedRmEntity, domainConcept, rmOriginator, rmName, rmEntity, specialisation, versionId);
    }

    // after JAXB unmarshalling, set the field values correctly
    public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        if(this.rmName == null) {
            parseValue(getValue());
        }

    }
}
