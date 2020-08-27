package com.nedap.archie.json.flat;

/**
 * the index notation to use for a flat json format
 */
public enum IndexNotation {
    /** Write numeric indexes in the flat json format between [], and if node id already present, as [idx, 4] */
    BRACKETED,
    /** Write numeric indexes after a ':', after attribute name or after ], as in [idx]:4*/
    AFTER_A_COLON;
}
