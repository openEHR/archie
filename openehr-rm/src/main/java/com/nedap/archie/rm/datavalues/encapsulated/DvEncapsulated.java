package com.nedap.archie.rm.datavalues.encapsulated;

import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.DataValue;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DV_ENCAPSULATED", propOrder = {
        "charset",
        "language"
})
public abstract class DvEncapsulated extends DataValue {
    @Nullable
    private CodePhrase charset;
    @Nullable
    private CodePhrase language;


    public DvEncapsulated() {
    }

    public DvEncapsulated(@Nullable CodePhrase charset, @Nullable CodePhrase language) {
        this.charset = charset;
        this.language = language;
    }

    public CodePhrase getCharset() {
        return charset;
    }

    public void setCharset(CodePhrase charset) {
        this.charset = charset;
    }

    public CodePhrase getLanguage() {
        return language;
    }

    public void setLanguage(CodePhrase language) {
        this.language = language;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DvEncapsulated that = (DvEncapsulated) o;

        if (charset != null ? !charset.equals(that.charset) : that.charset != null) return false;
        return language != null ? language.equals(that.language) : that.language == null;

    }

    @Override
    public int hashCode() {
        int result = charset != null ? charset.hashCode() : 0;
        result = 31 * result + (language != null ? language.hashCode() : 0);
        return result;
    }
}
