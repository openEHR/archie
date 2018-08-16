package com.nedap.archie.archetypevalidator.validations;

import com.nedap.archie.aom.ArchetypeModelObject;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.archetypevalidator.ErrorType;
import com.nedap.archie.archetypevalidator.ArchetypeValidatingVisitor;
import com.nedap.archie.paths.PathSegment;
import com.nedap.archie.query.APathQuery;

import java.util.List;

public class DefinitionStructureValidation extends ArchetypeValidatingVisitor {


    public DefinitionStructureValidation() {
        super();
    }

    protected void validate(CAttribute cAttribute) {

        if (cAttribute.getDifferentialPath() != null) {
            if (archetype.getParentArchetypeId() == null) {
                //differential paths can only occur in specialized archetypes, so not in this one
                addMessage(ErrorType.VDIFV, cAttribute.path());
            } else if (repository != null) {
                if (flatParent != null) {
                    //adl workbench deviates from spec by only allowing differential paths at root, we allow them everywhere, according to spec

                    ArchetypeModelObject parentAOMObject = flatParent.itemAtPath(AOMUtils.pathAtSpecializationLevel(
                            cAttribute.getParent().getPathSegments(), flatParent.specializationDepth()));
                    ArchetypeModelObject differentialPathInParent = null;

                    List<PathSegment> pathSegments = new APathQuery(cAttribute.getDifferentialPath()).getPathSegments();

                    if(parentAOMObject == null || !(parentAOMObject instanceof CComplexObject)) {
                        addPathNotFoundInParentError(cAttribute);
                    }
                    CComplexObject parentObject = (CComplexObject) parentAOMObject;


                    differentialPathInParent = parentObject.itemAtPath(
                            //TODO: the ADL workbench does this, so /items[id9.1]/value is a valid differential path even in openEHR-EHR-CLUSTER.exam-uterine_cervix.v1.0.0. Should it be?
                            AOMUtils.pathAtSpecializationLevel(
                                    pathSegments,
                                    flatParent.specializationDepth()
                            )
                    );

                    if(differentialPathInParent == null) {
                        //not found in parent, but the terminal node in the path is allowed to be an unarchetyped constraint, apparently
                        String pathMinuLastNode = AOMUtils.pathAtSpecializationLevel(pathSegments.subList(0, pathSegments.size()-1), flatParent.specializationDepth());
                        CObject parent = parentObject.itemAtPath(pathMinuLastNode);
                        if(parent == null || parent.isRoot()) {
                            addPathNotFoundInParentError(cAttribute);
                        } else {
                            PathSegment terminalNode = pathSegments.get(pathSegments.size() - 1);
                            if (!combinedModels.attributeExists(parent.getRmTypeName(), terminalNode.getNodeName())) {
                                addPathNotFoundInParentError(cAttribute);
                            }
                        }
                    } else if (!(differentialPathInParent instanceof CAttribute)) {
                        addMessageWithPath(ErrorType.VDIFP, cAttribute.getDifferentialPath(), "differential path must point to an attribute in the flat parent, but it pointed instead to a " + differentialPathInParent.getClass());
                    }
                }
            }
        }
    }

    private void addPathNotFoundInParentError(CAttribute cAttribute) {
        addMessageWithPath(ErrorType.VDIFP, cAttribute.getDifferentialPath());
    }
}
