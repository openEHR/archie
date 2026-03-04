package com.nedap.archie.aom.utils;

/**
 * A simple redefinition status concept, that can be derived just from the node id/value/valueset code
 * <p>
 * Not the full redefinition status as defined in <a href="https://www.openehr.org/releases/AM/latest/docs/ADL2/ADL2.html#_redefinition_concepts">...</a>
 */
public enum CodeRedefinitionStatus {
    ADDED, REDEFINED, INHERITED, UNDEFINED
}
