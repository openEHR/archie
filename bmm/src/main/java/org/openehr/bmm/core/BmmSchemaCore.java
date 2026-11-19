package org.openehr.bmm.core;

/*
 * #%L
 * OpenEHR - Java Model Stack
 * %%
 * Copyright (C) 2016 - 2017 Cognitive Medical Systems
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 * Author: Claude Nanjo
 */

import org.openehr.bmm.persistence.validation.BmmDefinitions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Core properties of BMM_SCHEMA.
 *
 * Created by cnanjo on 4/11/16.
 */
public class BmmSchemaCore implements IBmmSchemaCore, Serializable {
    /**
     * Publisher of model expressed in the schema. Persisted attribute.
     */
    private String rmPublisher;
    /**
     * Release of model expressed in the schema as a 3-part numeric, e.g. "3.1.0" . Persisted attribute.
     */
    private String rmRelease;
    /**
     * Name of model expressed in schema; a 'schema' usually contains all of the packages of one 'model'
     * of a publisher. A publisher with more than one model can have multiple schemas. Persisted attribute.
     */
    private String schemaName;
    /**
     * Revision of schema. Persisted attribute.
     */
    private String schemaRevision;
    /**
     * Persisted attribute.
     */
    private String schemaLifecycleState;
    /**
     * Primary author of schema. Persisted attribute.
     */
    private String schemaAuthor;
    /**
     * Description of schema. Persisted attribute.
     */
    private String schemaDescription;
    /**
     * Contributing authors of schema. Persisted attribute.
     */
    private List<String> schemaContributors;
    /**
     * Name of a parent class used within the schema to provide archetype capability, enabling filtering of
     * classes in RM visualisation. If empty, 'Any' is assumed. Persisted attribute.
     */
    private String archetypeParentClass;
    /**
     * Name of a parent class of logical 'data types' used within the schema to provide archetype capability,
     * enabling filtering of classes in RM visualisation. If empty, 'Any' is assumed. Persisted attribute.
     */
    private String archetypeDataValueParentClass;
    /**
     * List of top-level package paths that provide the RM 'model' part in achetype identifiers, e.g. the path
     * "org.openehr.ehr" gives "EHR" in "openEHR-EHR". Within this namespace, archetypes can be based on any
     * class reachable from classes defined directly in these packages.
     *
     * Persisted attribute.
     */
    private List<String> archetypeRmClosurePackages;
    /**
     * If archetype_parent_class is not set, designate a class whose descendants should be made visible in tree and grid
     * renderings of the archetype definition. For openEHR and CEN this class is normally the same as the archetype_parent_class,
     * i.e. LOCATABLE and RECORD_COMPONENT respectively. It is typically set for CEN, because archetype_parent_class may not
     * be stated, due to demographic types not inheriting from it.
     *
     * The effect of this attribute in visualisation is to generate the most natural tree or grid-based view of an
     * archetype definition, from the semantic viewpoint.
     *
     * Persisted attribute.
     */
    private String archetypeVisualizeDescendantsOf;

    private String modelName;

    /**
     * No-arg constructor
     */
    public BmmSchemaCore() {
        schemaContributors = new ArrayList<>();
        archetypeRmClosurePackages = new ArrayList<>();
    }

    public BmmSchemaCore(String aRmPublisher, String aSchemaName, String aRmRelease) {
        this();
        rmPublisher = aRmPublisher;
        schemaName = aSchemaName;
        rmRelease = aRmRelease;
    }

    /**
     * Returns the publisher of model expressed in the schema.
     *
     * @return
     */
    public String getRmPublisher() {
        return rmPublisher;
    }

    /**
     * Sets the publisher of model expressed in the schema.
     *
     * @param rmPublisher
     */
    public void setRmPublisher(String rmPublisher) {
        this.rmPublisher = rmPublisher;
    }

