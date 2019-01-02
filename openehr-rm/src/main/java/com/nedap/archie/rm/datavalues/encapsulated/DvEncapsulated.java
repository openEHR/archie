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
}
