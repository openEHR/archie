package com.nedap.archie.adlparser.treewalkers;

import com.nedap.archie.aom.terminology.ArchetypeTerminology;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A helper class for parsing component terminologies. Not to be used outside of the parser
 */
public class ComponentTerminologiesHelper {

    private Map<String, ArchetypeTerminology> componentTerminologies = new ConcurrentHashMap<>();

    public Map<String, ArchetypeTerminology> getComponentTerminologies() {
        return componentTerminologies;
    }

    public void setComponentTerminologies(Map<String, ArchetypeTerminology> componentTerminologies) {
        this.componentTerminologies = componentTerminologies;
    }
}
