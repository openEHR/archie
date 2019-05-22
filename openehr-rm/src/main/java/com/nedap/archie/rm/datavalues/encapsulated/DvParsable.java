package com.nedap.archie.rm.datavalues.encapsulated;

import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.SingleValuedDataValue;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DV_PARSABLE", propOrder = {
        "value",
        "formalism"
})
public class DvParsable extends DvEncapsulated implements SingleValuedDataValue<String> {

    private String value;
    private String formalism;

    public DvParsable() {
    }

    public DvParsable(String value, String formalism) {
        this.value = value;
        this.formalism = formalism;
    }

    public DvParsable(@Nullable CodePhrase charset, @Nullable CodePhrase language, String value, String formalism) {
        super(charset, language);
        this.value = value;
        this.formalism = formalism;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    public String getFormalism() {
        return formalism;
    }

    public void setFormalism(String formalism) {
        this.formalism = formalism;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DvParsable that = (DvParsable) o;

        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        return formalism != null ? formalism.equals(that.formalism) : that.formalism == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (formalism != null ? formalism.hashCode() : 0);
        return result;
    }
}
