package com.nedap.archie.aom;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.Map;

/**
 * Created by pieter.bos on 02/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="RESOURCE_ANNOTATION")
public class ResourceAnnotations  extends ArchetypeModelObject {
    //TODO: write more convenient methods than this very deep map
    //TODO: probably a custom XML adapter for JAXB

    //language -> path -> annotation key -> annotation
    //so: en -> /value/items -> 'extra description' -> 'some description here'
    //@XmlElement(name="documentation")
    @XmlTransient
    private Map<String, Map<String, Map<String, String>>> documentation;


    public Map<String, Map<String, Map<String, String>>> getDocumentation() {
        return documentation;
    }

    public void setDocumentation(Map<String, Map<String, Map<String, String>>> documentation) {
        this.documentation = documentation;
    }
}
