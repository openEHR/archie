package com.nedap.archie.flattener;

import com.github.zafarkhaja.semver.Version;
import com.nedap.archie.aom.ArchetypeHRID;
import com.nedap.archie.definitions.VersionStatus;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ArchetypeHRIDMap<T> extends ConcurrentHashMap<ArchetypeHRID,T> {

    public T getLatestVersion(String archetypeId) throws IllegalArgumentException {
        return getLatestVersion(new ArchetypeHRID(archetypeId));
    }

    public T getLatestVersion(ArchetypeHRID archetypeHRID){
        //Early return for fully defined BUILD version
        if (archetypeHRID.getVersionStatus() == VersionStatus.BUILD) {
            return this.get(archetypeHRID);
        }

        //Filter if version is not fully defined
        List<ArchetypeHRID> keys = this.keySet().stream().
                filter(id -> id.getIdUpToConcept().equals(archetypeHRID.getIdUpToConcept())).
                filter(id -> (archetypeHRID.getMajorVersion() == null) || id.getMajorVersion().equals(archetypeHRID.getMajorVersion())).
                filter(id -> (archetypeHRID.getMinorVersion() == null) || id.getMinorVersion().equals(archetypeHRID.getMinorVersion())).
                filter(id -> (archetypeHRID.getPatchVersion() == null) || id.getPatchVersion().equals(archetypeHRID.getPatchVersion())).
                collect(Collectors.toList());

        //Sort in ascending order
        keys.sort(Comparator.comparing(o -> Version.valueOf(o.getVersionId()), new CustomVersionComparator()));

        //Return latest version
        return (keys.size() == 0) ? null : this.get(keys.get(keys.size() - 1));

    }

}
