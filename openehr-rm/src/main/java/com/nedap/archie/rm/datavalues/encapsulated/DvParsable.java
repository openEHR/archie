package com.nedap.archie.rm.datavalues.encapsulated;

import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.SingleValuedDataValue;
import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.rmutil.InvariantUtil;
import org.apache.commons.io.Charsets;

import javax.annotation.Nullable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

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


    /**
     * The number of bytes in the value string, represented in the given character set. Defaults to UTF-8 if no encoding is given.
     * @return The number of bytes in the value string, represented in the given character set. Defaults to UTF-8 if no encoding is given.
     * @throws java.nio.charset.UnsupportedCharsetException in case of an unsupported character set.
     */
    public int size() {
        if(value == null) {
            return 0;
        }

        if(getCharset() != null) {
            return value.getBytes(Charsets.toCharset(getCharset().getCodeString())).length;
        }
        return value.getBytes(StandardCharsets.UTF_8).length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DvParsable that = (DvParsable) o;
        return Objects.equals(value, that.value) &&
                Objects.equals(formalism, that.formalism);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value, formalism);
    }

    @Invariant("Formalism_valid")
    public boolean formalismValid() {
        return InvariantUtil.nullOrNotEmpty(formalism);
    }

    //this is an implementation correctness validation, not a data validation, so set to be ignored here
    @Invariant(value = "Size_valid", ignored = true)
    public boolean sizeValid() {
        return size() >= 0;
    }
}
