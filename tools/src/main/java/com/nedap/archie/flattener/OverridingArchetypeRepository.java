package com.nedap.archie.flattener;

import com.nedap.archie.aom.Archetype;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pieter.bos on 21/10/15.
 */
public class OverridingArchetypeRepository implements ArchetypeRepository {

    private ArchetypeRepository root;
    private SimpleArchetypeRepository overrides;

    public OverridingArchetypeRepository(ArchetypeRepository root) {
        this.root = root;
        overrides = new SimpleArchetypeRepository();
    }
    public void addExtraArchetype(Archetype archetype) {
        overrides.addArchetype(archetype);
    }

    @Override
    public Archetype getArchetype(String archetypeId) {
        Archetype result = overrides.getArchetype(archetypeId);
        if(result == null) {
            result = root.getArchetype(archetypeId);
        }
        return result;
    }

    @Override
    public List<Archetype> getAllArchetypes() {
        Map<String, Archetype> result = new LinkedHashMap<>();//only add archetype once if it has been overridde
        root.getAllArchetypes().forEach((a) -> result.put(a.getArchetypeId().toString(), a));
        overrides.getAllArchetypes().forEach((a) -> result.put(a.getArchetypeId().toString(), a));
        return new ArrayList<>(result.values());
    }

    public List<Archetype> getExtraArchetypes() {
        return overrides.getAllArchetypes();
    }
}
