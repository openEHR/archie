package com.nedap.archie.rm.datavalues;


import com.nedap.archie.rm.datatypes.CodePhrase;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DV_TEXT", propOrder = {
        "value",
        "hyperlink",
        "formatting",
        "mappings",
        "language",
        "encoding"
})
public class DvText extends DataValue implements SingleValuedDataValue<String> {


    private String value;
    @Nullable
    private DvURI hyperlink;
    @Nullable
    private String formatting;
    @Nullable
    private List<TermMapping> mappings = new ArrayList<>();
    @Nullable
    private CodePhrase language;
    @Nullable
    private CodePhrase encoding;

    public DvText() {

    }

    public DvText(String value) {
        this.value = value;
    }

    public DvText(String value, @Nullable CodePhrase language, @Nullable CodePhrase encoding) {
        this.value = value;
        this.language = language;
        this.encoding = encoding;
    }

    public List<TermMapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<TermMapping> mappings) {
        this.mappings = mappings;
    }

    public void addMapping(TermMapping mapping) {
        this.mappings.add(mapping);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public DvURI getHyperlink() {
        return hyperlink;
    }

    public void setHyperlink(DvURI hyperlink) {
        this.hyperlink = hyperlink;
    }

    @Nullable
    public String getFormatting() {
        return formatting;
    }

    public void setFormatting(@Nullable String formatting) {
        this.formatting = formatting;
    }

    public CodePhrase getLanguage() {
        return language;
    }

    public void setLanguage(CodePhrase language) {
        this.language = language;
    }

    public CodePhrase getEncoding() {
        return encoding;
    }

    public void setEncoding(CodePhrase encoding) {
        this.encoding = encoding;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DvText dvText = (DvText) o;

        if (value != null ? !value.equals(dvText.value) : dvText.value != null) return false;
        if (hyperlink != null ? !hyperlink.equals(dvText.hyperlink) : dvText.hyperlink != null) return false;
        if (formatting != null ? !formatting.equals(dvText.formatting) : dvText.formatting != null) return false;
        if (mappings != null ? !mappings.equals(dvText.mappings) : dvText.mappings != null) return false;
        if (language != null ? !language.equals(dvText.language) : dvText.language != null) return false;
        return encoding != null ? encoding.equals(dvText.encoding) : dvText.encoding == null;

    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (hyperlink != null ? hyperlink.hashCode() : 0);
        result = 31 * result + (formatting != null ? formatting.hashCode() : 0);
        result = 31 * result + (mappings != null ? mappings.hashCode() : 0);
        result = 31 * result + (language != null ? language.hashCode() : 0);
        result = 31 * result + (encoding != null ? encoding.hashCode() : 0);
        return result;
    }
}
