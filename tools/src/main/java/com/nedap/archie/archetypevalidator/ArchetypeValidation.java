package com.nedap.archie.archetypevalidator;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.flattener.FullArchetypeRepository;
import com.nedap.archie.rminfo.MetaModel;

import java.util.List;

/**
 * Created by pieter.bos on 31/03/2017.
 */
public interface ArchetypeValidation {

    List<ValidationMessage> validate(MetaModel metaModel, Archetype archetype, Archetype flatParent, FullArchetypeRepository repository, ArchetypeValidationSettings settings);

}
