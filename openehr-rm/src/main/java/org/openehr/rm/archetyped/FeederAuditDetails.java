package org.openehr.rm.archetyped;

import com.nedap.archie.base.RMObject;
import org.openehr.rm.datastructures.ItemStructure;
import org.openehr.rm.datavalues.quantity.datetime.DvDateTime;
import org.openehr.rm.generic.PartyIdentified;
import org.openehr.rm.generic.PartyProxy;
import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.openehr.rmutil.InvariantUtil;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

/**
 * Created by pieter.bos on 08/07/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FEEDER_AUDIT_DETAILS")
public class FeederAuditDetails extends RMObject {

    @XmlElement(name = "system_id")
    protected String systemId;
    @Nullable
    protected PartyIdentified location;
    @Nullable
    protected PartyIdentified provider;
    @Nullable
    protected PartyProxy subject;
    @Nullable
    protected DvDateTime time;
    @Nullable
    @XmlElement(name = "version_id")
    protected String versionId;
    @Nullable
    @XmlElement(name = "other_details")
    private ItemStructure otherDetails;

    public FeederAuditDetails() {
    }

    public FeederAuditDetails(String systemId) {
        this.systemId = systemId;
    }

    public FeederAuditDetails(String systemId, @Nullable PartyIdentified provider, @Nullable PartyIdentified location, @Nullable DvDateTime time, @Nullable PartyProxy subject, @Nullable String versionId, @Nullable ItemStructure otherDetails) {
        this.systemId = systemId;
        this.location = location;
        this.provider = provider;
        this.subject = subject;
        this.time = time;
        this.versionId = versionId;
        this.otherDetails = otherDetails;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public PartyIdentified getLocation() {
        return location;
    }

    public void setLocation(PartyIdentified location) {
        this.location = location;
    }

    public PartyIdentified getProvider() {
        return provider;
    }

    public void setProvider(PartyIdentified provider) {
        this.provider = provider;
    }

    public PartyProxy getSubject() {
        return subject;
    }

    public void setSubject(PartyProxy subject) {
        this.subject = subject;
    }

    public DvDateTime getTime() {
        return time;
    }

    public void setTime(DvDateTime time) {
        this.time = time;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    @Nullable
    public ItemStructure getOtherDetails() {
        return otherDetails;
    }

    public void setOtherDetails(@Nullable ItemStructure otherDetails) {
        this.otherDetails = otherDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeederAuditDetails that = (FeederAuditDetails) o;
        return Objects.equals(systemId, that.systemId) &&
                Objects.equals(location, that.location) &&
                Objects.equals(provider, that.provider) &&
                Objects.equals(subject, that.subject) &&
                Objects.equals(time, that.time) &&
                Objects.equals(versionId, that.versionId) &&
                Objects.equals(otherDetails, that.otherDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(systemId, location, provider, subject, time, versionId, otherDetails);
    }

    @Invariant("System_id_valid")
    public boolean systemIdValid() {
        return InvariantUtil.nullOrNotEmpty(systemId);
    }
}
