package com.nedap.archie.template.betterjson;

import com.nedap.archie.aom.Archetype;

public interface FlatArchetypeProvider {

    Archetype getFlatArchetype(String archetypeId);
}
