package com.nedap.archie.xml.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;


@XmlType(name="RM_OVERLAY")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlRmOverlay {
    @XmlElement(name="rm_visibility")
    private List<XmlRmAttributeVisibility> rmVisibility;

    public XmlRmOverlay() {
    }

    public XmlRmOverlay(List<XmlRmAttributeVisibility> visibility) {
        this.rmVisibility = visibility;
    }

    public List<XmlRmAttributeVisibility> getRmVisibility() {
        return rmVisibility;
    }

    public void setRmVisibility(List<XmlRmAttributeVisibility> rmVisibility) {
        this.rmVisibility = rmVisibility;
    }
}
