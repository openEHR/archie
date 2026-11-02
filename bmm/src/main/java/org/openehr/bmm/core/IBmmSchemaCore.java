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

import java.util.List;

/**
 * Core properties of BMM_SCHEMA.
 *
 * Created by cnanjo on 4/11/16.
 */
public interface IBmmSchemaCore {

    /**
     * Returns the publisher of model expressed in the schema.
     *
     * @return
     */
    String getRmPublisher();

    /**
     * Sets the publisher of model expressed in the schema.
     *
     * @param rmPublisher
     */
    void setRmPublisher(String rmPublisher);

    /**
     * Returns the release of model expressed in the schema as a 3-part numeric, e.g. "3.1.0" .
     *
     * @return
     */
    String getRmRelease();

    /**
     * Sets the release of model expressed in the schema as a 3-part numeric, e.g. "3.1.0" .
     *
     * @param rmRelease
     */
    void setRmRelease(String rmRelease);

    /**
     * Returns the name of model expressed in schema; a 'schema' usually contains all of the packages of one 'model'
     * of a publisher. A publisher with more than one model can have multiple schemas. Persisted attribute.
     *
     * @return
     */
    String getSchemaName();

    /**
     * Sets the name of model expressed in schema; a 'schema' usually contains all of the packages of one 'model'
     * of a publisher. A publisher with more than one model can have multiple schemas. Persisted attribute.
     *
     * @param schemaName
     */
    void setSchemaName(String schemaName);

    /**
     * Returns the revision of schema.
     *
     * @return
     */
    String getSchemaRevision();

    /**
     * Sets the revision of schema.
     *
     * @param schemaRevision
     */
    void setSchemaRevision(String schemaRevision);

    /**
     * Returns the lifecycle state of the schema.
     *
     * @return
     */
    String getSchemaLifecycleState();

    /**
     * Sets the lifecycle state of the schema.
     *
     * @param schemaLifecycleState
     */
    void setSchemaLifecycleState(String schemaLifecycleState);

    /**
     * Returns the primary author of schema.
     *
     * @return
     */
    String getSchemaAuthor();
    /**
     * Sets the primary author of schema.
     *
     * @param schemaAuthor
     */
    void setSchemaAuthor(String schemaAuthor);

    /**
     * Returns the description of schema.
     *
     * @return
     */
    String getSchemaDescription();

    /**
     * Sets the description of schema.
     *
     * @param schemaDescription
     */
    void setSchemaDescription(String schemaDescription);

    /**
     * Returns the contributing authors of schema.
     *
     * @return
     */
    List<String> getSchemaContributors();

    /**
     * Sets the contributing authors of schema.
     *
     * @param schemaContributors
     */
    void setSchemaContributors(List<String> schemaContributors);

    /**
     * Returns the name of a parent class used within the schema to provide archetype capability, enabling filtering of
     * classes in RM visualisation. If empty, 'Any' is assumed. Persisted attribute.
     *
     * @return
     */
    String getArchetypeParentClass();

    /**
     * Sets the name of a parent class used within the schema to provide archetype capability, enabling filtering of
     * classes in RM visualisation. If empty, 'Any' is assumed. Persisted attribute.
     *
     * @param archetypeParentClass
     */
    void setArchetypeParentClass(String archetypeParentClass);

    /**
     * Returns the name of a parent class of logical 'data types' used within the schema to provide archetype capability, enabling
     * filtering of classes in RM visualisation. If empty, 'Any' is assumed. Persisted attribute.
     *
     * @return
     */
    String getArchetypeDataValueParentClass();

    /**
     * Sets the name of a parent class of logical 'data types' used within the schema to provide archetype capability,
     * enabling filtering of classes in RM visualisation. If empty, 'Any' is assumed. Persisted attribute.
     *
     * @param archetypeDataValueParentClass
     */
    void setArchetypeDataValueParentClass(String archetypeDataValueParentClass);

    /**
     * List of top-level package paths that provide the RM 'model' part in archetype identifiers, e.g. the path "org.openehr.ehr"
     * gives "EHR" in "openEHR-EHR". Within this namespace, archetypes can be based on any class reachable from classes defined directly in these packages.
     *
     * @return
     */
    List<String> getArchetypeRmClosurePackages();

    /**
     * Returns the list of top-level package paths that provide the RM 'model' part in achetype identifiers, e.g. the path
     * "org.openehr.ehr" gives "EHR" in "openEHR-EHR". Within this namespace, archetypes can be based on any class reachable
     * from classes defined directly in these packages.
     *
     * @param rmClosurePackages
     */
    void setArchetypeRmClosurePackages(List<String> rmClosurePackages);

    /**
     * Method adds a top-level package paths that provide the RM 'model' part in achetype identifiers, e.g. the path
     * "org.openehr.ehr" gives "EHR" in "openEHR-EHR". Within this namespace, archetypes can be based on any class reachable from classes defined directly in these packages.
     *
     * @param rmClosurePackage
     */
    void addArchetypeRmClosurePackage(String rmClosurePackage);

    /**
     * Method returns a class whose descendants should be made visible in tree and grid renderings of the archetype
     * definition, if archetype_parent_class is not set, designate . For openEHR and CEN this class is normally the
     * same as the archetype_parent_class, i.e. LOCATABLE and RECORD_COMPONENT respectively. It is typically set for CEN,
     * because archetype_parent_class may not be stated, due to demographic types not inheriting from it.
     *
     * @return
     */
    String getArchetypeVisualizeDescendantsOf();

    /**
     * Method a class whose descendants should be made visible in tree and grid renderings of the archetype
     * definition, if archetype_parent_class is not set, designate . For openEHR and CEN this class is normally the
     * same as the archetype_parent_class, i.e. LOCATABLE and RECORD_COMPONENT respectively. It is typically set for CEN,
     * because archetype_parent_class may not be stated, due to demographic types not inheriting from it.
     *
     * @param archetypeVisualizeDescendantsOf
     */
    void setArchetypeVisualizeDescendantsOf(String archetypeVisualizeDescendantsOf);

    /**
     * Returns the derived name of schema, based on model publisher, model name, model release.
     *
     * @return
     */
    String getSchemaId();

    /**
     * Returns the name of this model, if this schema is a model root point. Not set for sub-schemas that are not considered models on their own.
     */
    String getModelName();

    /** Sets the Name of this model, if this schema is a model root point. Not set for sub-schemas that are not considered models on their own. */
    void setModelName(String modelName);
}
