package com.nedap.archie.json.flat;

import com.google.common.collect.Sets;

import java.util.Set;


/**
 * The confiration object to configure the FlatJsonGenerator to a specific format
 */
public class FlatJsonFormatConfiguration {

    private boolean writePipesForPrimitiveTypes = false;
    private IndexNotation indexNotation = IndexNotation.AFTER_A_COLON;
    private String typeIdPropertyName = "_type";
    private Set<AttributeReference> ignoredAttributes;

    /**
     * Construct a default flat JSON format configuration, with all fields set to default
     */
    public FlatJsonFormatConfiguration() {
        ignoredAttributes = Sets.newHashSet(
                new AttributeReference("LOCATABLE", "archetype_node_id"),
                new AttributeReference("LOCATABLE", "archetype_details")
        );
    }

    /**
     * Construct the flat JSON format configuration for the Nedap Internal format, with bracketed index, machine readable, no pipes and "@type"
     * @return the created configuration
     */
    public static FlatJsonFormatConfiguration nedapInternalFormat() {
        FlatJsonFormatConfiguration config = new FlatJsonFormatConfiguration();
        config.setWritePipesForPrimitiveTypes(false);
        config.setIndexNotation(IndexNotation.BRACKETED);
        config.setTypeIdPropertyName("@type");
        return config;
    }


    /**
     * Construct the flat JSON format configuration for the in development SDT standard, with :index, machine readable, pipes and "_type"
     * @return the created configuration
     */
    public static FlatJsonFormatConfiguration standardFormatInDevelopment() {
        FlatJsonFormatConfiguration config = new FlatJsonFormatConfiguration();
        config.setWritePipesForPrimitiveTypes(true);
        config.setIndexNotation(IndexNotation.AFTER_A_COLON);
        return config;
    }

    /**
     * Get whether to use the '|'-character as the final delimiter of paths. Defaults to false.
     * @return true if the final delimiter is '|', false if '/'
     */
    public boolean isWritePipesForPrimitiveTypes() {
        return writePipesForPrimitiveTypes;
    }

    /**
     * Set whether to use the '|' character as the final delimiter of paths. Be warned that this behaviour is likely to
     * be changed, because the SDT specification does not specify this clearly, and it is something like this, but not exactly this.
     * @param writePipesForPrimitiveTypes whether to use the '|' character as the final delimiter of paths.
     */
    public void setWritePipesForPrimitiveTypes(boolean writePipesForPrimitiveTypes) {
        this.writePipesForPrimitiveTypes = writePipesForPrimitiveTypes;
    }

    /**
     * Get the numeric index notation. Default is :index.
     * @return the used numeric index notation
     */
    public IndexNotation getIndexNotation() {
        return indexNotation;
    }

    /**
     * Set the numeric index notation to either [index] or :index.
     * @param indexNotation The index notation to use
     */
    public void setIndexNotation(IndexNotation indexNotation) {
        this.indexNotation = indexNotation;
    }

    /**
     * Get the type id property name to the given value. Defaults to "_type"
     * @return the type id property name
     */
    public String getTypeIdPropertyName() {
        return typeIdPropertyName;
    }

    /**
     * Set the type id property name to the given value. Defaults to "_type"
     * @param typeIdPropertyName the value of the type id property name
     */
    public void setTypeIdPropertyName(String typeIdPropertyName) {
        this.typeIdPropertyName = typeIdPropertyName;
    }

    /**
     * Get the ignored attributes. These will not be serialized. Defaults to LOCATABLE.archetype_node_id and LOCATABLE.archetype_details
     * @return the ignore attribute references
     */
    public Set<AttributeReference> getIgnoredAttributes() {
        return ignoredAttributes;
    }

    /**
     * Set the ignored attributes. These will not be serialized. Defaults to LOCATABLE.archetype_node_id and LOCATABLE.archetype_details
     * @param ignoredAttributes the attributes which will be ignored
     **/
    public void setIgnoredAttributes(Set<AttributeReference> ignoredAttributes) {
        this.ignoredAttributes = ignoredAttributes;
    }
}
