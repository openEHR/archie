package com.nedap.archie.template.betterjson;

import com.nedap.archie.adlparser.modelconstraints.ReflectionConstraintImposer;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.Template;
import com.nedap.archie.aom.TemplateOverlay;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.archetypevalidator.ErrorType;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.rminfo.MetaModel;
import com.nedap.archie.rminfo.MetaModels;
import org.openehr.utils.message.I18n;

public class MultiplicityFixer {

    private MetaModels combinedModels;

    public void fixMultiplicity(MetaModels metaModels, Archetype archetype, FlatArchetypeProvider repo) {

        combinedModels = metaModels;
        metaModels.selectModel(archetype);

        fixMultiplicity(archetype,repo, archetype.getDefinition());


        if(archetype instanceof Template) {
            Template template = (Template) archetype;
            for(TemplateOverlay overlay:template.getTemplateOverlays()) {
                fixMultiplicity(overlay, repo, overlay.getDefinition());
            }
        }
    }

    private void fixMultiplicity(Archetype archetype, FlatArchetypeProvider repo, CObject cObject) {
        for(CAttribute attribute:cObject.getAttributes()) {
            fixMultiplicity(archetype, repo, attribute);
        }
    }

    private void fixMultiplicity(Archetype archetype, FlatArchetypeProvider repo, CAttribute cAttribute ) {
        for(CObject cObject:cAttribute.getChildren()) {
            fixMultiplicity(archetype, repo, cObject);
        }
        Archetype flatParent = archetype.getParentArchetypeId() == null ?
                null :
                repo.getFlatArchetype(archetype.getParentArchetypeId());
        if(flatParent == null && cAttribute.getDifferentialPath() != null) {
            return;
        }
        CObject owningObject = cAttribute.getParent();
        if(cAttribute.getDifferentialPath() != null) {
            CAttribute differentialPathFromParent = (CAttribute) AOMUtils.getDifferentialPathFromParent(flatParent, cAttribute);
            owningObject =  differentialPathFromParent == null ? null : differentialPathFromParent.getParent();
        }
        if(owningObject != null) {
            if (!combinedModels.attributeExists(owningObject.getRmTypeName(), cAttribute.getRmAttributeName())) {
                //This is an error that cannot be fixed
            } else {
                CAttribute defaultAttribute = new ReflectionConstraintImposer(combinedModels).getDefaultAttribute(owningObject.getRmTypeName(), cAttribute.getRmAttributeName());
                if(defaultAttribute != null) {
                    if(cAttribute.getExistence() != null) {
                        if(!defaultAttribute.getExistence().contains(cAttribute.getExistence())) {
                            if(!archetype.isSpecialized() && defaultAttribute.getExistence().equals(cAttribute.getExistence())) {
                                //does not pass strict validation, but nobody cares, will be deleted in default remover anyway
                            } else {
                                //this is an actual error. It can only be 0..0 or 1..1 in parent in this case, so remove the existence here
                                cAttribute.setExistence(null);
                            }
                        }
                    }
                    if(defaultAttribute.isMultiple()) {
                        if(defaultAttribute.getCardinality() != null && cAttribute.getCardinality() != null && cAttribute.getCardinality().getInterval() != null && !defaultAttribute.getCardinality().contains(cAttribute.getCardinality())){
                            if(defaultAttribute.getCardinality().equals(cAttribute.getCardinality())) {
                                //no-one cares about this, will be removed
                            } else {
                                ///TODO: cardinality problem. Fixme! not a problem so far ...
                            }
                        }
                    } else {
                        if(cAttribute.getCardinality() != null) {
                            //TODO: fix?
                        }
                    }
                }
            }

        }
    }
}
