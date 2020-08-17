package org.openehr.bmm.v2.persistence;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.openehr.bmm.persistence.validation.BmmDefinitions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@JsonPropertyOrder({"bmm_version",
    "rm_publisher",
    "schema_name",
    "rm_release",
    "model_name",
    "schema_revision",
    "schema_lifecycle_state",
    "schema_description",
    "schema_author",
    "archetype_closure_packages",
    "archetype_parent_class",
    "archetype_data_value_parent_class",
    "archetype_visualize_descendants_of",
    "includes",
    "packages",
    "primitive_types",
    "class_definitions"

})
public final class PBmmSchema extends PBmmPackageContainer {

    private Map<String, PBmmClass> primitiveTypes;
    private Map<String, PBmmClass> classDefinitions;
    private Map<String, BmmIncludeSpec> includes;

    private String rmPublisher;
    private String rmRelease;
    private String bmmVersion;
    private String modelName;
    private String schemaName;
    private String schemaRevision;
    private String schemaLifecycleState;
    private String schemaAuthor;
    private String schemaDescription;
    private List<String> schemaContributors;

    /** this is present in older BMM files, so must be supported here */
    @Deprecated
    private List<String> archetypeRmClosurePackages;

    // These fields are now stored in the Archetype Profile files, and are
    // deprecated here - included mainly to be able to parse older P_BMM files.
    @Deprecated
    private String archetypeParentClass;
    @Deprecated
    private String archetypeDataValueParentClass;
    @Deprecated
    private String archetypeVisualizeDescendantsOf;

    public Map<String, PBmmClass> getPrimitiveTypes() {
        if (primitiveTypes == null) {
            primitiveTypes = new CaseInsensitiveLinkedHashMap<>();
        }
        return primitiveTypes;
    }

    public void setPrimitiveTypes(Map<String, PBmmClass> primitiveTypes) {
        this.primitiveTypes = primitiveTypes;
    }

    public Map<String, PBmmClass> getClassDefinitions() {
        if (classDefinitions == null) {
            classDefinitions = new CaseInsensitiveLinkedHashMap<>();
        }
        return classDefinitions;
    }

    public void setClassDefinitions(Map<String, PBmmClass> classDefinitions) {
        this.classDefinitions = classDefinitions;
    }

    public Map<String, BmmIncludeSpec> getIncludes() {
        if (includes == null) {
            includes = new CaseInsensitiveLinkedHashMap<>();
        }
        return includes;
    }

    public void setIncludes (Map<String, BmmIncludeSpec> includes) {
        this.includes = includes;
    }

    public String getBmmVersion () {
        return bmmVersion;
    }

    public void setBmmVersion (String bmmVersion) {
        this.bmmVersion = bmmVersion;
    }

    public String getModelName () {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getSchemaName () {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getSchemaRevision() {
        return schemaRevision;
    }

    public void setSchemaRevision(String schemaRevision) {
        this.schemaRevision = schemaRevision;
    }

    public String getSchemaLifecycleState() {
        return schemaLifecycleState;
    }

    public void setSchemaLifecycleState(String schemaLifecycleState) {
        this.schemaLifecycleState = schemaLifecycleState;
    }

    public String getSchemaAuthor() {
        return schemaAuthor;
    }

    public void setSchemaAuthor(String schemaAuthor) {
        this.schemaAuthor = schemaAuthor;
    }

    public String getSchemaDescription() {
        return schemaDescription;
    }

    public void setSchemaDescription(String schemaDescription) {
        this.schemaDescription = schemaDescription;
    }

    public List<String> getSchemaContributors() {
        return schemaContributors;
    }

    public void setSchemaContributors(List<String> schemaContributors) {
        this.schemaContributors = schemaContributors;
    }

    public String getRmPublisher() {
        return rmPublisher;
    }

    public void setRmPublisher(String rmPublisher) {
        this.rmPublisher = rmPublisher;
    }

    public String getRmRelease() {
        return rmRelease;
    }

    public void setRmRelease(String rmRelease) {
        this.rmRelease = rmRelease;
    }

    @Deprecated
    public List<String> getArchetypeRmClosurePackages() {
        if(archetypeRmClosurePackages == null) {
            archetypeRmClosurePackages = new ArrayList<>();
        }
        return archetypeRmClosurePackages;
    }

    @Deprecated
    public void setArchetypeRmClosurePackages(List<String> archetypeRmClosurePackages) {
        this.archetypeRmClosurePackages = archetypeRmClosurePackages;
    }

    public String getArchetypeParentClass() {
        return archetypeParentClass;
    }

    public void setArchetypeParentClass(String archetypeParentClass) {
        this.archetypeParentClass = archetypeParentClass;
    }

    public String getArchetypeDataValueParentClass() {
        return archetypeDataValueParentClass;
    }

    public void setArchetypeDataValueParentClass(String archetypeDataValueParentClass) {
        this.archetypeDataValueParentClass = archetypeDataValueParentClass;
    }

    @JsonAlias({"archetype_visualise_descendants_of"})
    public String getArchetypeVisualizeDescendantsOf() {
        return archetypeVisualizeDescendantsOf;
    }

    public void setArchetypeVisualizeDescendantsOf(String archetypeVisualizeDescendantsOf) {
        this.archetypeVisualizeDescendantsOf = archetypeVisualizeDescendantsOf;
    }

    @JsonIgnore
    public String getSchemaId() {
        return BmmDefinitions.createSchemaId(getRmPublisher(), getSchemaName(), getRmRelease());
    }

    public boolean hasClassOrPrimitiveDefinition(String persistedBmmClass) {
        return getClassDefinitions().containsKey(persistedBmmClass) || getPrimitiveTypes().containsKey(persistedBmmClass);
    }

    /**
     * Finds class either among class definitions or primitive definitions in case primitives are used directly as types.
     *
     * @param className
     * @return
     */
    public PBmmClass getClassDefinition(String className) {
        PBmmClass result = classDefinitions.get(className);
        if (result == null) {
            result = primitiveTypes.get(className);
        }
        return result;
    }

    /**
     * Do some action to all primitive type and class objects
     * process in any order
     *
     * @param action
     */
    public void doAllClasses(Consumer<PBmmClass> action) {
        getPrimitiveTypes().values().forEach(action);
        getClassDefinitions().values().forEach(action);
    }
}
