package com.nedap.archie.xml.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;


@XmlType(name="RM_OVERLAY")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlRMOverlay {
    @XmlElement(name="rm_visibility")
    private List<XmlRMAttributeVisibility> rmVisibility;

    public XmlRMOverlay() {
    }

    public XmlRMOverlay(List<XmlRMAttributeVisibility> visibility) {
        this.rmVisibility = visibility;
    }

    public List<XmlRMAttributeVisibility> getRmVisibility() {
        return rmVisibility;
    }

    public void setRmVisibility(List<XmlRMAttributeVisibility> rmVisibility) {
        this.rmVisibility = rmVisibility;
    }
}
