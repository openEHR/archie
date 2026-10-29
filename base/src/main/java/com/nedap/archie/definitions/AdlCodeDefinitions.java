package com.nedap.archie.definitions;

public class AdlCodeDefinitions {
    /**
     * String leader of ‘identifier’ codes, i.e. codes used to identify id-coded archetype nodes.
     */
    public static final String ID_CODE_LEADER = "id";

    /**
     * String leader of ‘identifier’ codes, i.e. codes used to identify at-coded archetype nodes.
     */
    public static final String AT_CODE_LEADER = "at";

    /**
     * String leader of ‘value’ codes, i.e. codes used to identify codes values, including value set members.
     */
    public static final String VALUE_CODE_LEADER = "at";

    /**
     * String leader of ‘value set’ codes, i.e. codes used to identify value sets.
     */
    public static final String VALUE_SET_CODE_LEADER = "ac";

    /**
     * Character used to separate numeric parts of codes belonging to different specialisation levels.
     */
    public static final char SPECIALIZATION_SEPARATOR = '.';

    /**
     * Regex used to define the legal numeric part of any archetype code. Corresponds to the simple pattern of dotted numbers, as used in typical multi-level numbering schemes.
     */
    public static final String CODE_REGEX_PATTERN = "(0|[1-9][0-9]*)(\\.(0|[1-9][0-9]*))*";

    /**
     * Regex pattern of the root code of any archetype: id1, id1.1, at0000, at0000.1 etc.
     */
    public static final String ROOT_CODE_REGEX_PATTERN = "^(id1|at0000)(\\.1)*$";

    /**
     * Regex pattern of the root code of an id-coded archetype: id1, id1.1, id1.1.1 etc.
     */
    public static final String ID_CODED_ROOT_CODE_REGEX_PATTERN = "^id1(\\.1)*$";

    /**
     * Regex pattern of the root code of an at-coded archetype: at0000, at0000.1, at0000.1.1 etc.
     */
    public static final String AT_CODED_ROOT_CODE_REGEX_PATTERN = "^at0000(\\.1)*$";

    /**
     * Code id used for C_PRIMITIVE_OBJECT nodes on creation.
     */
    public static final String PRIMITIVE_NODE_ID = "id9999";

    /**
     * Code id for C_PRIMITIVE_OBJECT nodes in at-coded archetypes.
     */
    public static final String AT_CODED_PRIMITIVE_NODE_ID = "at9999";

    /**
     * True if the code is a primitive node id: id9999 or at9999.
     */
    public static boolean isPrimitiveNodeId(String code) {
        return PRIMITIVE_NODE_ID.equals(code) || AT_CODED_PRIMITIVE_NODE_ID.equals(code);
    }

}
