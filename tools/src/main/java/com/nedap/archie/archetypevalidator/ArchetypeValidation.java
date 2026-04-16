package com.nedap.archie.archetypevalidator;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.flattener.FullArchetypeRepository;
import com.nedap.archie.rminfo.MetaModel;
import com.nedap.archie.rminfo.MetaModels;

import java.util.List;
import java.util.Objects;

/**
 * Created by pieter.bos on 31/03/2017.
 */
public interface ArchetypeValidation {

    /**
     * @deprecated Use {@link #validate(MetaModel, Archetype, Archetype, FullArchetypeRepository, ArchetypeValidationSettings)} instead.
     */
    @Deprecated
    default List<ValidationMessage> validate(MetaModels models, Archetype archetype, Archetype flatParent, FullArchetypeRepository repository, ArchetypeValidationSettings settings) {
        return validate(Objects.requireNonNull(models.getSelectedModel(), "No MetaModel selected"), archetype, flatParent, repository, settings);
    }

    List<ValidationMessage> validate(MetaModel metaModel, Archetype archetype, Archetype flatParent, FullArchetypeRepository repository, ArchetypeValidationSettings settings);

}