    /**
     * Returns the release of model expressed in the schema as a 3-part numeric, e.g. "3.1.0" .
     *
     * @return
     */
    public String getRmRelease() {
        return rmRelease;
    }

    /**
     * Sets the release of model expressed in the schema as a 3-part numeric, e.g. "3.1.0" .
     *
     * @param rmRelease
     */
    public void setRmRelease(String rmRelease) {
        this.rmRelease = rmRelease;
    }

    /**
     * Returns the name of model expressed in schema; a 'schema' usually contains all of the packages of one 'model'
     * of a publisher. A publisher with more than one model can have multiple schemas. Persisted attribute.
     *
     * @return
     */
    public String getSchemaName() {
        return schemaName;
    }

    /**
     * Sets the name of model expressed in schema; a 'schema' usually contains all of the packages of one 'model'
     * of a publisher. A publisher with more than one model can have multiple schemas. Persisted attribute.
     *
     * @param schemaName
     */
    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    /**
     * Returns the revision of schema.
     *
     * @return
     */
    public String getSchemaRevision() {
        return schemaRevision;
    }

    /**
     * Sets the revision of schema.
     *
     * @param schemaRevision
     */
    public void setSchemaRevision(String schemaRevision) {
        this.schemaRevision = schemaRevision;
    }

    /**
     * Returns the lifecycle state of the schema.
     *
     * @return
     */
    public String getSchemaLifecycleState() {
        return schemaLifecycleState;
    }

    /**
     * Sets the lifecycle state of the schema.
     *
     * @param schemaLifecycleState
     */
    public void setSchemaLifecycleState(String schemaLifecycleState) {
        this.schemaLifecycleState = schemaLifecycleState;
    }

    /**
     * Returns the primary author of schema.
     *
     * @return
     */
    public String getSchemaAuthor() {
        return schemaAuthor;
    }

    /**
     * Sets the primary author of schema.
     *
     * @param schemaAuthor
     */
    public void setSchemaAuthor(String schemaAuthor) {
        this.schemaAuthor = schemaAuthor;
    }

    /**
     * Returns the description of schema.
     *
     * @return
     */
    public String getSchemaDescription() {
        return schemaDescription;
    }

    /**
     * Sets the description of schema.
     *
     * @param schemaDescription
     */
    public void setSchemaDescription(String schemaDescription) {
        this.schemaDescription = schemaDescription;
    }

    /**
     * Returns the contributing authors of schema.
     *
     * @return
     */
    public List<String> getSchemaContributors() {
        return schemaContributors;
    }

    /**
     * Sets the contributing authors of schema.
     *
     * @param schemaContributors
     */
    public void setSchemaContributors(List<String> schemaContributors) {
        this.schemaContributors = schemaContributors;
    }

    public boolean hasSchemaContributor(String aSchemaContributor) {
        return this.schemaContributors.contains(aSchemaContributor);
    }

    public void addSchemaContributor(String aContributor) {
        this.schemaContributors.add(aContributor);
    }

    /**
     * Returns the name of a parent class used within the schema to provide archetype capability, enabling filtering of
     * classes in RM visualisation. If empty, 'Any' is assumed. Persisted attribute.
     *
     * @return
     */
    public String getArchetypeParentClass() {
        return archetypeParentClass;
    }

    public boolean hasArchetypeParentClass() { return archetypeParentClass != null;}

    /**
     * Sets the name of a parent class used within the schema to provide archetype capability, enabling filtering of
     * classes in RM visualisation. If empty, 'Any' is assumed. Persisted attribute.
     *
     * @param archetypeParentClass
     */
    public void setArchetypeParentClass(String archetypeParentClass) {
        this.archetypeParentClass = archetypeParentClass;
    }

    /**
     * Returns the name of a parent class of logical 'data types' used within the schema to provide archetype capability, enabling
     * filtering of classes in RM visualisation. If empty, 'Any' is assumed. Persisted attribute.
     *
     * @return
     */
    public String getArchetypeDataValueParentClass() {
        return archetypeDataValueParentClass;
    }

