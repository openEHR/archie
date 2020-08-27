package com.nedap.archie.flattener;

import com.nedap.archie.aom.*;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.query.ComplexObjectProxyReplacement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Creates operational templates. Not to be used externally, use the Flattener with the right parameters to
 * create operational templates
 */
class OperationalTemplateCreator {

    private final Flattener flattener;

    OperationalTemplateCreator(Flattener flattener) {
        this.flattener = flattener;
    }

    public static OperationalTemplate createOperationalTemplate(Archetype archetype) {
        Archetype clone = archetype.clone(); //clone so we do not overwrite the parent archetype. never
        OperationalTemplate result = new OperationalTemplate();
        result.setArchetypeId((ArchetypeHRID) archetype.getArchetypeId().clone());
        result.setDefinition(clone.getDefinition());
        result.setDifferential(false);

        result.setRmRelease(clone.getRmRelease());
        result.setAdlVersion(clone.getAdlVersion());
        result.setTerminology(clone.getTerminology());
        result.setGenerated(true);
        result.setOtherMetaData(clone.getOtherMetaData());
        result.setRules(clone.getRules());
        result.setBuildUid(clone.getBuildUid());
        result.setDescription(clone.getDescription());
        result.setOriginalLanguage(clone.getOriginalLanguage());
        result.setTranslations(clone.getTranslations());
        result.setAnnotations(clone.getAnnotations());

        return result;
    }

    public static void overrideArchetypeId(Archetype result, Archetype override) {
        result.setArchetypeId(override.getArchetypeId());
        result.setParentArchetypeId(override.getParentArchetypeId());
    }

    public void fillSlots(OperationalTemplate archetype) { //should this be OperationalTemplate?
        //TODO: closing archetype slots should be moved to AFTER including other archetypes
        closeArchetypeSlots(archetype);
        fillArchetypeRoots(archetype);
        fillComplexObjectProxies(archetype);
    }

    /** Zero occurrences and existence constraint processing when creating OPT templates. Removes attributes */
    public void removeZeroOccurrencesConstraints(Archetype archetype) {
        Stack<CObject> workList = new Stack<>();
        workList.push(archetype.getDefinition());
        while (!workList.isEmpty()) {
            CObject object = workList.pop();
            List<CAttribute> attributesToRemove = new ArrayList<>();
            for (CAttribute attribute : object.getAttributes()) {
                if (attribute.getExistence() != null && attribute.getExistence().getUpper() == 0 && !attribute.getExistence().isUpperUnbounded()) {
                    attributesToRemove.add(attribute);
                } else {
                    List<CObject> objectsToRemove = new ArrayList<>();
                    for (CObject child : attribute.getChildren()) {
                        if (!child.isAllowed()) {
                            objectsToRemove.add(child);
                        }
                        workList.push(child);
                    }
                    attribute.getChildren().removeAll(objectsToRemove);
                }

            }
            object.getAttributes().removeAll(attributesToRemove);
        }
    }

    /** Zero occurrences and existence constraint processing when creating OPT templates. Removes attributes */
    public void fillEmptyOccurrences(Archetype archetype) {
        Stack<CObject> workList = new Stack<>();
        workList.push(archetype.getDefinition());
        while (!workList.isEmpty()) {
            CObject object = workList.pop();
            if( (object instanceof CComplexObject || object instanceof ArchetypeSlot || object instanceof CComplexObjectProxy)
                    && object.getOccurrences() == null) {
                object.setOccurrences(object.effectiveOccurrences(flattener.getMetaModels()::referenceModelPropMultiplicity));
            }
            for (CAttribute attribute : object.getAttributes()) {

                for (CObject child : attribute.getChildren()) {
                    workList.push(child);
                }
            }
        }
    }


    private void closeArchetypeSlots(OperationalTemplate archetype) {
        if(!getConfig().isCloseArchetypeSlots()) {
            return;
        }
        Stack<CObject> workList = new Stack<>();
        workList.push(archetype.getDefinition());
        while(!workList.isEmpty()) {
            CObject object = workList.pop();
            for(CAttribute attribute:object.getAttributes()) {
                List<CObject> toRemove = new ArrayList<>();
                for(CObject child:attribute.getChildren()) {
                    if(child instanceof ArchetypeSlot) { //use_archetype
                        if(((ArchetypeSlot) child).isClosed()) {
                            toRemove.add(child);
                        }
                    }
                    workList.push(child);
                }
                attribute.getChildren().removeAll(toRemove);
            }
        }
    }

