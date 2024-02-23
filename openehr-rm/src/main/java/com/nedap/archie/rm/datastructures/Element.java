package com.nedap.archie.rm.datastructures;

import com.nedap.archie.rm.archetyped.Archetyped;
import com.nedap.archie.rm.archetyped.FeederAudit;
import com.nedap.archie.rm.archetyped.Link;
import com.nedap.archie.rm.archetyped.Pathable;
import com.nedap.archie.rm.datavalues.DataValue;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.datavalues.SingleValuedDataValue;
import com.nedap.archie.rm.support.identification.UIDBasedId;
import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.rmutil.InvariantUtil;

import javax.annotation.Nullable;
import jakarta.xml.bind.annotation.*;
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