    public boolean hasArchetypeDataValueParentClass() { return archetypeDataValueParentClass != null; }

    /**
     * Sets the name of a parent class of logical 'data types' used within the schema to provide archetype capability,
     * enabling filtering of classes in RM visualisation. If empty, 'Any' is assumed. Persisted attribute.
     *
     * @param archetypeDataValueParentClass
     */
    public void setArchetypeDataValueParentClass(String archetypeDataValueParentClass) {
        this.archetypeDataValueParentClass = archetypeDataValueParentClass;
    }

    /**
     * List of top-level package paths that provide the RM 'model' part in archetype identifiers, e.g. the path "org.openehr.ehr"
     * gives "EHR" in "openEHR-EHR". Within this namespace, archetypes can be based on any class reachable from classes defined directly in these packages.
     *
     * @return
     */
    public List<String> getArchetypeRmClosurePackages() {
        return archetypeRmClosurePackages;
    }

    /**
     * Returns the list of top-level package paths that provide the RM 'model' part in achetype identifiers, e.g. the path
     * "org.openehr.ehr" gives "EHR" in "openEHR-EHR". Within this namespace, archetypes can be based on any class reachable
     * from classes defined directly in these packages.
     *
     * @param rmClosurePackages
     */
    public void setArchetypeRmClosurePackages(List<String> rmClosurePackages) {
        this.archetypeRmClosurePackages = rmClosurePackages;
    }

    /**
     * Method adds a top-level package paths that provide the RM 'model' part in achetype identifiers, e.g. the path
     * "org.openehr.ehr" gives "EHR" in "openEHR-EHR". Within this namespace, archetypes can be based on any class reachable from classes defined directly in these packages.
     *
     * @param rmClosurePackage
     */
    public void addArchetypeRmClosurePackage(String rmClosurePackage) {
        archetypeRmClosurePackages.add(rmClosurePackage);
    }

    /**
     * `a_package_path' is a qualified package name, like 'org.openehr.ehr', 'org.openehr.demographic'
     *
     * @param rmClosurePackage
     * @return
     */
    public boolean hasArchetypeRmClosurePackage(String rmClosurePackage) {
        return archetypeRmClosurePackages.contains(rmClosurePackage);
    }

    /**
     * Method returns a class whose descendants should be made visible in tree and grid renderings of the archetype
     * definition, if archetype_parent_class is not set, designate . For openEHR and CEN this class is normally the
     * same as the archetype_parent_class, i.e. LOCATABLE and RECORD_COMPONENT respectively. It is typically set for CEN,
     * because archetype_parent_class may not be stated, due to demographic types not inheriting from it.
     *
     * @return
     */
    public String getArchetypeVisualizeDescendantsOf() {
        return archetypeVisualizeDescendantsOf;
    }

    /**
     * Method a class whose descendants should be made visible in tree and grid renderings of the archetype
     * definition, if archetype_parent_class is not set, designate . For openEHR and CEN this class is normally the
     * same as the archetype_parent_class, i.e. LOCATABLE and RECORD_COMPONENT respectively. It is typically set for CEN,
     * because archetype_parent_class may not be stated, due to demographic types not inheriting from it.
     *
     * @param archetypeVisualizeDescendantsOf
     */
    public void setArchetypeVisualizeDescendantsOf(String archetypeVisualizeDescendantsOf) {
        this.archetypeVisualizeDescendantsOf = archetypeVisualizeDescendantsOf;
    }

    /**
     * Returns the derived name of schema, based on model publisher, model name, model release.
     *
     * @return
     */
    public String getSchemaId() {
        return BmmDefinitions.createSchemaId(getRmPublisher(), getSchemaName(), getRmRelease());
    }

    @Override
    public String getModelName() {
        return modelName;
    }

    @Override
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
}
