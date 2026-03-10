package com.nedap.archie.xml.types;

import com.nedap.archie.base.terminology.TerminologyCode;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TRANSLATION_DETAILS", propOrder = {
        "language",
        "author",
        "accreditation",
        "otherDetails",
        "versionLastTranslated"
})
public class XmlTranslationDetails
{

    @XmlElement(required = true)
    private TerminologyCode language;
    @XmlElement(required = true)
    private List<StringDictionaryItem> author;
    private String accreditation;
    @XmlElement(name = "other_details")
    private List<StringDictionaryItem> otherDetails;
    @XmlElement(name="version_last_translated")
    private String versionLastTranslated;

    public TerminologyCode getLanguage() {
        return language;
    }

    public void setLanguage(TerminologyCode language) {
        this.language = language;
    }

    public List<StringDictionaryItem> getAuthor() {
        return author;
    }

    public void setAuthor(List<StringDictionaryItem> author) {
        this.author = author;
    }

    public String getAccreditation() {
        return accreditation;
    }

    public void setAccreditation(String accreditation) {
        this.accreditation = accreditation;
    }

    public List<StringDictionaryItem> getOtherDetails() {
        return otherDetails;
    }

    public void setOtherDetails(List<StringDictionaryItem> otherDetails) {
        this.otherDetails = otherDetails;
    }

    public void setVersionLastTranslated(String versionLastTranslated) {
        this.versionLastTranslated = versionLastTranslated;
    }

    public String getVersionLastTranslated() {
        return versionLastTranslated;
    }
}
