package com.nedap.archie.archetypevalidator;

/**
 * Configures which node identifier code system the archetype validator accepts.
 *
 * ADL 2 archetypes can use two node identifier code systems: the original 'id'-coded form (id1, id1.1, ...) and
 * the ADL 2.4 'at'-coded form (at0000, at0000.1, ...). An archetype must consistently use a single code system.
 *
 * <ul>
 *     <li>{@link #ID_CODED}: only accept id-coded archetypes. This is the default, for backwards compatibility.</li>
 *     <li>{@link #AT_CODED}: only accept at-coded archetypes.</li>
 *     <li>{@link #AUTO_DETECT}: detect the code system from the root node id and validate the rest of the
 *     archetype against that code system.</li>
 * </ul>
 */
public enum NodeIdCodeSystemValidation {
    ID_CODED,
    AT_CODED,
    AUTO_DETECT
}
