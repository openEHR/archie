package com.nedap.archie.json;

import org.openehr.bmm.core.BmmClass;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A Json Schema URI provider that splits the json schema into one file per class
 * to generate the official JSON Schema that is to be used in the ITS-JSON specification
 * Way too many for internal validation use!
 */
public class ItsJsonUriProvider implements JsonSchemaUriProvider {

    Map<String, String> names = new LinkedHashMap<>();
    Map<String, String> schemas = new LinkedHashMap<>();

    private String baseUri;
    private String mainFileName;

    public ItsJsonUriProvider(String baseUri, String mainFileName) {
        this.baseUri = baseUri;
        this.mainFileName = mainFileName;
        names.put("org.openehr.rm.support.assumed_types", "Base_types");
        names.put("org.openehr.base.foundation_types", "Foundation_types");
        names.put("org.openehr.base.base_types.resource", "Resource");
        names.put("org.openehr.base.base_types", "Base_types");
        names.put("org.openehr.base", "Base_types");
        names.put("org.openehr.rm.common", "Common");
        names.put("org.openehr.rm.data_structures", "Data_structures");
        names.put("org.openehr.rm.composition", "Composition");
        names.put("org.openehr.rm.data_types", "Data_types");
        names.put("org.openehr.rm.demographic", "Demographic");
        names.put("org.openehr.rm.ehr_extract", "Ehr_extract");
        names.put("org.openehr.rm.composition.content.integration", "Integration");
        names.put("org.openehr.rm.support", "Support");

        names.put("org.openehr.rm.ehr", "Ehr");

        schemas.put("openehr_base_1.0.4", "BASE/Release-1.0.4");
        schemas.put("openehr_base_1.1.0", "BASE/Release-1.1.0");
        schemas.put("openehr_base_1.2.0", "BASE/Release-1.2.0");

        schemas.put("openehr_ehr_1.0.3", "RM/Release-1.0.3");
        schemas.put("openehr_structures_1.0.3", "RM/Release-1.0.3");
        schemas.put("openehr_ehr_extract_1.0.3", "RM/Release-1.0.3");
        schemas.put("openehr_data_types_1.0.3", "RM/Release-1.0.3");
        schemas.put("openehr_demographic_1.0.3", "RM/Release-1.0.3");
        schemas.put("openehr_basic_types_1.0.3", "RM/Release-1.0.3");
        schemas.put("openehr_primitive_types_1.0.3", "RM/Release-1.0.3");

        schemas.put("openehr_rm_ehr_1.0.4", "RM/Release-1.0.4");
        schemas.put("openehr_rm_structures_1.0.4", "RM/Release-1.0.4");
        schemas.put("openehr_rm_ehr_extract_1.0.4", "RM/Release-1.0.4");
        schemas.put("openehr_rm_data_types_1.0.4", "RM/Release-1.0.4");
        schemas.put("openehr_rm_demographic_1.0.4", "RM/Release-1.0.4");

        schemas.put("openehr_rm_ehr_1.1.0", "RM/Release-1.1.0");
        schemas.put("openehr_rm_structures_1.1.0", "RM/Release-1.1.0");
        schemas.put("openehr_rm_ehr_extract_1.1.0", "RM/Release-1.1.0");
        schemas.put("openehr_rm_data_types_1.1.0", "RM/Release-1.1.0");
        schemas.put("openehr_rm_demographic_1.1.0", "RM/Release-1.1.0");
    }



    public String getName(BmmClass bmmClass) {
        String fullPath = bmmClass.getPackage().getFullPath();
        String schemaId = bmmClass.getSourceSchemaId();
        String prefix = schemas.get(schemaId);
        if(prefix == null) {
            prefix = "";
        } else {
            prefix = prefix + "/";
        }

        for(String name:names.keySet()) {
            if(fullPath.contains(name)) {
                return prefix + names.get(name) + "/";
            }
        }
        return prefix + "/" + fullPath;
    }

    public JsonSchemaUri provideJsonSchemaUrl(BmmClass bmmClass) {
//        return new JsonSchemaUri(baseUri, bmmClass.getPackagePath() + ".json");
//        System.out.println(bmmClass.getPackage().getFullPath());
//        System.out.println(bmmClass.getName());
//        System.out.println(bmmClass.getBmmModel().getSchemaName());
//        System.out.println(bmmClass.getSourceSchemaId());
//        System.out.println(getName(bmmClass) + bmmClass.getName() + ".json");

        return new JsonSchemaUri(baseUri, getName(bmmClass) + bmmClass.getName() + ".json");
    }

    public JsonSchemaUri provideMainFileUri() {
        return new JsonSchemaUri(baseUri, mainFileName);
    }
}

