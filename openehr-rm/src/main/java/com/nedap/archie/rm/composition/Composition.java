package com.nedap.archie.rm.composition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nedap.archie.rm.archetyped.*;
import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.generic.PartyProxy;
import com.nedap.archie.rm.support.identification.UIDBasedId;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
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
@XmlRootElement(name = "composition")
public class Composition extends Locatable {

    private CodePhrase language;
    private CodePhrase territory;
    private DvCodedText category;

    private PartyProxy composer;

    @Nullable
    private EventContext context;

    @Nullable
    private List<ContentItem> content = new ArrayList<>();

    public Composition() {
    }

    public Composition(String archetypeNodeId, DvText name, @Nullable List<ContentItem> content, CodePhrase language, @Nullable EventContext context, PartyProxy composer, DvCodedText category, CodePhrase territory) {
        super(archetypeNodeId, name);
        this.language = language;
        this.territory = territory;
        this.category = category;
        this.composer = composer;
        this.context = context;
        this.content = content;
    }

    public Composition(@Nullable UIDBasedId uid, String archetypeNodeId, DvText name, @Nullable Archetyped archetypeDetails, @Nullable FeederAudit feederAudit, @Nullable List<Link> links, @Nullable Pathable parent, @Nullable String parentAttributeName, @Nullable List<ContentItem> content, CodePhrase language, @Nullable EventContext context, PartyProxy composer, DvCodedText category, CodePhrase territory) {
        super(uid, archetypeNodeId, name, archetypeDetails, feederAudit, links, parent, parentAttributeName);
        this.language = language;
        this.territory = territory;
        this.category = category;
        this.composer = composer;
        this.context = context;
        this.content = content;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Composition that = (Composition) o;

        if (language != null ? !language.equals(that.language) : that.language != null) return false;
        if (territory != null ? !territory.equals(that.territory) : that.territory != null) return false;
        if (category != null ? !category.equals(that.category) : that.category != null) return false;
        if (composer != null ? !composer.equals(that.composer) : that.composer != null) return false;
        if (context != null ? !context.equals(that.context) : that.context != null) return false;
        return content != null ? content.equals(that.content) : that.content == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (language != null ? language.hashCode() : 0);
        result = 31 * result + (territory != null ? territory.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (composer != null ? composer.hashCode() : 0);
        result = 31 * result + (context != null ? context.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }
}
