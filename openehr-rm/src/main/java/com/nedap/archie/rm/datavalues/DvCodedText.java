package com.nedap.archie.rm.datavalues;

import com.google.common.base.MoreObjects;
import com.nedap.archie.rm.datatypes.CodePhrase;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DV_CODED_TEXT", propOrder = {
        "definingCode"
})
public class DvCodedText extends DvText {

	@XmlElement(name = "defining_code")
    private CodePhrase definingCode;


    public DvCodedText() {
    }

    public DvCodedText(String value, CodePhrase definingCode) {
        super(value);
        this.definingCode = definingCode;
    }

    public DvCodedText(String value, @Nullable CodePhrase language, @Nullable CodePhrase encoding, CodePhrase definingCode) {
        super(value, language, encoding);
        this.definingCode = definingCode;
    }

    public CodePhrase getDefiningCode() {
        return definingCode;
    }

    public void setDefiningCode(CodePhrase definingCode) {
        this.definingCode = definingCode;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DvCodedText that = (DvCodedText) o;

        return definingCode != null ? definingCode.equals(that.definingCode) : that.definingCode == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (definingCode != null ? definingCode.hashCode() : 0);
        return result;
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("code_string", definingCode.getCodeString())
                .add("terminology_id", definingCode.getTerminologyId())
                .add("value", getValue())
                .toString();
    }
}
