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

    private BmmClassCreator classCreator = new BmmClassCreator();

    public BmmModel create(BmmValidationResult validationResult) {
        PBmmSchema schema = validationResult.getSchemaWithMergedIncludes();
        BmmModel model = new BmmModel();
        model.setRmPublisher(schema.getRmPublisher());
        model.setRmRelease(schema.getRmRelease());
        model.setModelName(schema.getModelName());
        model.setSchemaName(schema.getSchemaName());
        model.setSchemaRevision(schema.getSchemaRevision());
        model.setSchemaAuthor(schema.getSchemaAuthor());
        model.setSchemaDescription(schema.getSchemaDescription());
        model.setSchemaLifecycleState(schema.getSchemaLifecycleState());
        //cannot set the documentation - the supported P_BMM version has no documentation in the P_BMM_SCHEMA
        model.setSchemaContributors(schema.getSchemaContributors() == null ? new ArrayList() : new ArrayList<>(schema.getSchemaContributors()));



        //Add packages first
        for(PBmmPackage pBmmPackage:validationResult.getCanonicalPackages().values()) {
            BmmPackage bmmPackage = createBmmPackageDefinition(pBmmPackage, null, null);

            model.addPackage(bmmPackage);

            pBmmPackage.doRecursiveClasses((p, s) -> {
                PBmmClass persistedBmmClass = schema.findClassOrPrimitiveDefinition(s);
                if (persistedBmmClass != null) {
                    BmmClass bmmClass = classCreator.createBmmClass(persistedBmmClass);

                    if (bmmClass != null && bmmPackage != null) {
                        if (schema.getPrimitiveTypes().get(bmmClass.getName()) != null)
                            bmmClass.setPrimitiveType(true);
                        if (persistedBmmClass.isOverride() != null && persistedBmmClass.isOverride())
                            bmmClass.setOverride(true);
                        model.addClassDefinition(bmmClass, bmmPackage);
                    }
                }
            });
        }

        model.setArchetypeParentClass(schema.getArchetypeParentClass());
        model.setArchetypeDataValueParentClass(schema.getArchetypeDataValueParentClass());
        model.setArchetypeVisualizeDescendantsOf(schema.getArchetypeVisualizeDescendantsOf());
        model.setArchetypeRmClosurePackages(schema.getArchetypeRmClosurePackages() == null ? new ArrayList<>() : new ArrayList<>(schema.getArchetypeRmClosurePackages()));


        // The basics have been created. Now populate the classes with properties
        ProcessClassesInOrder processClassesInOrder = new ProcessClassesInOrder();
        processClassesInOrder.doAllClassesInOrder(schema, bmmClass -> classCreator.populateBmmClass(bmmClass, model), new ArrayList<>(schema.getPrimitiveTypes().values()));
        processClassesInOrder.doAllClassesInOrder(schema, bmmClass -> classCreator.populateBmmClass(bmmClass, model), new ArrayList<>(schema.getClassDefinitions().values()));

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
