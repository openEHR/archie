package org.openehr.bmm.v2.validation.converters;

import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.core.BmmPackage;
import org.openehr.bmm.v2.persistence.PBmmClass;
import org.openehr.bmm.v2.persistence.PBmmPackage;
import org.openehr.bmm.v2.persistence.PBmmSchema;
import org.openehr.bmm.v2.validation.BmmValidationResult;

import java.util.ArrayList;

public class BmmModelCreator {

    public BmmModel create(BmmValidationResult validationResult) {
        PBmmSchema schema = validationResult.getSchemaWithMergedIncludes();
        BmmModel model = new BmmModel();
        model.setRmPublisher (schema.getRmPublisher());
        model.setRmRelease (schema.getRmRelease());
        model.setModelName (schema.getModelName());
        model.setSchemaName (schema.getSchemaName());
        model.setSchemaRevision (schema.getSchemaRevision());
        model.setSchemaAuthor (schema.getSchemaAuthor());
        model.setSchemaDescription (schema.getSchemaDescription());
        model.setSchemaLifecycleState (schema.getSchemaLifecycleState());
        // cannot set the documentation - the supported P_BMM version has no documentation in the P_BMM_SCHEMA
        model.setSchemaContributors(schema.getSchemaContributors() == null ? new ArrayList<>() : new ArrayList<>(schema.getSchemaContributors()));

        // Add packages first
        for(PBmmPackage pBmmPackage:validationResult.getCanonicalPackages().values()) {
            BmmPackage bmmPackage = createBmmPackageDefinition (pBmmPackage, null, null);

            model.addPackage(bmmPackage);

            pBmmPackage.doRecursiveClasses((p, s) -> {
                PBmmClass pBmmClass = schema.getClassDefinition(s);
                if (pBmmClass != null) {
                    BmmClass bmmClass = pBmmClass.createBmmClass();
                    if (bmmClass != null) {
                        bmmClass.setPrimitiveType(schema.getPrimitiveTypes().containsKey (bmmClass.getName()));
                        model.addClassDefinition(bmmClass, bmmPackage);
                    }
                }
            });
        }

        model.setArchetypeParentClass (schema.getArchetypeParentClass());
        model.setArchetypeDataValueParentClass (schema.getArchetypeDataValueParentClass());
        model.setArchetypeVisualizeDescendantsOf (schema.getArchetypeVisualizeDescendantsOf());
        model.setArchetypeRmClosurePackages (schema.getArchetypeRmClosurePackages() == null ? new ArrayList<>() : new ArrayList<>(schema.getArchetypeRmClosurePackages()));


        // The basics have been created. Now populate the classes with properties
        //setup all classes, ancestors and generic parameters
        BmmClassProcessor classSupplier = new BmmClassProcessor(model, schema, (pBmmClass, processor) -> pBmmClass.populateBmmClass(processor, schema));
        classSupplier.run();
        //add all properties
        BmmClassProcessor propertySupplier = new BmmClassProcessor(model, schema, (pBmmClass, processor) -> pBmmClass.populateBmmClassProperties(processor));
        propertySupplier.run();

        return model;
    }


    private BmmPackage createBmmPackageDefinition(PBmmPackage p, PBmmPackage parent, BmmPackage parentPackageDefinition) {
        BmmPackage bmmPackageDefinition = new BmmPackage(p.getName());
        bmmPackageDefinition.setDocumentation(p.getDocumentation());
        if (parent != null) {
            bmmPackageDefinition.appendToPath(parent.getName());
            bmmPackageDefinition.setParent(parentPackageDefinition);
        }
        bmmPackageDefinition.appendToPath(p.getName());
        p.getPackages().values().forEach(childPackage -> {
            BmmPackage bmmChildPackageDefinition = createBmmPackageDefinition(childPackage, p, bmmPackageDefinition);
            bmmPackageDefinition.addPackage(bmmChildPackageDefinition);
        });
        return bmmPackageDefinition;
    }
}
