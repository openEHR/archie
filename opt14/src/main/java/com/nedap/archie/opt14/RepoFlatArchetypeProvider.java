package com.nedap.archie.opt14;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.flattener.ArchetypeHRIDMap;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.rminfo.MetaModels;
import org.openehr.referencemodels.BuiltinReferenceModels;

class RepoFlatArchetypeProvider implements FlatArchetypeProvider {

    private InMemoryFullArchetypeRepository repo;
    private ArchetypeHRIDMap<Archetype> flatArchetypes = new ArchetypeHRIDMap<>();
    private MetaModels metaModels = BuiltinReferenceModels.getMetaModels();

    public RepoFlatArchetypeProvider(InMemoryFullArchetypeRepository repo) {
        this.repo = repo;
    }

    public Archetype getFlatArchetype(String id) {
        Archetype flattenedArchetype = repo.getFlattenedArchetype(id);
        if(flattenedArchetype != null) {
            return flattenedArchetype;
        }
        flattenedArchetype = flatArchetypes.get(id);
        if(flattenedArchetype != null) {
            return flattenedArchetype;
        }
        Archetype archetype = repo.getArchetype(id);
        if(archetype == null) {
            return null;
        }
        try {
            flattenedArchetype = new Flattener(repo, metaModels).flatten(archetype);
            flatArchetypes.put(flattenedArchetype.getArchetypeId(), flattenedArchetype);
            return flattenedArchetype;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
