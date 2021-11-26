package com.nedap.archie.json;

import java.util.Objects;

public class ArchieJacksonConfiguration {
    private String typePropertyName = "@type";
    private boolean alwaysIncludeTypeProperty = true;
    private boolean addPathProperty = true;
    private boolean addExtraFieldsInArchetypeId = true;
    private boolean failOnUnknownProperties = false;
    private boolean serializeEmptyCollections = true;
    private boolean archetypeBooleanIsPrefix = true;

    public ArchieJacksonConfiguration() {

    }

    public static ArchieJacksonConfiguration createStandardsCompliant() {
        ArchieJacksonConfiguration configuration = new ArchieJacksonConfiguration();
        configuration.setTypePropertyName("_type");
        configuration.setAlwaysIncludeTypeProperty(false);
        configuration.setAddPathProperty(false);
        configuration.setAddExtraFieldsInArchetypeId(false);
        return configuration;
    }

    public static ArchieJacksonConfiguration createConfigForJavascriptUsage() {
        ArchieJacksonConfiguration configuration = new ArchieJacksonConfiguration();
        configuration.setTypePropertyName("_type");
        configuration.setAlwaysIncludeTypeProperty(true);
        configuration.setAddPathProperty(true);
        configuration.setAddExtraFieldsInArchetypeId(true);
        return configuration;
    }

    public String getTypePropertyName() {
        return typePropertyName;
    }

    public void setTypePropertyName(String typePropertyName) {
        this.typePropertyName = typePropertyName;
    }

    public boolean isAlwaysIncludeTypeProperty() {
        return alwaysIncludeTypeProperty;
    }

    public void setAlwaysIncludeTypeProperty(boolean alwaysIncludeTypeProperty) {
        this.alwaysIncludeTypeProperty = alwaysIncludeTypeProperty;
    }

    public boolean isAddPathProperty() {
        return addPathProperty;
    }

    public void setAddPathProperty(boolean addPathProperty) {
        this.addPathProperty = addPathProperty;
    }

    public boolean isAddExtraFieldsInArchetypeId() {
        return addExtraFieldsInArchetypeId;
    }

    public void setAddExtraFieldsInArchetypeId(boolean addExtraFieldsInArchetypeId) {
        this.addExtraFieldsInArchetypeId = addExtraFieldsInArchetypeId;
    }

    public boolean isFailOnUnknownProperties() {
        return failOnUnknownProperties;
    }

    public void setFailOnUnknownProperties(boolean failOnUnknownProperties) {
        this.failOnUnknownProperties = failOnUnknownProperties;
    }

    public boolean isSerializeEmptyCollections() {
        return serializeEmptyCollections;
    }

    public void setSerializeEmptyCollections(boolean serializeEmptyCollections) {
        this.serializeEmptyCollections = serializeEmptyCollections;
    }

    /**
     * Set whether to add the is_ prefix on boolean fields in the AOM. Set to true for standard compliance, false
     * for fallback for earlier behaviour
     * @param archetypeBooleanIsPrefix whether to add the is_prefix on boolean fields
     */
    public void setArchetypeBooleanIsPrefix(boolean archetypeBooleanIsPrefix) {
        this.archetypeBooleanIsPrefix = archetypeBooleanIsPrefix;
    }


    /**
     * GET whether to add the is_ prefix on boolean fields in the AOM. Set to true for standard compliance, false
     * for fallback for earlier behaviour
     * @return whether to add the is_prefix on boolean fields
     */
    public boolean isArchetypeBooleanIsPrefix() {
        return archetypeBooleanIsPrefix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArchieJacksonConfiguration)) return false;
        ArchieJacksonConfiguration that = (ArchieJacksonConfiguration) o;
        return alwaysIncludeTypeProperty == that.alwaysIncludeTypeProperty &&
                addPathProperty == that.addPathProperty &&
                addExtraFieldsInArchetypeId == that.addExtraFieldsInArchetypeId &&
                failOnUnknownProperties == that.failOnUnknownProperties &&
                serializeEmptyCollections == that.serializeEmptyCollections &&
                Objects.equals(typePropertyName, that.typePropertyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typePropertyName, alwaysIncludeTypeProperty, addPathProperty, addExtraFieldsInArchetypeId, failOnUnknownProperties, serializeEmptyCollections);
    }
}
