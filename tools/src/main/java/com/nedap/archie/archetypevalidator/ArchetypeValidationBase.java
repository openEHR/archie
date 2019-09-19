package com.nedap.archie.archetypevalidator;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.flattener.ArchetypeRepository;
import com.nedap.archie.flattener.FullArchetypeRepository;
import com.nedap.archie.rminfo.MetaModels;
import com.nedap.archie.rminfo.ModelInfoLookup;

import java.util.ArrayList;
import java.util.List;

public abstract class ArchetypeValidationBase implements ArchetypeValidation {

    protected Archetype archetype;
    protected Archetype flatParent;
    protected FullArchetypeRepository repository;
    protected List<ValidationMessage> messages;
    protected ModelInfoLookup lookup;
    protected MetaModels combinedModels;
    protected ArchetypeValidationSettings settings;

    public ArchetypeValidationBase() {
    }

    @Override
    public List<ValidationMessage> validate(MetaModels models, Archetype archetype, Archetype flatParent, FullArchetypeRepository repository, ArchetypeValidationSettings settings) {
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
        messages.add(new ValidationMessage(errorType));
    }

    public void addMessageWithPath(ErrorType errorType, String path) {
        messages.add(new ValidationMessage(errorType, path));
    }

    public void addMessageWithPath(ErrorType errorType, String path, String customMessage) {
        messages.add(new ValidationMessage(errorType, path, customMessage));
    }

    public void addMessage(ErrorType errorType, String customMessage) {
        messages.add(new ValidationMessage(errorType, null, customMessage));
    }

    public void addWarning(ErrorType errorType) {
        ValidationMessage message = new ValidationMessage(errorType);
        message.setWarning(true);
        messages.add(message);

    }

    public void addWarning(ErrorType errorType, String customMessage) {
        ValidationMessage message = new ValidationMessage(errorType, null, customMessage);
        message.setWarning(true);
        messages.add(message);
    }

    public void addWarningWithPath(ErrorType errorType, String location) {
        ValidationMessage message = new ValidationMessage(errorType, location);
        message.setWarning(true);
        messages.add(message);
    }

    public void addWarningWithPath(ErrorType errorType, String location, String customMessage) {
        ValidationMessage message = new ValidationMessage(errorType, location, customMessage);
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

    public List<ValidationMessage> getMessages() {
        return messages;
    }

    public ArchetypeValidationSettings getSettings() {
        return settings;
    }
}
