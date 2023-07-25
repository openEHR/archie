package com.nedap.archie.xml.types;

import com.nedap.archie.aom.rmoverlay.VisibilityType;
import com.nedap.archie.base.terminology.TerminologyCode;

import javax.annotation.Nullable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name="RM_ATTRIBUTE_VISIBILITY")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlRmAttributeVisibility {

    private String path;
    @Nullable
    private VisibilityType visibility;
    @Nullable
    private TerminologyCode alias;

    public XmlRmAttributeVisibility() {
    }

    public XmlRmAttributeVisibility(String path, @Nullable VisibilityType visibility, @Nullable TerminologyCode alias) {
        this.path = path;
        this.visibility = visibility;
        this.alias = alias;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Nullable
    public VisibilityType getVisibility() {
        return visibility;
    }

    public void setVisibility(@Nullable VisibilityType visibility) {
        this.visibility = visibility;
    }

    @Nullable
    public TerminologyCode getAlias() {
        return alias;
    }

    public void setAlias(@Nullable TerminologyCode alias) {
        this.alias = alias;
    }
}
