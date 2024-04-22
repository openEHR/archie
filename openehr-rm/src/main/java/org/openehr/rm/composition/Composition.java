package org.openehr.rm.composition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.openehr.rm.archetyped.*;
import org.openehr.rm.datatypes.CodePhrase;
import org.openehr.rm.datavalues.DvCodedText;
import org.openehr.rm.datavalues.DvText;
import org.openehr.rm.generic.PartyProxy;
import org.openehr.rm.support.identification.UIDBasedId;
import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.openehr.rmutil.InvariantUtil;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    public void setCategory(String value, String codePhrase) {
        this.category = new DvCodedText(value, codePhrase);
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
        return Objects.equals(language, that.language) &&
                Objects.equals(territory, that.territory) &&
                Objects.equals(category, that.category) &&
                Objects.equals(composer, that.composer) &&
                Objects.equals(context, that.context) &&
                Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), language, territory, category, composer, context, content);
    }

    @Invariant("Category_validity")
    public boolean categoryValid() {
        return InvariantUtil.belongsToTerminologyByGroupId(category, "composition category");
    }

    @Invariant("Territory_valid")
    public boolean territoryValid() {
        return InvariantUtil.belongsToTerminologyByOpenEHRId(territory, "countries");
    }

    @Invariant("Language_valid")
    public boolean languageValid() {
        return InvariantUtil.belongsToTerminologyByOpenEHRId(language, "languages");
    }

    @Invariant(value="Content valid", ignored=true)
    public boolean contentValid() {
        return InvariantUtil.nullOrNotEmpty(content);
    }

    @Invariant("Is_archetype_root")
    public boolean archetypeRoot() {
        return isArchetypeRoot();
    }
}
