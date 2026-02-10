package com.nedap.archie.base.terminology;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.nedap.archie.base.OpenEHRBase;

import javax.annotation.Nullable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pieter.bos on 15/10/15.
 */
@XmlType(name="TERMINOLOGY_CODE")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, defaultImpl=TerminologyCode.class)
public class TerminologyCode extends OpenEHRBase {

    @XmlElement(name="terminology_id")
    private String terminologyId;
    @XmlElement(name="terminology_version")
    @Nullable
    private String terminologyVersion;
    @XmlElement(name="code_string")
    private String codeString;

    private URI uri;

    @JsonCreator
    public static TerminologyCode createFromString(String terminologyString) {
        if(terminologyString.isEmpty()) {
            return null;
        }
        //'[' NAME_CHAR+ ( '(' NAME_CHAR+ ')' )? '::' NAME_CHAR+ ']' ;
        Pattern pattern = Pattern.compile("\\[(?<terminologyId>[^\\(\\)]+)(\\((?<terminologyVersion>.+)\\))?::(?<codeString>.+)\\]");
        Matcher matcher = pattern.matcher(terminologyString);
        TerminologyCode result = new TerminologyCode();
        if(matcher.matches()) {
            result.setTerminologyId(matcher.group("terminologyId"));
            result.setCodeString(matcher.group("codeString"));
            result.setTerminologyVersion(matcher.group("terminologyVersion"));

        } else {
            result.setCodeString(terminologyString);
        }
        return result;
    }

    @JsonCreator
    public static TerminologyCode createFromString(@JsonProperty("terminology_id") String terminologyId,
                                                   @JsonProperty("terminology_version") String terminologyVersion,
                                                   @JsonProperty("code_string") String codeString) {
        TerminologyCode result = new TerminologyCode();
        result.terminologyId = terminologyId;
        result.terminologyVersion = terminologyVersion;
        result.codeString = codeString;
        return result;
    }

    public String getTerminologyVersion() {
        return terminologyVersion;
    }

    public void setTerminologyVersion(String terminologyVersion) {
        this.terminologyVersion = terminologyVersion;
    }

    public String getTerminologyIdString() {
        return getTerminologyId();
    }

    public String toString() {
        return terminologyVersion == null ?
                "[" + getTerminologyId() + "::" + getCodeString() + "]" :
                "[" + getTerminologyId() + "(" + terminologyVersion + ")::" + getCodeString() + "]";
    }

    public String getTerminologyId() {
        return terminologyId;
    }

    public void setTerminologyId(String terminologyId) {
        this.terminologyId = terminologyId;
    }

    public String getCodeString() {
        return codeString;
    }

    public void setCodeString(String codeString) {
        this.codeString = codeString;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }
}
