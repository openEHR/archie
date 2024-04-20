package org.openehr.referencemodels;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.nedap.archie.openehr.rminfo.OpenEhrRmInfoLookup;
import org.junit.Test;
import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.v2.validation.BmmRepository;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RMComparedWithBmmTest {

    @Test
    public void compareBmmWithRM() {

        Map<String, String> typeMap =  new HashMap<>();
        typeMap.put("Any", "Object");
        typeMap.put("Integer64", "Long");
        typeMap.put("Integer", "Long");
        typeMap.put("REAL", "Double");
        typeMap.put("HASH", "Map");
        typeMap.put("Character", "char");
        typeMap.put("Octet", "Byte"); //TODO: fix this properly
        typeMap.put("PROPORTION_KIND", "Long");//TODO: replace with enum!

        Set<String> extraParams = Sets.newHashSet("parent", "path");

        Map<String, String> typeNamesOverride = new HashMap<>();
        typeNamesOverride.put("DV_URI.value", "URI");
        typeNamesOverride.put("DV_EHR_URI.value", "URI");
        typeNamesOverride.put("DV_DATE.value", "temporal");
        typeNamesOverride.put("DV_TIME.value", "temporal_accessor");
        typeNamesOverride.put("DV_DATE_TIME.value", "temporal_accessor");
        typeNamesOverride.put("DV_DURATION.value", "temporal_amount");
        typeNamesOverride.put("Interval.lower", "object");
        typeNamesOverride.put("Interval.upper", "object");

        BmmRepository bmmRepository = AllMetaModelsInitialiser.getBmmRepository();
        BmmModel model = bmmRepository.getModel("openehr_rm_1.1.0").getModel();

        List<ModelDifference> compared = new BmmComparison(extraParams, typeMap, typeNamesOverride).compare(model, OpenEhrRmInfoLookup.getInstance());

        compared.sort(Comparator.comparing((a) -> a.getClassName() + "." + a.getType().toString()));
        compared = compared.stream().filter((diff) -> {
            BmmClass classDefinition = model.getClassDefinition(diff.getClassName());
            return classDefinition == null ? true : !classDefinition.getSourceSchemaId().equalsIgnoreCase("openehr_rm_ehr_extract_1.1.0");
        }).collect(Collectors.toList());

        Set<ModelDifference> knownDifferences = new HashSet<>();


        //Needs a backwards incompatible fix, not changing now (and not important, new API is included)
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "ARCHETYPE_ID", "namespace"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "ARCHETYPE_ID", "rm_entity"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "ARCHETYPE_ID", "rm_originator"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "ARCHETYPE_ID", "qualified_rm_entity"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "ARCHETYPE_ID", "specialisation"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "ARCHETYPE_ID", "version_id"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "ARCHETYPE_ID", "rm_name"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "ARCHETYPE_ID", "domain_concept"));
        //AOM class. Differences are not important right now
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "AUTHORED_RESOURCE", "uid"));
        //two ancestors in BMM for all date types. Not going to happen in java unless interface, and cannot check automatically
        knownDifferences.add(new ModelDifference(ModelDifferenceType.ANCESTOR_DIFFERENCE, "", "DV_DATE", null));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.ANCESTOR_DIFFERENCE, "", "DV_DATE_TIME", null));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.ANCESTOR_DIFFERENCE, "", "DV_DURATION", null));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.ANCESTOR_DIFFERENCE, "", "DV_TIME", null));
        //two ancestors for ordered as well
        knownDifferences.add(new ModelDifference(ModelDifferenceType.ANCESTOR_DIFFERENCE, "", "DV_ORDERED", null));
        //same for interval
        knownDifferences.add(new ModelDifference(ModelDifferenceType.ANCESTOR_DIFFERENCE, "", "DV_INTERVAL", null));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "DV_INTERVAL", "interval"));

        //TODO: not sure, looks the same to me
        knownDifferences.add(new ModelDifference(ModelDifferenceType.EXISTENCE_DIFFERENCE, "", "EHR", "contributions"));
        //not important
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_MODEL, "", "EHR", "most_recent_composition"));

        //unused enum, not important
        knownDifferences.add(new ModelDifference(ModelDifferenceType.CLASS_MISSING_IN_MODEL, "", "PROPORTION_KIND", null));
        //included in base, not used in EHR,only AOM, so not included here until we need it
        knownDifferences.add(new ModelDifference(ModelDifferenceType.CLASS_MISSING_IN_MODEL, "", "RESOURCE_DESCRIPTION", null));
        //included in base, not used in EHR,only AOM, so not included here until we need it
        knownDifferences.add(new ModelDifference(ModelDifferenceType.CLASS_MISSING_IN_MODEL, "", "RESOURCE_DESCRIPTION_ITEM", null));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.CLASS_MISSING_IN_MODEL, "", "RESOURCE_ANNOTATIONS", null));

        //Archie specific implementation of base class for all objects
        knownDifferences.add(new ModelDifference(ModelDifferenceType.CLASS_MISSING_IN_BMM, "", "RMOBJECT", null));
        //unused enum, not important
        knownDifferences.add(new ModelDifference(ModelDifferenceType.CLASS_MISSING_IN_MODEL, "", "VALIDITY_KIND", null));
        //RM implementation at least in old spec, not sure what happened to it, but not getting rid of it!
        knownDifferences.add(new ModelDifference(ModelDifferenceType.CLASS_MISSING_IN_BMM, "", "VERSIONED_COMPOSITION", null));
        //RM implementation at least in old spec, not sure what happened to it, but not getting rid of it!
        knownDifferences.add(new ModelDifference(ModelDifferenceType.CLASS_MISSING_IN_BMM, "", "VERSIONED_EHR_ACCESS", null));
        //RM implementation at least in old spec, not sure what happened to it, but not getting rid of it!
        knownDifferences.add(new ModelDifference(ModelDifferenceType.CLASS_MISSING_IN_BMM, "", "VERSIONED_EHR_STATUS", null));
        //RM implementation at least in old spec, not sure what happened to it, but not getting rid of it!
        knownDifferences.add(new ModelDifference(ModelDifferenceType.CLASS_MISSING_IN_BMM, "", "VERSIONED_FOLDER", null));

        //BMM changed VERSION_STATUS to an enum. For now this remains a string until some further major release
        knownDifferences.add(new ModelDifference(ModelDifferenceType.CLASS_MISSING_IN_MODEL, "",  "VERSION_STATUS", null));

        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "DV_ABSOLUTE_QUANTITY", "magnitude"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "DV_AMOUNT", "magnitude"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "DV_DATE", "magnitude"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "DV_DATE_TIME", "magnitude"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "DV_DURATION", "magnitude"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.TYPE_NAME_DIFFERENCE, "", "DV_INTERVAL", "upper"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.TYPE_NAME_DIFFERENCE, "", "DV_INTERVAL", "lower"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "DV_PROPORTION", "magnitude"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "DV_QUANTIFIED", "magnitude"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "DV_TEMPORAL", "magnitude"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "DV_TIME", "magnitude"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "HIER_OBJECT_ID", "extension"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "HIER_OBJECT_ID", "root"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "IMPORTED_VERSION", "is_branch"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "IMPORTED_VERSION", "uid"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "IMPORTED_VERSION", "lifecycle_state"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "IMPORTED_VERSION", "data"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "IMPORTED_VERSION", "preceding_version_uid"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "IMPORTED_VERSION", "canonical_form"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "OBJECT_VERSION_ID", "extension"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "OBJECT_VERSION_ID", "root"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "OBJECT_VERSION_ID", "object_id"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "OBJECT_VERSION_ID", "version_tree_id"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "OBJECT_VERSION_ID", "creating_system_id"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "OBJECT_VERSION_ID", "is_branch"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "ORIGINAL_VERSION", "is_branch"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "ORIGINAL_VERSION", "canonical_form"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "UID_BASED_ID", "extension"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "UID_BASED_ID", "root"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "VERSION", "is_branch"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "VERSION", "lifecycle_state"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "VERSION", "uid"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "VERSION", "data"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "VERSION", "preceding_version_uid"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "VERSION", "canonical_form"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "VERSION_TREE_ID", "trunk_version"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "VERSION_TREE_ID", "branch_number"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "VERSION_TREE_ID", "branch_version"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "VERSION_TREE_ID", "is_branch"));
        //The bmm files from the standard, are missing the next two fields, although they are specified in the relevant specification
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "TERMINOLOGY_ID", "name"));
        knownDifferences.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM, "", "TERMINOLOGY_ID", "version_id"));
        //System.out.println(Joiner.on("\n").join(compared));
        List<ModelDifference> foundErrors = new ArrayList<>();

        for(ModelDifference difference:compared) {
            if(!knownDifferences.contains(difference)) {
                foundErrors.add(difference);
            }
        }

        List<ModelDifference> noLongerFoundErrors = new ArrayList<>();

        for(ModelDifference difference:knownDifferences) {
            if(!compared.contains(difference)) {
                noLongerFoundErrors.add(difference);
            }
        }
        assertTrue("unexpected model differences: "+ Joiner.on("\n").join(foundErrors), foundErrors.isEmpty());

        assertTrue("difference was in known difference, but is actually not a problem anymore: "+ Joiner.on("\n").join(noLongerFoundErrors), noLongerFoundErrors.isEmpty());
        assertEquals(knownDifferences.size(), compared.size());
    }


    /**
     * Generate and return the code to add all modelDifferences to the knownDifferences array. useful if you have new ones
     * and want to quickly add them without writing code :)
     * @param modelDifferences
     */
    private String getCodeForModelDifferences(List<ModelDifference> modelDifferences) {
        StringBuilder stringBuilder = new StringBuilder();
        for(ModelDifference difference:modelDifferences) {
            stringBuilder.append(MessageFormat.format("knownDifferences.add(new ModelDifference(ModelDifferenceType.{0}, \"\", \"{1}\", {2}));",
                    difference.getType().toString(),
                    difference.getClassName(),
                    difference.getPropertyName() == null ? "null" : '"' + difference.getPropertyName() + '"'));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
