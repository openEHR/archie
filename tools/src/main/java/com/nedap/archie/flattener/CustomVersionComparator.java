package com.nedap.archie.flattener;

import com.github.zafarkhaja.semver.Version;
import com.nedap.archie.aom.ArchetypeHRID;

import java.util.Comparator;

/**
 * @author vera.prinsen
 * Created on 10/06/2020
 */
public class CustomVersionComparator implements Comparator<Version> {

    Comparator versionComparator = Comparator.comparingInt(Version::getMajorVersion)
            .thenComparingInt(Version::getMinorVersion)
            .thenComparingInt(Version::getPatchVersion)
            .thenComparing(
                    (v) -> v.getPreReleaseVersion().isEmpty() ? null : v.getPreReleaseVersion(),
                    Comparator.nullsLast(String::compareTo))
            .thenComparingInt(v -> v.getBuildMetadata().equals("") ? 0 : Integer.parseInt(v.getBuildMetadata()));

    @Override
    public int compare(Version v1, Version v2) {
        return versionComparator.compare(v1, v2);
    }

 }
