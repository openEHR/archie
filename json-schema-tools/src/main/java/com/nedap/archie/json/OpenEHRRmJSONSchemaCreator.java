package com.nedap.archie.json;

import java.util.ArrayList;
import java.util.HashSet;

public class OpenEHRRmJSONSchemaCreator extends JSONSchemaCreator {

    public OpenEHRRmJSONSchemaCreator() {
        super();

        ArrayList<String> rootTypes = new ArrayList<>();
        rootTypes.add("COMPOSITION");
        rootTypes.add("OBSERVATION");
        rootTypes.add("EVALUATION");
        rootTypes.add("ACTIVITY");
        rootTypes.add("ACTION");
        rootTypes.add("SECTION");
        rootTypes.add("INSTRUCTION");
        rootTypes.add("INSTRUCTION_DETAILS");
        rootTypes.add("ADMIN_ENTRY");
        rootTypes.add("CLUSTER");
        rootTypes.add("CAPABILITY");
        rootTypes.add("PERSON");
        rootTypes.add("ROLE");
        rootTypes.add("ORGANISATION");
        rootTypes.add("AGENT");
        rootTypes.add("GROUP");
        rootTypes.add("PARTY_IDENTITY");
        rootTypes.add("ITEM_TREE");
        rootTypes.add("CONTRIBUTION");
        rootTypes.add("EHR");
        rootTypes.add("EHR_STATUS");
        rootTypes.add("ORIGINAL_VERSION");
        rootTypes.add("IMPORTED_VERSION");
        rootTypes.add("HISTORY");
        rootTypes.add("ITEM_TABLE");
        rootTypes.add("ITEM_LIST");
        rootTypes.add("ITEM_TREE");
        rootTypes.add("ITEM_SINGLE");
        rootTypes.add("ITEM_TABLE");
        rootTypes.add("ELEMENT");
        setRootTypes(rootTypes);

        HashSet<String> ignoredAttributes = new HashSet<>();
        ignoredAttributes.add("DV_QUANTITY.property"); //this is for use in old archetypes
        ignoredAttributes.add("RESOURCE_DESCRIPTION.parent_resource"); //this is a runtime attribute, not serialized!
        super.setIgnoredAttributes(ignoredAttributes);

        HashSet<String> ignoredClasses = new HashSet<>();
        ignoredClasses.add("MULTIPLICITY_INTERVAL");
        ignoredClasses.add("CARDINALITY");
        super.setIgnoredClasses(ignoredClasses);
    }
}
