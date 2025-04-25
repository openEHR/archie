package com.nedap.archie.xml.types;

import com.nedap.archie.aom.ArchetypeModelObject;
import com.nedap.archie.aom.terminology.ArchetypeTerm;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Created by pieter.bos on 22/07/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CodeDefinitionSet", propOrder = {
        "items"
})
public class CodeDefinitionSet
        extends ArchetypeModelObject
{
    @XmlElement(type = ArchetypeTerm.class)
    private List<ArchetypeTerm> items;
    @XmlAttribute(name = "language", required = true)
    private String language;

    public List<ArchetypeTerm> getItems() {
        return items;
    }

    public void setItems(List<ArchetypeTerm> items) {
        this.items = items;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
