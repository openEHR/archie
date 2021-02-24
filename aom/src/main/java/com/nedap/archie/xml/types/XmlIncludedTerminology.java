package com.nedap.archie.xml.types;

import com.nedap.archie.aom.terminology.ArchetypeTerminology;

import javax.xml.bind.annotation.XmlType;

@XmlType(name="INCLUDED_TERMINOLOGY")
public class XmlIncludedTerminology extends XmlArchetypeTerminology {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
