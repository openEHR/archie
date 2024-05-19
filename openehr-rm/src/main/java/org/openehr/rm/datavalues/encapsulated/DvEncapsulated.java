package org.openehr.rm.datavalues.encapsulated;

import org.openehr.rm.datatypes.CodePhrase;
import org.openehr.rm.datavalues.DataValue;
import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.openehr.rmutil.InvariantUtil;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

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
        return Objects.equals(charset, that.charset) &&
                Objects.equals(language, that.language);
    }

    @Override
    public int hashCode() {
        return Objects.hash(charset, language);
    }

    @Invariant("Language_valid")
    public boolean languageValid() {
        return InvariantUtil.belongsToTerminologyByOpenEHRId(language, "languages");
    }

    @Invariant("Charset_valid")
    public boolean charsetValid() {
        return InvariantUtil.belongsToTerminologyByOpenEHRId(charset, "character sets");
    }
}
