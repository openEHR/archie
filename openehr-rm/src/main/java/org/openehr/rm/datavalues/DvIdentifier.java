package org.openehr.rm.datavalues;

import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.openehr.rmutil.InvariantUtil;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DV_IDENTIFIER", propOrder = {
        "issuer",
        "assigner",
        "id",
        "type"
})
public class DvIdentifier extends DataValue {

    @Nullable
    private String issuer;
    @Nullable
    private String assigner;
    private String id;
    @Nullable
    private String type;

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getAssigner() {
        return assigner;
    }

    public void setAssigner(String assigner) {
        this.assigner = assigner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DvIdentifier that = (DvIdentifier) o;
        return Objects.equals(issuer, that.issuer) &&
                Objects.equals(assigner, that.assigner) &&
                Objects.equals(id, that.id) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(issuer, assigner, id, type);
    }

    @Invariant("Id_valid")
    public boolean idValid() {
        return InvariantUtil.nullOrNotEmpty(id);
    }
}
