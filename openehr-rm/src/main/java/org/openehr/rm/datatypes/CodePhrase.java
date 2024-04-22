package org.openehr.rm.datatypes;

import com.nedap.archie.base.RMObject;
import org.openehr.rm.support.identification.TerminologyId;
import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.openehr.rmutil.InvariantUtil;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO: reuse archetype model TerminologyCode? Thing is, that doesn't constrain as nicely with the archetype model...
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CODE_PHRASE", propOrder = {
        "terminologyId",
        "codeString",
        "preferredTerm"
})
public class CodePhrase extends RMObject {

    @XmlElement(name = "terminology_id")
    private TerminologyId terminologyId;
    @XmlElement(name = "code_string")
    private String codeString;
    @Nullable
    @XmlElement(name = "preferred_term")
    private String preferredTerm;

    public CodePhrase() {

    }

    public CodePhrase(TerminologyId terminologyId, String codeString) {
        this(terminologyId, codeString, null);
    }

    public CodePhrase(TerminologyId terminologyId, String codeString, String preferredTerm) {
        this.terminologyId = terminologyId;
        this.codeString = codeString;
        this.preferredTerm = preferredTerm;
    }

    /**
     * Construct a code phrase in the form:
     * <br>
     * [terminologyId::codeString]
     * <br>
     * or
     * <br>
     * terminologyId::codeString
     * <p>
     * terminologyId can be just a a string, or contain a version as in  terminologyId(version)
     *
     * @param phrase
     */
    public CodePhrase(String phrase) {
        //'[' NAME_CHAR+ ( '(' NAME_CHAR+ ')' )? '::' NAME_CHAR+ ']' ;
        Pattern pattern = Pattern.compile("\\[?(?<terminologyId>.+)(\\((?<terminologyVersion>.+)\\))?::(?<codeString>[^\\]]+)\\]?");
        Matcher matcher = pattern.matcher(phrase);

        if (matcher.matches()) {
            terminologyId = new TerminologyId(matcher.group("terminologyId"), matcher.group("terminologyVersion"));
            codeString = matcher.group("codeString");
        } else {
            terminologyId = new TerminologyId();
            terminologyId.setValue("UNKNOWN");
            codeString = phrase;//no id
        }
    }

    public TerminologyId getTerminologyId() {
        return terminologyId;
    }

    public void setTerminologyId(TerminologyId terminologyId) {
        this.terminologyId = terminologyId;
    }

    public String getCodeString() {
        return codeString;
    }

    public void setCodeString(String codeString) {
        this.codeString = codeString;
    }

    public String getPreferredTerm() {
        return preferredTerm;
    }

    public void setPreferredTerm(String preferredTerm) {
        this.preferredTerm = preferredTerm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodePhrase that = (CodePhrase) o;
        return Objects.equals(terminologyId, that.terminologyId) &&
                Objects.equals(codeString, that.codeString) &&
                Objects.equals(preferredTerm, that.preferredTerm);

    }

    @Override
    public int hashCode() {
        return Objects.hash(terminologyId, codeString, preferredTerm);
    }

    public String toString() {
        return terminologyId + "::" + codeString;//TODO: preferredTerm?
    }

    @Invariant("Code_string_valid")
    public boolean codeStringValid() {
        return InvariantUtil.nullOrNotEmpty(codeString);
    }
}
