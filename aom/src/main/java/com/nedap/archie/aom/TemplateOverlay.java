package com.nedap.archie.aom;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * Template overlay class, for the template overlays used in a template. Has one implementation specific addition to the normal archetype: methods to obtain the owning archetype
 * plus methods to get the adl version and rm release of the owning archetype
 * This makes for less instanceof calls in obtaining the BMM model of the overlay
 *
 * Created by pieter.bos on 15/10/15.
 */
@XmlType(name="TEMPLATE_OVERLAY")
public class TemplateOverlay extends Archetype {

    @JsonIgnore
    private transient Template owningTemplate;

    @JsonIgnore
    @XmlTransient
    @Override
    public String getRmRelease() {
        return owningTemplate == null ? super.getRmRelease() : owningTemplate.getRmRelease();
    }

    @JsonIgnore
    @XmlTransient
    @Override
    public String getAdlVersion() {
        return owningTemplate == null ? null : owningTemplate.getAdlVersion();
    }

    @JsonIgnore
    @XmlTransient
    public Template getOwningTemplate() {
        return owningTemplate;
    }


    public void setOwningTemplate(Template owningTemplate) {
        this.owningTemplate = owningTemplate;
    }
}
