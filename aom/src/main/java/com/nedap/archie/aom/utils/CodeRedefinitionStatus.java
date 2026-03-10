package com.nedap.archie.aom.utils;

/**
 * A simple redefinition status concept, that can be derived just from the node id/value/valueset code
 * <p>
 * Not the full redefinition status as defined in the specifications: <a href="https://specifications.openehr.org/releases/AM/latest/ADL2.html#_redefinition_concepts">ADL2 - Redefinition Concepts</a>
 */
public enum CodeRedefinitionStatus {
    ADDED, REDEFINED, INHERITED, UNDEFINED
}
