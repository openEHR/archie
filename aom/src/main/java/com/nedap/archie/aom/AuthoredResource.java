package com.nedap.archie.aom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nedap.archie.base.terminology.TerminologyCode;
import com.nedap.archie.xml.adapters.ResourceDescriptionAdapter;
import com.nedap.archie.xml.adapters.TranslationDetailsAdapter;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Authored Resource.
 *
 * Contains a small deviation from the spec that should not be noticed during use:
 * All methods, including get and set methods from the AOM spec are present.
 * However, where normally every attribute is directly mapped to a field, the field from the language section of an
 * archetype in ADL are mapped to a LanguageSection object. This is done to enable odin parsing directly from odin to
 * objects, instead of steps in between.
 *
 * Created by pieter.bos on 15/10/15.
 */
@XmlType(name="AUTHORED_RESOURCE")
@XmlAccessorType(XmlAccessType.PROPERTY)
public abstract class AuthoredResource extends ArchetypeModelObject {

    @Nullable
    private Boolean controlled;
    private String uid;
    @Nullable
    private ResourceDescription description;

    private LanguageSection content = new LanguageSection();

    private ResourceAnnotations annotations;

    @XmlElement(name="is_controlled")
    public Boolean getControlled() {
        return controlled;
    }

    public void setControlled(Boolean controlled) {
        this.controlled = controlled;
    }

    @XmlElement(name="uid")
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @XmlElement(name="description")
    @XmlJavaTypeAdapter(ResourceDescriptionAdapter.class)
    public ResourceDescription getDescription() {
        return description;
    }

    public void setDescription(ResourceDescription description) {
        this.description = description;
    }

    @XmlElement(name="original_language")
    public TerminologyCode getOriginalLanguage() {
        if(content == null) {
            return null;
        }
        return content.getOriginalLanguage();
    }

    public void setOriginalLanguage(TerminologyCode originalLanguage) {
        if(content == null) {
            content = new LanguageSection();
        }
        content.setOriginalLanguage(originalLanguage);
    }

    @XmlTransient
    public Map<String, TranslationDetails> getTranslations() {
        if(content == null) {
            return null;
        }
        return content.getTranslations();
    }

    public void setTranslations(Map<String, TranslationDetails> translations) {
        if(content == null) {
            content = new LanguageSection();
        }
        content.setTranslations(translations);
    }

    @XmlElement(name="translations")
    @XmlJavaTypeAdapter(TranslationDetailsAdapter.class)
    @JsonIgnore
    public List<TranslationDetails> getTranslationList() { return new ArrayList(content.getTranslations().values());}

    public void setTranslationList(List<TranslationDetails> translationList) {
        LinkedHashMap<String, TranslationDetails> translations = new LinkedHashMap<>();
        for(TranslationDetails translationDetails:translationList) {
            translations.put(translationDetails.getLanguage().getCodeString(), translationDetails);
        }
        content.setTranslations(translations);
    }

    @XmlElement(name="annotations")
    public ResourceAnnotations getAnnotations() {
        return annotations;
    }

    public void setAnnotations(ResourceAnnotations annotations) {
        this.annotations = annotations;
    }

    /**
     * Not in archetype object model specs, but this is the full content of the language section.
     *   All methods of this languageSection class are directly exposed by methods of AuthoredResource
     */
    @JsonIgnore
    @XmlTransient
    public LanguageSection getAuthoredResourceContent() {
        return content;
    }


    public void setAuthoredResourceContent(LanguageSection content) {
        this.content = content;
    }
}
