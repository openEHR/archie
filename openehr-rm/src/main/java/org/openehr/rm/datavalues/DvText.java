package org.openehr.rm.datavalues;


import org.openehr.rm.datatypes.CodePhrase;
import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.openehr.rmutil.InvariantUtil;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Invariant("Language_valid")
    public boolean languageValid() {
        return InvariantUtil.belongsToTerminologyByOpenEHRId(language, "languages");
    }
    @Invariant("Encoding_valid")
    public boolean encodingValid() {
        return InvariantUtil.belongsToTerminologyByOpenEHRId(encoding, "character sets");
    }
    @Invariant(value = "Mappings_valid", ignored = true)
    public boolean mappingsValid() {
        return InvariantUtil.nullOrNotEmpty(mappings);
    }
    @Invariant("Formatting_valid")
    public boolean formattingValid() {
        return InvariantUtil.nullOrNotEmpty(formatting);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DvText dvText = (DvText) o;
        return Objects.equals(value, dvText.value) &&
                Objects.equals(hyperlink, dvText.hyperlink) &&
                Objects.equals(formatting, dvText.formatting) &&
                Objects.equals(mappings, dvText.mappings) &&
                Objects.equals(language, dvText.language) &&
                Objects.equals(encoding, dvText.encoding);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, hyperlink, formatting, mappings, language, encoding);
    }
}