    private void fillArchetypeRoots(OperationalTemplate result) {
        if(!getConfig().isFillArchetypeRoots()) {
            return;
        }
        Stack<CObject> workList = new Stack<>();
        workList.push(result.getDefinition());
        while(!workList.isEmpty()) {
            CObject object = workList.pop();
            for(CAttribute attribute:object.getAttributes()) {
                for(CObject child:attribute.getChildren()) {
                    if(child instanceof CArchetypeRoot) { //use_archetype
                        fillArchetypeRoot((CArchetypeRoot) child, result);
                    }
                    workList.push(child);
                }
            }
        }
    }

    private void fillComplexObjectProxies(OperationalTemplate result) {
        if(!getConfig().isReplaceUseNode()) {
            return;
        }
        Stack<CObject> workList = new Stack<>();
        workList.push(result.getDefinition());
        List<ComplexObjectProxyReplacement> replacements = new ArrayList<>();
        while(!workList.isEmpty()) {
            CObject object = workList.pop();
            for(CAttribute attribute:object.getAttributes()) {
                for(CObject child:attribute.getChildren()) {
                    if(child instanceof CComplexObjectProxy) { //use_node
                        ComplexObjectProxyReplacement possibleReplacement =
                                ComplexObjectProxyReplacement.getComplexObjectProxyReplacement((CComplexObjectProxy) child);
                        if(possibleReplacement != null) {
                            replacements.add(possibleReplacement);
                        } else {
                            throw new RuntimeException("cannot find target in CComplexObjectProxy");
                        }

                    }
                    workList.push(child);
                }
            }
        }
        for(ComplexObjectProxyReplacement replacement:replacements) {
            replacement.replace();
        }
    }

    private void fillArchetypeRoot(CArchetypeRoot root, OperationalTemplate result) {
        if(flattener.getCreateOperationalTemplate()) {
            String archetypeRef = root.getArchetypeRef();
            String newArchetypeRef = archetypeRef;
            Archetype archetype = flattener.getRepository().getArchetype(archetypeRef);
            if(archetype instanceof TemplateOverlay){
                //we want to be able to check which archetype this is in the UI. If it's an overlay, that means retrieving the non-operational template
                //which is a hassle.
                //That's a problem. Is this the way to fix is?
                newArchetypeRef = archetype.getParentArchetypeId();
            }
            if (archetype == null) {
                throw new IllegalArgumentException("Archetype with reference :" + archetypeRef + " not found.");
            }

            archetype = flattener.getNewFlattener().flatten(archetype);

            //
            CComplexObject rootToFill = root;
            if(flattener.isUseComplexObjectForArchetypeSlotReplacement()) {
                rootToFill = archetype.getDefinition();
                root.getParent().replaceChild(root.getNodeId(), rootToFill);
            } else {
                rootToFill.setAttributes(archetype.getDefinition().getAttributes());
                rootToFill.setAttributeTuples(archetype.getDefinition().getAttributeTuples());
                rootToFill.setDefaultValue(archetype.getDefinition().getDefaultValue());
            }
            String newNodeId = archetype.getArchetypeId().getFullId();

            ArchetypeTerminology terminology = archetype.getTerminology();

            //The node id will be replaced from "id1" to something like "openEHR-EHR-COMPOSITION.template_overlay.v1.0.0
            //so store it in the terminology as well
            Map<String, Map<String, ArchetypeTerm>> termDefinitions = terminology.getTermDefinitions();

            for(String language: termDefinitions.keySet()) {
                Map<String, ArchetypeTerm> translations = termDefinitions.get(language);
                translations.put(newNodeId, TerminologyFlattener.getTerm(terminology.getTermDefinitions(), language, archetype.getDefinition().getNodeId()));
            }

            //rootToFill.setNodeId(newNodeId);
            if(!flattener.isUseComplexObjectForArchetypeSlotReplacement()) {
                root.setArchetypeRef(newNodeId);
            }

            //todo: should we filter this?
            if(archetype instanceof OperationalTemplate) {
                OperationalTemplate template = (OperationalTemplate) archetype;
                //add all the component terminologies, otherwise we lose translation
                for(String subarchetypeId:template.getComponentTerminologies().keySet()) {
                    result.addComponentTerminology(subarchetypeId, template.getComponentTerminologies().get(subarchetypeId));
                }
            }

            result.addComponentTerminology(newNodeId, terminology);

            String prefix = archetype.getArchetypeId().getConceptId() + "_";
            flattener.getRulesFlattener().combineRules(archetype, root.getArchetype(), prefix, prefix, rootToFill.getPath(), false);
            flattener.getAnnotationsFlattener().addAnnotationsWithPathPrefix(rootToFill.getPath(), archetype, result);
            //todo: do we have to put something in the terminology extracts?
            //templateResult.addTerminologyExtract(child.getNodeId(), archetype.getTerminology().);
        }

    }

    private FlattenerConfiguration getConfig() {
        return flattener.getConfiguration();
    }




}
