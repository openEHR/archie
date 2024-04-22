package org.openehr.rm.datavalues;

import com.google.common.base.MoreObjects;
import org.openehr.rm.datatypes.CodePhrase;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

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

    public DvCodedText(String value, String definingCode) {
        super(value);
        this.definingCode = new CodePhrase(definingCode);
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
        return Objects.equals(definingCode, that.definingCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), definingCode);
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("defining_code", definingCode)
                .add("value", getValue())
                .toString();
    }
}
