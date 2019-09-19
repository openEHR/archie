package com.nedap.archie.archetypevalidator;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.flattener.FullArchetypeRepository;
import com.nedap.archie.rminfo.MetaModels;

import java.util.List;

/**
 * Created by pieter.bos on 31/03/2017.
 */
public interface ArchetypeValidation {

    List<ValidationMessage> validate(MetaModels models, Archetype archetype, Archetype flatParent, FullArchetypeRepository repository, ArchetypeValidationSettings settings);

}
