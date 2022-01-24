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
    private boolean addPatternConstraintTypo = false;
    private boolean standardsCompliantExpressions = true;

    private ArchieJacksonConfiguration() {

    }

    /**
     * Creates a legacy Archie jackson configuration, for backwards compatible output.
     * Has a couple of property naming differences and uses "@type" as a type property.
     * <br>
     * Do not use unless you need the json output to be backwards compatible with old archie versions or other applications
     * expecting this format.
     * <br>
     * It can parse both the standards compliant and legacy version of the archie output, as long as it uses "_type".
     * The only thing non-standard it cannot parse is "@type" instead of "_type"
     * Optionally modify config as necessary before using.
     * @return a legacy Archie Jackson configuration
     */
    @Deprecated
    public static ArchieJacksonConfiguration createLegacyConfiguration() {
        ArchieJacksonConfiguration configuration = new ArchieJacksonConfiguration();
        configuration.setTypePropertyName("@type");
        configuration.setArchetypeBooleanIsPrefix(false);
        configuration.setStandardsCompliantExpressions(false);
        configuration.setAddPatternConstraintTypo(true);
        return configuration;
    }

    /**
     * Creates a standards compliant jackson configuration for Archie.
     * It can parse both the standards compliant and legacy version of the archie output, as long as it uses "_type"
     * Optionally modify config as necessary before using.
     * @return a legacy Archie Jackson configuration
     */
    public static ArchieJacksonConfiguration createStandardsCompliant() {
        ArchieJacksonConfiguration configuration = new ArchieJacksonConfiguration();
        configuration.setTypePropertyName("_type");
        configuration.setAlwaysIncludeTypeProperty(false);
        configuration.setAddPathProperty(false);
        configuration.setAddExtraFieldsInArchetypeId(false);
        return configuration;
    }

    /**
     * Creates a standards compliant jackson configuration for Archie, with some extra fields added for easier usage in
     * environments where there is no RM implementation
     * <br>
     * Extra fields include:
     * <ul>
     *   <li>path properties everywhere in the RM, so they do not have to be calculated at runtime</li>
     *   <li>the "_type" is included always on all classes, not just when strictly necessary</li>
     *   <li>The archetype id has some extra fields so no parser is necessary on client side</li>
     * </ul>
     * It can parse both the standards compliant and legacy version of the archie output, as long as it uses "_type"
     * Optionally modify config as necessary before using.
     * @return a legacy Archie Jackson configuration
     */
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
     * the new behaviour can always parse the old behaviour.
     * Marked deprecated since in some future version the old format will be removed.
     * @param archetypeBooleanIsPrefix whether to add the is_prefix on boolean fields
     */
    @Deprecated
    public void setArchetypeBooleanIsPrefix(boolean archetypeBooleanIsPrefix) {
        this.archetypeBooleanIsPrefix = archetypeBooleanIsPrefix;
    }


    /**
     * GET whether to add the is_ prefix on boolean fields in the AOM. Set to true for standard compliance, false
     * for fallback for earlier behaviour
     * the new behaviour can always parse the old behaviour.
     * @return whether to add the is_prefix on boolean fields
     */
    public boolean isArchetypeBooleanIsPrefix() {
        return archetypeBooleanIsPrefix;
    }

    /**
     * Return whether pattern constraint should be named patterned constraint for backwards compatibility
     * the new behaviour can always parse the old behaviour
     * @return whether pattern constraint should be named patterned constraint for backwards compatibility
     */
    public boolean isAddPatternConstraintTypo() {
        return addPatternConstraintTypo;
    }

    /**
     * Set whether pattern constraint should be named patterned constraint for backwards compatibility
     * the new behaviour can always parse the old behaviour
     * Marked deprecated since in some future version the old format will be removed.
     * @param addPatternConstraintTypo whether pattern constraint should be named patterned constraint for backwards compatibility
     */
    @Deprecated
    public void setAddPatternConstraintTypo(boolean addPatternConstraintTypo) {
        this.addPatternConstraintTypo = addPatternConstraintTypo;
    }

    /**
     * Return  whether the expression/rule part of the AOM should use standards compliant class names, or whether it should
     * revert back to the old behaviour, which is an older standard without the 'EXPR_' prefix that is in the standard
     * If the new setting (standards compliant) is used, the parser will still allow both the old and new format, so this
     * will affect the serialized json only.
     * Defaults to true
     * @return true for the standards compliant class names, false otherwise
     */
    public boolean isStandardsCompliantExpressions() {
        return standardsCompliantExpressions;
    }

    /**
     * Set whether the expression/rule part of the AOM should use standards compliant class names, or whether it should
     * revert back to the old behaviour, which is an older standard without the 'EXPR_' prefix that is in the standard.
     * If the new setting (standards compliant) is used, the parser will still allow both the old and new format, so this
     * will affect the serialized json only.
     * Defaults to true, so calling this is only to disable it and is marked deprecated, so the old behaviour can at some point
     * be removed
     * @param standardsCompliantExpressions true for the standards compliant class names, false otherwise
     */
    @Deprecated
    public void setStandardsCompliantExpressions(boolean standardsCompliantExpressions) {
        this.standardsCompliantExpressions = standardsCompliantExpressions;
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
                archetypeBooleanIsPrefix == that.archetypeBooleanIsPrefix &&
                addPatternConstraintTypo == that.addPatternConstraintTypo &&
                standardsCompliantExpressions == that.standardsCompliantExpressions &&
                Objects.equals(typePropertyName, that.typePropertyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typePropertyName, alwaysIncludeTypeProperty, addPathProperty, addExtraFieldsInArchetypeId, failOnUnknownProperties, serializeEmptyCollections, archetypeBooleanIsPrefix, addPatternConstraintTypo, standardsCompliantExpressions);
    }
}
