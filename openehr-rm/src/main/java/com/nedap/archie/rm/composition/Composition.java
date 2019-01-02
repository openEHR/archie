package com.nedap.archie.rm.composition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nedap.archie.rm.archetyped.Locatable;
import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.generic.PartyProxy;
import com.nedap.archie.rm.datavalues.DvCodedText;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pieter.bos on 03/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "COMPOSITION", propOrder = {
        "language",
        "territory",
        "category",
        "composer",
        "context",
        "content"
})
@XmlRootElement(name="composition")
public class Composition extends Locatable {

    private CodePhrase language;
    private CodePhrase territory;
    private DvCodedText category;

    private PartyProxy composer;

    @Nullable
    private EventContext context;

    @Nullable
    private List<ContentItem> content = new ArrayList<>();

    @JsonProperty
    public CodePhrase getLanguage() {
        return language;
    }

    @JsonProperty
    public void setLanguage(CodePhrase language) {
        this.language = language;
    }

    @JsonIgnore
    public void setLanguage(String codePhrase) {
        this.language = new CodePhrase(codePhrase);
    }

    @JsonProperty
    public CodePhrase getTerritory() {
        return territory;
    }

    @JsonProperty
    public void setTerritory(CodePhrase territory) {
        this.territory = territory;
    }

    @JsonIgnore
    public void setTerritory(String codePhrase) {
        this.territory = new CodePhrase(codePhrase);
    }

    @JsonProperty
    public DvCodedText getCategory() {
        return category;
    }

    @JsonProperty
    public void setCategory(DvCodedText category) {
        this.category = category;
    }

    @JsonIgnore
    public void setCategory(String codePhrase) {
        this.category = new DvCodedText();
        category.setDefiningCode(new CodePhrase(codePhrase));
    }

    public PartyProxy getComposer() {
        return composer;
    }

    public void setComposer(PartyProxy composer) {
        this.composer = composer;
    }

    @Nullable
    public EventContext getContext() {
        return context;
    }

    public void setContext(@Nullable EventContext context) {
        this.context = context;
        setThisAsParent(context, "context");
    }

    public List<ContentItem> getContent() {
        return content;
    }

    public void setContent(List<ContentItem> content) {
        this.content = content;
        setThisAsParent(content, "content");
    }

    public void addContent(ContentItem item) {
        this.content.add(item);
        setThisAsParent(item, "content");
    }


}
