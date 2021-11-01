package com.nedap.archie.aom.profile;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Data object expressing a mapping between two types.
 *
 * Created by cnanjo on 5/30/16.
 */
public class AomTypeMapping {

    /**
     * Name of the AOM type being mapped to an RM type.
     */
    private String sourceClassName;
    /**
     * Name of the RM type in the mapping.
     */
    private String targetClassName;
    /**
     * List of mappings of properties of this type to another type.
     */
    @Nullable
    private Map<String, AomPropertyMapping> propertyMappings;

    /**
     *
     * @return Name of the AOM type being mapped to an RM type.
     */
    public String getSourceClassName() {
        return sourceClassName;
    }

    /**
     *
     * @param sourceClassName Name of the AOM type being mapped to an RM type.
     */
    public void setSourceClassName(String sourceClassName) {
        this.sourceClassName = sourceClassName;
    }

    /**
     *
     * @return Name of the RM type in the mapping.
     */
    public String getTargetClassName() {
        return targetClassName;
    }

    /**
     *
     * @param targetClassName Name of the RM type in the mapping.
     */
    public void setTargetClassName(String targetClassName) {
        this.targetClassName = targetClassName;
    }

    /**
     *
     * @return List of mappings of properties of this type to another type.
     */
    public Map<String, AomPropertyMapping> getPropertyMappings() {
        return propertyMappings;
    }

    /**
     *
     * @param propertyMappings List of mappings of properties of this type to another type.
     */
    public void setPropertyMappings(Map<String, AomPropertyMapping> propertyMappings) {
        this.propertyMappings = propertyMappings;
    }
}
