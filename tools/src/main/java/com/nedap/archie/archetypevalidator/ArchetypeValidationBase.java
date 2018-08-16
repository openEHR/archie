package com.nedap.archie.archetypevalidator;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.flattener.ArchetypeRepository;
import com.nedap.archie.rminfo.MetaModels;
import com.nedap.archie.rminfo.ModelInfoLookup;

import java.util.ArrayList;
import java.util.List;

public abstract class ArchetypeValidationBase implements ArchetypeValidation {

    protected Archetype archetype;
    protected Archetype flatParent;
    protected ArchetypeRepository repository;
    protected List<ArchetypeValidationMessage> messages;
    protected ModelInfoLookup lookup;
    protected MetaModels combinedModels;
    protected ArchetypeValidationSettings settings;

    public ArchetypeValidationBase() {
    }

    @Override
    public List<ArchetypeValidationMessage> validate(MetaModels models, Archetype archetype, Archetype flatParent, ArchetypeRepository repository, ArchetypeValidationSettings settings) {
        this.archetype = archetype;
        this.flatParent = flatParent;
        this.repository = repository;
        this.lookup = models.getSelectedModelInfoLookup();
        this.combinedModels = models;
        this.settings = settings;

        messages = new ArrayList<>();
        validate();
        return messages;
    }

    public abstract void validate();

    public void addMessage(ErrorType errorType) {
        messages.add(new ArchetypeValidationMessage(errorType));
    }

    public void addMessageWithPath(ErrorType errorType, String path) {
        messages.add(new ArchetypeValidationMessage(errorType, path));
    }

    public void addMessageWithPath(ErrorType errorType, String path, String customMessage) {
        messages.add(new ArchetypeValidationMessage(errorType, path, customMessage));
    }

    public void addMessage(ErrorType errorType, String customMessage) {
        messages.add(new ArchetypeValidationMessage(errorType, null, customMessage));
    }

    public void addWarning(ErrorType errorType) {
        ArchetypeValidationMessage message = new ArchetypeValidationMessage(errorType);
        message.setWarning(true);
        messages.add(message);

    }

    public void addWarning(ErrorType errorType, String customMessage) {
        ArchetypeValidationMessage message = new ArchetypeValidationMessage(errorType, null, customMessage);
        message.setWarning(true);
        messages.add(message);
    }

    public void addWarningWithPath(ErrorType errorType, String location) {
        ArchetypeValidationMessage message = new ArchetypeValidationMessage(errorType, location);
        message.setWarning(true);
        messages.add(message);
    }

    public void addWarningWithPath(ErrorType errorType, String location, String customMessage) {
        ArchetypeValidationMessage message = new ArchetypeValidationMessage(errorType, location, customMessage);
        message.setWarning(true);
        messages.add(message);
    }

    public boolean hasPassed() {
        return messages.isEmpty();
    }

    public ModelInfoLookup getLookup() {
        return lookup;
    }

    public Archetype getArchetype() {
        return archetype;
    }

    public Archetype getFlatParent() {
        return flatParent;
    }

    public ArchetypeRepository getRepository() {
        return repository;
    }

    public List<ArchetypeValidationMessage> getMessages() {
        return messages;
    }

    public ArchetypeValidationSettings getSettings() {
        return settings;
    }
}
