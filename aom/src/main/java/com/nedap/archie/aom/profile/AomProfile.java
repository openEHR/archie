package com.nedap.archie.aom.profile;

import java.util.List;

import java.util.Map;

/**
 * Profile of common settings relating to use of reference model(s) and terminology for a given archetype
 * developing organisation.
 *
 * Created by cnanjo on 5/30/16.
 */
public class AomProfile {

    /**
     * Name of this profile, usually based on the publisher it pertains to e.g. "openEHR", "cdisc", etc.
     */
    private String profileName;

    private List<String> rmSchemaPattern;

    private String archetypeVisualiseDescendantsOf;

    private String archetypeParentClass;

    private String archetypeDataValueParentClass;

    /**
     * Allowed type substitutions: Actual RM type names keyed by AOM built-in types which can substitute
     * for them in an archetype. E.g. &lt;value = "String", key = "ISO8601_DATE"&gt; means that if RM property
     * TYPE.some_property is of type String, an ISO8601_DATE is allowed at that position in the archetype.
     */
    private Map<String, String> aomRmTypeSubstitutions;
    /**
     * List of mappings of lifecycle state names used in archetypes to AOM lifecycle state names. value = AOM
     * lifecycle state; key = source lifecycle state.
     */
    private Map<String, String> aomLifecycleMappings;

    /**
     * Mappings from AOM built-in types to actual types in RM: whenever the type name is encountered in
     * an archetype, it is mapped to a specific RM type.
     */
    private Map<String, AomTypeMapping> aomRmTypeMappings;

    private Map<String, String> rmPrimitiveTypeEquivalences;

    private AomTerminologyProfile terminologyProfile;

    /**
     * @return Allowed type substitutions: Actual RM type names keyed by AOM built-in types which can substitute
     * for them in an archetype. E.g. &lt;value = "String", key = "ISO8601_DATE"&gt; means that if RM property
     * TYPE.some_property is of type String, an ISO8601_DATE is allowed at that position in the archetype.
     */
    public Map<String, String> getAomRmTypeSubstitutions() {
        return aomRmTypeSubstitutions;
    }

    /**
     *
     * @param aomRmTypeSubstitutions Allowed type substitutions: Actual RM type names keyed by AOM built-in types which can substitute
     * for them in an archetype. E.g. &lt;value = "String", key = "ISO8601_DATE"&gt; means that if RM property
     * TYPE.some_property is of type String, an ISO8601_DATE is allowed at that position in the archetype.
     */
    public void setAomRmTypeSubstitutions(Map<String, String> aomRmTypeSubstitutions) {
        this.aomRmTypeSubstitutions = aomRmTypeSubstitutions;
    }

    /**
     *
     * @return List of mappings of lifecycle state names used in archetypes to AOM lifecycle state names. value = AOM
     * lifecycle state; key = source lifecycle state.
     */
    public Map<String, String> getAomLifecycleMappings() {
        return aomLifecycleMappings;
    }

    /**
     *
     * @param aomLifecycleMappings List of mappings of lifecycle state names used in archetypes to AOM lifecycle state names. value = AOM
     * lifecycle state; key = source lifecycle state.
     */
    public void setAomLifecycleMappings(Map<String, String> aomLifecycleMappings) {
        this.aomLifecycleMappings = aomLifecycleMappings;
    }

    /**
     *
     * @return Name of this profile, usually based on the publisher it pertains to e.g. "openEHR", "cdisc", etc.
     */
    public String getProfileName() {
        return profileName;
    }

    /**
     *
     * @param profileName Name of this profile, usually based on the publisher it pertains to e.g. "openEHR", "cdisc", etc.
     */
    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    /**
     *
     * @return Mappings from AOM built-in types to actual types in RM: whenever the type name is encountered in
     * an archetype, it is mapped to a specific RM type.
     */
    public Map<String, AomTypeMapping> getAomRmTypeMappings() {
        return aomRmTypeMappings;
    }

    /**
     *
     * @param aomRmTypeMappings Mappings from AOM built-in types to actual types in RM: whenever the type name is encountered in
     * an archetype, it is mapped to a specific RM type.
     */
    public void setAomRmTypeMappings(Map<String, AomTypeMapping> aomRmTypeMappings) {
        this.aomRmTypeMappings = aomRmTypeMappings;
    }

    public Map<String, String> getRmPrimitiveTypeEquivalences() {
        return rmPrimitiveTypeEquivalences;
    }

    public void setRmPrimitiveTypeEquivalences(Map<String, String> rmPrimitiveTypeEquivalences) {
        this.rmPrimitiveTypeEquivalences = rmPrimitiveTypeEquivalences;
    }

    public List<String> getRmSchemaPattern() {
        return rmSchemaPattern;
    }

    public void setRmSchemaPattern(List<String> rmSchemaPattern) {
        this.rmSchemaPattern = rmSchemaPattern;
    }

    public String getArchetypeParentClass() {
        return archetypeParentClass;
    }

    public void setArchetypeParentClass(String archetypeVisualiseDescendantsOf) {
        this.archetypeParentClass = archetypeParentClass;
    }

    public String getArchetypeVisualiseDescendantsOf() {
        return archetypeVisualiseDescendantsOf;
    }

    public void setArchetypeVisualiseDescendantsOf(String archetypeVisualiseDescendantsOf) {
        this.archetypeVisualiseDescendantsOf = archetypeVisualiseDescendantsOf;
    }

    public String getArchetypeDataValueParentClass() {
        return archetypeDataValueParentClass;
    }

    public void setArchetypeDataValueParentClass(String archetypeDataValueParentClass) {
        this.archetypeDataValueParentClass = archetypeDataValueParentClass;
    }

    public AomTerminologyProfile getTerminologyProfile() {
        return terminologyProfile;
    }

    public void setTerminologyProfile(AomTerminologyProfile terminologyProfile) {
        this.terminologyProfile = terminologyProfile;
    }
}
