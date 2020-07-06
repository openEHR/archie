package org.openehr.bmm.v2.validation.converters;

import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.v2.persistence.CaseInsensitiveLinkedHashMap;
import org.openehr.bmm.v2.persistence.PBmmClass;
import org.openehr.bmm.v2.persistence.PBmmPackage;
import org.openehr.bmm.v2.persistence.PBmmPackageContainer;
import org.openehr.bmm.v2.persistence.PBmmSchema;

import java.util.Map;

public class PreprocessPersistedSchema {
    //convert all maps to case insensitive variants
    public void preprocess(PBmmSchema schema) {
        {
            Map<String, PBmmClass<BmmClass>> classDefinitions = schema.getClassDefinitions();
            CaseInsensitiveLinkedHashMap<PBmmClass<BmmClass>> newClassDefinitions = new CaseInsensitiveLinkedHashMap<>();
            newClassDefinitions.putAll(classDefinitions);
            schema.setClassDefinitions(newClassDefinitions);
            for (PBmmClass clazz:classDefinitions.values()) {
                clazz.setSourceSchemaId(schema.getSchemaId());
            }
        }

        {
            Map<String, PBmmClass<BmmClass>> primitiveTypes = schema.getPrimitiveTypes();
            CaseInsensitiveLinkedHashMap<PBmmClass<BmmClass>> newPrimitiveTypes = new CaseInsensitiveLinkedHashMap<>();
            newPrimitiveTypes.putAll(primitiveTypes);
            schema.setPrimitiveTypes(newPrimitiveTypes);
            for (PBmmClass<BmmClass> clazz:primitiveTypes.values()) {
                clazz.setSourceSchemaId(schema.getSchemaId());
            }
        }
        {
            convertPackages(schema);

        }



    }

    private void convertPackages(PBmmPackageContainer schema) {
        Map<String, PBmmPackage> packages = schema.getPackages();
        CaseInsensitiveLinkedHashMap<PBmmPackage> newpackages = new CaseInsensitiveLinkedHashMap<>();
        newpackages.putAll(packages);
        for(PBmmPackage bmmPackage:newpackages.values()) {
            convertPackages(bmmPackage);
        }
        schema.setPackages(newpackages);
    }
}
