package com.nedap.archie.flattener;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeHRID;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.archetypevalidator.ArchetypeValidationSettings;
import com.nedap.archie.archetypevalidator.ValidationResult;

import java.util.ArrayList;
import java.util.List;

public class InMemoryFullArchetypeRepository extends SimpleArchetypeRepository implements FullArchetypeRepository, MutableArchetypeRepository {

    private ArchetypeHRIDMap<ValidationResult> validationResult = new ArchetypeHRIDMap<>();

    private ArchetypeHRIDMap<Archetype> flattenedArchetypes = new ArchetypeHRIDMap<>();
    private ArchetypeHRIDMap<OperationalTemplate> operationalTemplates = new ArchetypeHRIDMap<>();
    private ArchetypeValidationSettings archetypeValidationSettings;

    @Override
    public Archetype getFlattenedArchetype(String archetypeId) {
        return flattenedArchetypes.getLatestVersion(archetypeId);
    }

    @Override
    public ValidationResult getValidationResult(String archetypeId) {
        return validationResult.getLatestVersion(archetypeId);
    }

    @Override
    public OperationalTemplate getOperationalTemplate(String archetypeId) {
        return operationalTemplates.getLatestVersion(archetypeId);
    }

    @Override
    public void setValidationResult(ValidationResult result) {
        if(result.getFlattened() != null) {
            setFlattenedArchetype(result.getFlattened());
        }
        validationResult.put(new ArchetypeHRID(result.getArchetypeId()), result);
    }

    @Override
    public void setFlattenedArchetype(Archetype archetype) {
        flattenedArchetypes.put(archetype.getArchetypeId(), archetype);
    }

    @Override
    public void setOperationalTemplate(OperationalTemplate template) {
        operationalTemplates.put(template.getArchetypeId(), template);
    }

    @Override
    public void removeValidationResult(String archetypeId) {
        operationalTemplates.remove(new ArchetypeHRID(archetypeId));
        validationResult.remove(new ArchetypeHRID(archetypeId));
    }

    @Override
    public List<ValidationResult> getAllValidationResults() {
        return new ArrayList<>(validationResult.values());
    }

    @Override
    public ArchetypeValidationSettings getArchetypeValidationSettings() {
        return archetypeValidationSettings;
    }

    public void setArchetypeValidationSettings(ArchetypeValidationSettings settings) {
        this.archetypeValidationSettings = settings;
    }

    /**
     * Removes the archetype, the flattened archetype, the operational template and the validationresult
     * corresponding to the given archetype id from this repository.
     * Does not invalidate any dependencies on this archetype. This is for now the responsibility of the caller,
     * but this might be included here in a later version as a method with a different name.
     * @param archetypeId
     */
    @Override
    public void removeArchetype(String archetypeId) {
        super.removeArchetype(archetypeId);
        this.flattenedArchetypes.remove(archetypeId);
        this.operationalTemplates.remove(archetypeId);
        this.validationResult.remove(archetypeId);
    }

}
