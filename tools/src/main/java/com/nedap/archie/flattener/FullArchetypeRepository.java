package com.nedap.archie.flattener;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.archetypevalidator.ArchetypeValidationSettings;
import com.nedap.archie.archetypevalidator.ArchetypeValidator;
import com.nedap.archie.archetypevalidator.ValidationResult;
import com.nedap.archie.rminfo.MetaModels;
import com.nedap.archie.rminfo.ReferenceModels;

import java.util.List;

public interface FullArchetypeRepository extends ArchetypeRepository, OperationalTemplateProvider {

    Archetype getFlattenedArchetype(String archetypeId);

    ValidationResult getValidationResult(String archetypeId);

    OperationalTemplate getOperationalTemplate(String archetypeId);

    void setValidationResult(ValidationResult result);

    void setFlattenedArchetype(Archetype archetype);

    void setOperationalTemplate(OperationalTemplate template);

    /**
     * Removes the validation result and the operational template of the given archetype id. Keeps the archetype
     *
     * @param archetypeId
     */
    void removeValidationResult(String archetypeId);

    List<ValidationResult> getAllValidationResults();

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
    default ValidationResult compileAndRetrieveValidationResult(String archetypeId, MetaModels models) {
        ValidationResult validationResult = getValidationResult(archetypeId);
        if(validationResult != null) {
            return validationResult;
        }
        Archetype archetype = getArchetype(archetypeId);
        if(archetype == null) {
            return null;
        }
        ArchetypeValidator validator = new ArchetypeValidator(models);
        return validator.validate(archetype, this);
    }

    default void compile(ArchetypeValidator validator) {
        for(Archetype archetype:getAllArchetypes()) {
            if(getValidationResult(archetype.getArchetypeId().toString()) == null) {
                validator.validate(archetype, this);
            }
        }
    }
}
