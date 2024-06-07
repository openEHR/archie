package com.nedap.archie.archetypevalidator.validations;

import com.nedap.archie.aom.ArchetypeModelObject;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.archetypevalidator.ErrorType;
import com.nedap.archie.archetypevalidator.ValidatingVisitor;
import com.nedap.archie.apath.PathSegment;
import com.nedap.archie.apath.APathQuery;
import org.openehr.utils.message.I18n;

import java.util.List;

public class DefinitionStructureValidation extends ValidatingVisitor {


    public DefinitionStructureValidation() {
        super();
    }

    protected void validate(CAttribute cAttribute) {

        if (cAttribute.getDifferentialPath() != null) {
            if (archetype.getParentArchetypeId() == null) {
                //differential paths can only occur in specialized archetypes, so not in this one
                addMessageWithPath(ErrorType.VDIFV, cAttribute.path(), I18n.t("A differential path was used in an attribute, but this is not allowed in an archetype that has no parent"));
            } else if (repository != null) {
                if (flatParent != null) {
                    //adl workbench deviates from spec by only allowing differential paths at root, we allow them everywhere, according to spec

                    ArchetypeModelObject parentAOMObject = flatParent.itemAtPath(AOMUtils.pathAtSpecializationLevel(
                            cAttribute.getParent().getPathSegments(), flatParent.specializationDepth()));
                    ArchetypeModelObject differentialPathInParent = null;

                    List<PathSegment> pathSegments = new APathQuery(cAttribute.getDifferentialPath()).getPathSegments();

                    if(parentAOMObject == null || !(parentAOMObject instanceof CComplexObject)) {
                        addPathNotFoundInParentError(cAttribute);
                    } else {
                        CComplexObject parentObject = (CComplexObject) parentAOMObject;


                        differentialPathInParent = parentObject.itemAtPath(
                                //TODO: the ADL workbench does this, so /items[id9.1]/value is a valid differential path even in openEHR-EHR-CLUSTER.exam-uterine_cervix.v1.0.0. Should it be?
                                AOMUtils.pathAtSpecializationLevel(
                                        pathSegments,
                                        flatParent.specializationDepth()
                                )
                        );

                        if (differentialPathInParent == null) {
                            //not found in parent, but the terminal node in the path is allowed to be an unarchetyped constraint, apparently
                            String pathMinuLastNode = AOMUtils.pathAtSpecializationLevel(pathSegments.subList(0, pathSegments.size() - 1), flatParent.specializationDepth());
                            CObject parent = parentObject.itemAtPath(pathMinuLastNode);
                            if (parent == null || parent.isRoot()) {
                                addPathNotFoundInParentError(cAttribute);
                            } else {
                                PathSegment terminalNode = pathSegments.get(pathSegments.size() - 1);
                                if (!combinedModels.attributeExists(parent.getRmTypeName(), terminalNode.getNodeName())) {
                                    addPathNotFoundInParentError(cAttribute);
                                }
                            }
                        } else if (!(differentialPathInParent instanceof CAttribute)) {
                            addMessageWithPath(ErrorType.VDIFP, cAttribute.getDifferentialPath(),
                                    I18n.t("Differential path must point to a C_ATTRIBUTE in the flat parent, but it pointed instead to a {0}", differentialPathInParent.getClass()));
                        }
                    }
                }
            }
        }
    }

    private void addPathNotFoundInParentError(CAttribute cAttribute) {
        addMessageWithPath(ErrorType.VDIFP, cAttribute.getPath(), I18n.t("Differential path {0} was not found in the parent archetype", cAttribute.getDifferentialPath()));
    }
}
