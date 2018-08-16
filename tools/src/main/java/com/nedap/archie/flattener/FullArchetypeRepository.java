package com.nedap.archie.flattener;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.archetypevalidator.ArchetypeValidationResult;
import com.nedap.archie.archetypevalidator.ArchetypeValidationSettings;
import com.nedap.archie.archetypevalidator.ArchetypeValidator;
import com.nedap.archie.rminfo.MetaModels;
import com.nedap.archie.rminfo.ReferenceModels;
import org.openehr.bmm.rmaccess.ReferenceModelAccess;

import java.util.List;

public interface FullArchetypeRepository extends ArchetypeRepository {

    Archetype getFlattenedArchetype(String archetypeId);

    ArchetypeValidationResult getValidationResult(String archetypeId);

    void setValidationResult(ArchetypeValidationResult result);

    void setFlattenedArchetype(Archetype archetype);

    void setOperationalTemplate(OperationalTemplate template);

    /**
     * Removes the validation result and the operational template of the given archetype id. Keeps the archetype
     *
     * @param archetypeId
     */
    void removeValidationResult(String archetypeId);

    List<ArchetypeValidationResult> getAllValidationResults();

    ArchetypeValidationSettings getArchetypeValidationSettings();

    default void compile(ReferenceModels models) {
        ArchetypeValidator validator = new ArchetypeValidator(models);
        compile(validator);
    }

    default void compile(MetaModels models) {
        ArchetypeValidator validator = new ArchetypeValidator(models);
        compile(validator);
    }

    /**
     * validate the validation result if necessary, and return either the newly validated one or
     * the existing validation result
     * @param models
     * @return
     */
    default ArchetypeValidationResult compileAndRetrieveValidationResult(String archetypeId, MetaModels models) {
        ArchetypeValidationResult archetypeValidationResult = getValidationResult(archetypeId);
        if(archetypeValidationResult != null) {
            return archetypeValidationResult;
        }
        Archetype archetype = getArchetype(archetypeId);
        if(archetype == null) {
            return null;
        }
        ArchetypeValidator validator = new ArchetypeValidator(models);
        return validator.validate(archetype, this);
    }

    default void compile(ReferenceModels models, ReferenceModelAccess bmmModels) {
        ArchetypeValidator validator = new ArchetypeValidator(models, bmmModels);
        compile(validator);
    }

    default void compile(ArchetypeValidator validator) {
        for(Archetype archetype:getAllArchetypes()) {
            if(getValidationResult(archetype.getArchetypeId().toString()) == null) {
                validator.validate(archetype, this);
            }
        }
    }
}
