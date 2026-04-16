package com.nedap.archie.flattener;

import com.nedap.archie.aom.Archetype;

import java.util.List;

/**
 * Created by pieter.bos on 21/10/15.
 */
public interface ArchetypeRepository {

    /**
     * Get Archetype based on archetype reference.
     * Warning: conversion from HRID to reference needs to be done by implementer for now! (so match v1.0.0 to v1 and vice versa)
     * TODO: implement some default methods that already do this and create a getlatestBySemanticIc and getByFullArchetypeHRID
     * @param archetypeId
     * @return
     */
    Archetype getArchetype(String archetypeId);

    List<Archetype> getAllArchetypes();

    /**
     * Return true if an only if the child archetype has parent as its parent somewhere in the tree
     * @param parent
     * @param child
     * @return
     */
    default boolean isChildOf(Archetype parent, Archetype child) {
        if(child.getArchetypeId().equals(parent.getArchetypeId()) || child.getArchetypeId().toString().equals(parent.getArchetypeId().getSemanticId())) {
            return true;
        }
        Archetype nextChild = child.getParentArchetypeId() == null ? null : getArchetype(child.getParentArchetypeId());
        if(nextChild != null) {
            return isChildOf(parent, nextChild);
        }
        return false;
    }
}
