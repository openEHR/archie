package org.openehr.bmm.v2.validation.converters;

import org.openehr.bmm.persistence.validation.BmmDefinitions;
import org.openehr.bmm.v2.persistence.PBmmPackage;
import org.openehr.bmm.v2.persistence.PBmmSchema;

import java.util.Map;
import java.util.TreeMap;

public class CanonicalPackagesGenerator {

    public Map<String, PBmmPackage> generateCanonicalPackages(PBmmSchema schema) {
        Map<String, PBmmPackage> canonicalPackages = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        PBmmPackage childPackage = null;
        String childPackageKey;
        Map<String, PBmmPackage> packageContainer;
        //top-level package canonicalisation: the result is that in each P_BMM_SCHEMA, the
        //attribute `canonical_packages' contains the mergeable structure
        for (PBmmPackage topPackage : schema.getPackages().values()) {
            //Iterate over qualified name, inserting new packages for each of these names.
            //E.g. 'rm.composition.content' causes three new packages 'rm', 'composition'
            // and 'content' to be created and linked, with the 'rm' one being put in
            // `canonical_packages'
            if (topPackage.getName().indexOf(BmmDefinitions.PACKAGE_NAME_DELIMITER) >= 0) {
                packageContainer = canonicalPackages;
                String[] packagePathComponents = topPackage.getName().split("\\.");
                for (String packagePathComponent:packagePathComponents) {
                    childPackageKey = packagePathComponent.toUpperCase();
                    if (packageContainer.containsKey(childPackageKey)) {
                        childPackage = packageContainer.get(childPackageKey);
                    }
                    else {
                        childPackage = new PBmmPackage(packagePathComponent);
                        packageContainer.put(childPackageKey, childPackage);
                    }
                    packageContainer = childPackage.getPackages();
                }
                //make this package with `packages' and `classes' references to those parts of `other_pkg'
                //but keeping its own name.
                childPackage.setClassesAndPackagesFrom(topPackage);
            } else {
                //just create a reference in the canonical packages; note that this precludes
                //the situation where top-level packages like 'rm' and 'rm.composition.content'
                //co-exist - this would be bad structure
                canonicalPackages.put(topPackage.getName().toUpperCase(), topPackage);
            }
        }

        return canonicalPackages;
    }
}
