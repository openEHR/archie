package com.nedap.archie.archetypevalidator;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.flattener.ArchetypeRepository;
import com.nedap.archie.rminfo.ModelInfoLookup;

import java.util.List;

/**
 * Created by pieter.bos on 31/03/2017.
 */
public interface ArchetypeValidation {

    public List<ValidationMessage> validate(MetaModel models, Archetype archetype, Archetype flatParent, ArchetypeRepository repository);

}
