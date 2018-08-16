package com.nedap.archie.flattener;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeHRID;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.archetypevalidator.ArchetypeValidationResult;
import com.nedap.archie.archetypevalidator.ArchetypeValidationSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryFullArchetypeRepository extends SimpleArchetypeRepository implements FullArchetypeRepository, MutableArchetypeRepository {

    private Map<String, ArchetypeValidationResult> validationResult = new ConcurrentHashMap<>();

    private Map<String, Archetype> flattenedArchetypes = new ConcurrentHashMap<>();
    private Map<String, Archetype> operationalTemplates = new ConcurrentHashMap<>();
    private ArchetypeValidationSettings archetypeValidationSettings;

    @Override
    public Archetype getFlattenedArchetype(String archetypeId) {
        return flattenedArchetypes.get(new ArchetypeHRID(archetypeId).getSemanticId());
    }

    @Override
    public ArchetypeValidationResult getValidationResult(String archetypeId) {
        return validationResult.get(new ArchetypeHRID(archetypeId).getSemanticId());
    }

    @Override
    public void setValidationResult(ArchetypeValidationResult result) {
        validationResult.put(new ArchetypeHRID(result.getArchetypeId()).getSemanticId(), result);
    }

    @Override
    public void setFlattenedArchetype(Archetype archetype) {
        flattenedArchetypes.put(archetype.getArchetypeId().getSemanticId(), archetype);
    }

    @Override
    public void setOperationalTemplate(OperationalTemplate template) {
        operationalTemplates.put(template.getArchetypeId().getSemanticId(), template);
    }

    @Override
    public void removeValidationResult(String archetypeId) {
        operationalTemplates.remove(new ArchetypeHRID(archetypeId).getSemanticId());
        validationResult.remove(new ArchetypeHRID(archetypeId).getSemanticId());
    }

    @Override
    public List<ArchetypeValidationResult> getAllValidationResults() {
        return new ArrayList<>(validationResult.values());
    }

    @Override
    public ArchetypeValidationSettings getArchetypeValidationSettings() {
        return archetypeValidationSettings;
    }

    public void setArchetypeValidationSettings(ArchetypeValidationSettings settings) {
        this.archetypeValidationSettings = settings;
    }
}
