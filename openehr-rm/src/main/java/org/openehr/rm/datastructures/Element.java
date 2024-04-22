package org.openehr.rm.datastructures;

import org.openehr.rm.archetyped.Archetyped;
import org.openehr.rm.archetyped.FeederAudit;
import org.openehr.rm.archetyped.Link;
import org.openehr.rm.archetyped.Pathable;
import org.openehr.rm.datavalues.DataValue;
import org.openehr.rm.datavalues.DvCodedText;
import org.openehr.rm.datavalues.DvText;
import org.openehr.rm.datavalues.SingleValuedDataValue;
import org.openehr.rm.support.identification.UIDBasedId;
import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.openehr.rmutil.InvariantUtil;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import java.util.List;
import java.util.Objects;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ELEMENT", propOrder = {
        "value",
        "nullFlavour",
        "nullReason"
})
@XmlRootElement(name = "element")
public class Element extends Item implements SingleValuedDataValue<DataValue> {

    @Nullable
    private DataValue value;

    @Nullable
    @XmlElement(name = "null_flavour")
    private DvCodedText nullFlavour;

    @Nullable
    @XmlElement(name = "null_reason")
    private DvText nullReason;

    public Element() {
    }

    public Element(String archetypeNodeId, DvText name, @Nullable DataValue value) {
        super(archetypeNodeId, name);
        this.value = value;
    }

    public Element(@Nullable UIDBasedId uid, String archetypeNodeId, DvText name, @Nullable Archetyped archetypeDetails,
                   @Nullable FeederAudit feederAudit, @Nullable List<Link> links, @Nullable Pathable parent, @Nullable String parentAttributeName,
                   @Nullable DataValue value, @Nullable DvCodedText nullFlavour, DvText nullReason) {
        super(uid, archetypeNodeId, name, archetypeDetails, feederAudit, links, parent, parentAttributeName);
        this.value = value;
        this.nullFlavour = nullFlavour;
        this.nullReason = nullReason;
    }

    public DvCodedText getNullFlavour() {
        return nullFlavour;
    }

    public void setNullFlavour(DvCodedText nullFlavour) {
        this.nullFlavour = nullFlavour;
    }

    @Override
    public DataValue getValue() {
        return value;
    }

    @Override
    public void setValue(DataValue value) {
        this.value = value;
    }

    @Nullable
    public DvText getNullReason() {
        return nullReason;
    }

    public void setNullReason(@Nullable DvText nullReason) {
        this.nullReason = nullReason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Element element = (Element) o;
        return Objects.equals(value, element.value) &&
                Objects.equals(nullFlavour, element.nullFlavour) &&
                Objects.equals(nullReason, element.nullReason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value, nullFlavour, nullReason);
    }

    @Invariant("Inv_null_flavour_indicated")
    public boolean nullFlavourIndicated() {
       return value == null ^ nullFlavour == null;
    }

    @Invariant("Inv_null_flavour_valid")
    public boolean nullFlavourValid() {
        return InvariantUtil.belongsToTerminologyByGroupId(nullFlavour, "null flavours");
    }

    @Invariant("Inv_null_reason_valid")
    public boolean nullReasonValid() {
        if(nullReason != null) {
            return value == null;
        }
        return true;
    }
}
