package com.nedap.archie.flattener;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.*;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.aom.utils.ArchetypeParsePostProcesser;
import com.nedap.archie.base.MultiplicityInterval;
import com.nedap.archie.paths.PathSegment;
import com.nedap.archie.paths.PathUtil;
import com.nedap.archie.query.AOMPathQuery;
import com.nedap.archie.query.APathQuery;
import com.nedap.archie.query.ComplexObjectProxyReplacement;
import com.nedap.archie.rminfo.MetaModels;
import com.nedap.archie.rminfo.ReferenceModels;
import com.nedap.archie.rules.Assertion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Flattener. For single use only, create a new flattener for every flatten-action you want to do!
 *
 * TODO: the parent/child naming is very confusing. Make a new name. Original/specialized? Root/specialized? something else? Check the specs!

 *
 * Created by pieter.bos on 21/10/15.
 */
public class Flattener {

    private final MetaModels metaModels;
    //to be able to store Template Overlays transparently during flattening
    private OverridingArchetypeRepository repository;

    private Archetype parent;
    private Archetype child;

    private Archetype result;
    private boolean createOperationalTemplate = false;
    private boolean removeLanguagesFromMetaData = false;
    private boolean useComplexObjectForArchetypeSlotReplacement = false;

    private String[] languagesToKeep = null;

    private RulesFlattener rulesFlattener = new RulesFlattener();


    public Flattener(ArchetypeRepository repository, ReferenceModels models) {
        this.repository = new OverridingArchetypeRepository(repository);
        this.metaModels = new MetaModels(models, null);
    }

    public Flattener(ArchetypeRepository repository, MetaModels models) {
        this.repository = new OverridingArchetypeRepository(repository);
        this.metaModels = models;
    }

    public Flattener createOperationalTemplate(boolean makeTemplate) {
        this.createOperationalTemplate = makeTemplate;
        return this;
    }

    /**
     * if this flattener is setup to create operational templates, also set it to remove all languages from the terminology
     * except for the given languages
     * @param languages
     * @return
     */
    public Flattener keepLanguages(String... languages) {
        languagesToKeep = languages;
        return this;
    }

    public Flattener removeLanguagesFromMetadata(boolean remove) {
        this.removeLanguagesFromMetaData = remove;
        return this;
    }

    public Archetype flatten(Archetype toFlatten) {
        if(parent != null) {
            throw new IllegalStateException("You've used this flattener before - single use instance, please create a new one!");
        }

        metaModels.selectModel(toFlatten);
       // new ReflectionConstraintImposer(lookup).setSingleOrMultiple(toFlatten.getDefinition());
        //validate that we can legally flatten first
        String parentId = toFlatten.getParentArchetypeId();
        if(parentId == null) {
            if(createOperationalTemplate) {
                OperationalTemplate template = createOperationalTemplate(toFlatten);
                result = template;
                //make an operational template by just filling complex object proxies and archetype slots
                fillSlots(template);
                TerminologyFlattener.filterLanguages(template, removeLanguagesFromMetaData, languagesToKeep);
                result = template;
            } else {
                result = toFlatten.clone();
            }
            result.getDefinition().setArchetype(result);
            return result;
        }

        this.parent = repository.getArchetype(toFlatten.getParentArchetypeId());
        if(parent == null) {
            throw new IllegalArgumentException("parent archetype not found in repository: " + toFlatten.getParentArchetypeId());
        }
        this.child = toFlatten.clone();//just to be sure, so we don't have to copy more things deeper down


        if(child instanceof Template) {
            Template childTemplate = (Template) child;
            for(TemplateOverlay overlay:childTemplate.getTemplateOverlays()) {
                //we'll flatten them later when we need them, otherwise, you run into problems with archetypes
                //not yet added to repository while we already need them
                repository.addExtraArchetype(overlay);
            }
        }

        if(parent.getParentArchetypeId() != null) {
            //parent needs flattening first
            parent = getNewFlattener().flatten(parent);
        }


        this.result = null;
        if(createOperationalTemplate) {
            result = createOperationalTemplate(parent);
            overrideArchetypeId(result, child);
        } else {
            result = parent.clone();
        }

        //1. redefine structure
        //2. fill archetype slots if we are creating an operational template
        flattenDefinition(result, child);
        if(createOperationalTemplate) {
            removeZeroOccurrencesConstraints(result);
        } else {
            prohibitZeroOccurrencesConstraints(result);
        }

        rulesFlattener.combineRules(child, result, "prefix", "", "", true /* override statements with same tag */);//TODO: actually set a unique prefix
        if(createOperationalTemplate) {
            fillSlots(result);

        }
        TerminologyFlattener.flattenTerminology(result, child);

        if(createOperationalTemplate) {
            TerminologyFlattener.filterLanguages((OperationalTemplate) result, removeLanguagesFromMetaData, languagesToKeep);
        }
        result.getDefinition().setArchetype(result);
        result.setDifferential(false);//note this archetype as being flat
        if(!createOperationalTemplate) {
            //set metadata to specialized archetype
            result.setOriginalLanguage(child.getOriginalLanguage());
            result.setDescription(child.getDescription());
            result.setOtherMetaData(child.getOtherMetaData());
            result.setGenerated(child.getGenerated());
            result.setControlled(child.getControlled());
            result.setBuildUid(child.getBuildUid());
            result.setTranslations(child.getTranslations());
        } //else as well, but is done elsewhere. needs refactor.
        ArchetypeParsePostProcesser.fixArchetype(result);
        return result;
    }


    public void fillSlots(Archetype archetype) { //should this be OperationalTemplate?
        closeArchetypeSlots(archetype);
        fillArchetypeRoots(archetype);
        fillComplexObjectProxies(archetype);
    }

    /** Zero occurrences and existence constraint processing when creating OPT templates. Removes attributes */
    private void removeZeroOccurrencesConstraints(Archetype archetype) {
        Stack<CObject> workList = new Stack<>();
        workList.push(archetype.getDefinition());
        while(!workList.isEmpty()) {
            CObject object = workList.pop();
            List<CAttribute> attributesToRemove = new ArrayList<>();
            for(CAttribute attribute:object.getAttributes()) {
                if(attribute.getExistence() != null && attribute.getExistence().getUpper() == 0 && !attribute.getExistence().isUpperUnbounded()) {
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

    /** Zero occurrences and existence constraint processing when flattening. Does not remove attributes*/
    private void prohibitZeroOccurrencesConstraints(Archetype archetype) {
        Stack<CObject> workList = new Stack<>();
        workList.push(archetype.getDefinition());
        while(!workList.isEmpty()) {
            CObject object = workList.pop();
            for(CAttribute attribute:object.getAttributes()) {
                if(attribute.getExistence() != null && attribute.getExistence().getUpper() == 0 && !attribute.getExistence().isUpperUnbounded()) {
                    //remove children, but do not remove attribute itself to make sure it stays prohibited
                    attribute.setChildren(new ArrayList<>());
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

        }
    }

    private void closeArchetypeSlots(Archetype archetype) {
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

    private void fillArchetypeRoots(Archetype result) {

        Stack<CObject> workList = new Stack<>();
        workList.push(result.getDefinition());
        while(!workList.isEmpty()) {
            CObject object = workList.pop();
            for(CAttribute attribute:object.getAttributes()) {
                for(CObject child:attribute.getChildren()) {
                    if(child instanceof CArchetypeRoot) { //use_archetype
                        fillArchetypeRoot((CArchetypeRoot) child);
                    }
                    workList.push(child);
                }
            }
        }
    }

    private void fillComplexObjectProxies(Archetype result) {

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

    private void fillArchetypeRoot(CArchetypeRoot root) {
        if(createOperationalTemplate) {
            String archetypeRef = root.getArchetypeRef();
            String newArchetypeRef = archetypeRef;
            Archetype archetype = this.repository.getArchetype(archetypeRef);
            if(archetype instanceof TemplateOverlay){
                //we want to be able to check which archetype this is in the UI. If it's an overlay, that means retrieving the non-operational template
                //which is a hassle.
                //That's a problem. Is this the way to fix is?
                newArchetypeRef = archetype.getParentArchetypeId();
            }
            if (archetype == null) {
                throw new IllegalArgumentException("Archetype with reference :" + archetypeRef + " not found.");
            }

            archetype = getNewFlattener().flatten(archetype);

            //
            CComplexObject rootToFill = root;
            if(useComplexObjectForArchetypeSlotReplacement) {
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
            if(!useComplexObjectForArchetypeSlotReplacement) {
                root.setArchetypeRef(newNodeId);
            }
            OperationalTemplate templateResult = (OperationalTemplate) result;

            //todo: should we filter this?
            if(archetype instanceof OperationalTemplate) {
                OperationalTemplate template = (OperationalTemplate) archetype;
                //add all the component terminologies, otherwise we lose translation
                for(String subarchetypeId:template.getComponentTerminologies().keySet()) {
                    templateResult.addComponentTerminology(subarchetypeId, template.getComponentTerminologies().get(subarchetypeId));
                }
            }

            templateResult.addComponentTerminology(newNodeId, terminology);

            String prefix = archetype.getArchetypeId().getConceptId() + "_";
            rulesFlattener.combineRules(archetype, root.getArchetype(), prefix, prefix, rootToFill.getPath(), false); //TODO: add prefixes to make them unique, or some other way of making unique
            //todo: do we have to put something in the terminology extracts?
            //templateResult.addTerminologyExtract(child.getNodeId(), archetype.getTerminology().);
        }

    }

    private static OperationalTemplate createOperationalTemplate(Archetype archetype) {
        Archetype toClone = archetype.clone(); //clone so we do not overwrite the parent archetype. never
        OperationalTemplate result = new OperationalTemplate();
        result.setArchetypeId((ArchetypeHRID) archetype.getArchetypeId().clone());
        result.setDefinition(toClone.getDefinition());
        result.setDifferential(false);

        result.setRmRelease(toClone.getRmRelease());
        result.setAdlVersion(toClone.getAdlVersion());
        result.setTerminology(toClone.getTerminology());
        result.setGenerated(true);
        result.setOtherMetaData(toClone.getOtherMetaData());
        result.setRules(toClone.getRules());
        result.setBuildUid(toClone.getBuildUid());
        result.setDescription(toClone.getDescription());
        result.setOriginalLanguage(toClone.getOriginalLanguage());
        result.setTranslations(toClone.getTranslations());

        return result;
    }

    private static void overrideArchetypeId(Archetype result, Archetype override) {
        result.setArchetypeId(override.getArchetypeId());
        result.setParentArchetypeId(override.getParentArchetypeId());
    }

    private void flattenDefinition(Archetype parent, Archetype specialized) {
        parent.setArchetypeId(specialized.getArchetypeId()); //TODO: override all metadata?
        flattenCObject(null, parent.getDefinition(), Lists.newArrayList(specialized.getDefinition()));

    }

    private void flattenCObject(CAttribute attribute, CObject parent, List<CObject> specializedList) {
        List<CObject> newNodes = new ArrayList<>();
        for(CObject specialized:specializedList) {
            CObject newObject = createSpecializeCObject(attribute, parent, specialized);

            newNodes.add(newObject);
        }

        if(attribute != null && !newNodes.isEmpty()) {
            boolean shouldReplaceParent = shouldReplaceParent(parent, newNodes);
            attribute.replaceChildren(parent.getNodeId(), newNodes, !shouldReplaceParent /* remove original */);
        }
    }

    private CObject createSpecializeCObject(CAttribute attribute, CObject parent, CObject specialized) {
        if(parent == null) {
            return specialized;//TODO: clone?
        }
        CObject newObject = cloneSpecializedObject(attribute, parent, specialized);

        specializeOccurrences(specialized, newObject);
        newObject.setSiblingOrder(getPossiblyOverridenValue(newObject.getSiblingOrder(), specialized.getSiblingOrder()));

        newObject.setNodeId(getPossiblyOverridenValue(newObject.getNodeId(), specialized.getNodeId()));
        newObject.setRmTypeName(getPossiblyOverridenValue(newObject.getRmTypeName(), specialized.getRmTypeName()));

        //now specialize the structure under the specialized node
        specializeContent(parent, specialized, newObject);
        return newObject;
    }

    private void specializeContent(CObject parent, CObject specialized, CObject newObject) {

        if (parent instanceof CComplexObject) {
            if(((CComplexObject) parent).isAnyAllowed() && specialized instanceof CComplexObjectProxy) {
                //you can replace an any allowed node with a CComplexObjectProxy. No content will need to be specialized, just merge it in
            }
            else if(!(specialized instanceof CComplexObject)) {
                //this is the specs. The ADL workbench allows an ARCHETYPE_SLOT to override a C_ARCHETYPE_ROOT without errors. Filed as https://openehr.atlassian.net/projects/AWBPR/issues/AWBPR-72
                throw new IllegalArgumentException(String.format("cannot override complex object %s (%s) with non-complex object %s (%s)", parent.path(), parent.getClass().getSimpleName(), specialized.path(), specialized.getClass().getSimpleName()));
            } else {
                flattenCComplexObject((CComplexObject) newObject, (CComplexObject) specialized);
            }
        }
        else if (newObject instanceof ArchetypeSlot) {//archetypeslot is NOT a complex object. It's replacement can be
            if(specialized instanceof ArchetypeSlot) {
                flattenArchetypeSlot((ArchetypeSlot) newObject, (ArchetypeSlot) specialized);
            } else if(specialized instanceof CArchetypeRoot) {
                //TODO: handle as if this is a template overlay, but inline. Probably needed in the fillArchetypeRoot method, not here?
            } else {
                throw new IllegalArgumentException("Can only replace an archetype slot with an archetype root or another archetype slot, not with a " + newObject.getClass());
            }
        }
    }

    private void specializeOccurrences(CObject specialized, CObject newObject) {
        //TODO: check if overriding occurrences is allowed
        newObject.setOccurrences(getPossiblyOverridenValue(newObject.getOccurrences(), specialized.getOccurrences()));
    }

    private CObject cloneSpecializedObject(CAttribute attribute, CObject parent, CObject specialized) {
        CObject newObject;
        if(attribute == null) {
            //root of archetype. don't clone anything.. alternative: make a mock attribute at the root
            newObject = parent;
        } else {
            newObject = (CObject) parent.clone();
        }
        if(newObject instanceof ArchetypeSlot && specialized instanceof CArchetypeRoot) {
            newObject = (CObject) specialized.clone();
        }
        return newObject;
    }

    private boolean shouldReplaceParent(CObject parent, List<CObject> differentialNodes) {
        for(CObject differentialNode: differentialNodes) {
            if(differentialNode.getNodeId().equals(parent.getNodeId())) {
                //same node id, so no specialization
                return true;
            }
        }
        MultiplicityInterval occurrences = parent.effectiveOccurrences(metaModels::referenceModelPropMultiplicity);
        //isSingle/isMultiple is tricky and not doable just in the parser. Don't use those
        if(isSingle(parent.getParent())) {
            return true;
        } else if(occurrences != null && occurrences.upperIsOne()) {
            //REFINE the parent node case 1, the parent has occurrences upper == 1
            return true;
        } else if (differentialNodes.size() == 1
                && differentialNodes.get(0).effectiveOccurrences(metaModels::referenceModelPropMultiplicity).upperIsOne()) {
            //REFINE the parent node case 2, only one child with occurrences upper == 1
            return true;
        }
        return false;
    }

    private boolean isSingle(CAttribute attribute) {
        if(attribute != null && attribute.getParent() != null && attribute.getDifferentialPath() == null) {
            return !metaModels.isMultiple(attribute.getParent().getRmTypeName(), attribute.getRmAttributeName());
        }
        return false;
    }

    private void flattenArchetypeSlot(ArchetypeSlot parent, ArchetypeSlot specialized) {
        if(specialized.isClosed()) {
            parent.setClosed(true);
        }
        parent.setIncludes(getPossiblyOverridenListValue(parent.getIncludes(), specialized.getIncludes()));
        parent.setExcludes(getPossiblyOverridenListValue(parent.getExcludes(), specialized.getExcludes()));

        //TODO: includes/excludes?
    }

    /**
     * Flatten a CComplexObject. newObject must be a clone of the original parent, specialized the original unmodified
     * specialized node.
     *
     * The attributes of newObject will be changed in place, so newObject will be altered in this operation
     *
     * @param newObject
     * @param specialized
     */
    private void flattenCComplexObject(CComplexObject newObject, CComplexObject specialized) {

        if(specialized instanceof CArchetypeRoot && newObject instanceof CArchetypeRoot) {
            //cloneSpecializedObject() will already have handled the case where the parent is an ARCHETYPE_SLOT
            //and the child is a C_ARCHETYPE_ROOT by cloning the child instead of the parent
            //handle redefinition of CArchetypeRoots here.
            CArchetypeRoot specializedArchetypeRoot = (CArchetypeRoot) specialized;
            if(specializedArchetypeRoot.getArchetypeRef() != null) {
                CArchetypeRoot newArchetypeRoot = (CArchetypeRoot) newObject;
                newArchetypeRoot.setArchetypeRef(specializedArchetypeRoot.getArchetypeRef());
            }
        }

        for(CAttribute attribute:specialized.getAttributes()) {
            flattenSingleAttribute(newObject, attribute);
        }
        for(CAttributeTuple tuple:specialized.getAttributeTuples()) {
            flattenTuple(newObject, tuple);
        }
    }

    private void flattenTuple(CComplexObject newObject, CAttributeTuple tuple) {
        CAttributeTuple matchingTuple = AOMUtils.findMatchingTuple(newObject.getAttributeTuples(), tuple);


        CAttributeTuple tupleClone = (CAttributeTuple) tuple.clone();
        if(matchingTuple == null) {
            //add
            newObject.addAttributeTuple(tupleClone);
        } else {
            //replace
            newObject.getAttributeTuples().remove(matchingTuple);
            newObject.addAttributeTuple(tupleClone);
        }
        for(CAttribute attribute:tupleClone.getMembers()){
            //replace the entire attribute with the attribute from the new tuple
            //this should be all there is to do.
            newObject.replaceAttribute(attribute);
        }
        //update all parent references
        for(CPrimitiveTuple primitiveTuple:tupleClone.getTuples()) {
            int i = 0;
            for(CPrimitiveObject object:primitiveTuple.getMembers()) {
                object.setSocParent(primitiveTuple);
                object.setParent((tupleClone.getMembers().get(i)));
                i++;
            }
        }
    }


    private void flattenSingleAttribute(CComplexObject newObject, CAttribute attribute) {
        if(attribute.getDifferentialPath() != null) {
            //this overrides a specific path
            ArchetypeModelObject object = newObject.itemAtPath(attribute.getDifferentialPath());
            if(object == null) {
                //it is possible that the object points to a reference, in which case we need to clone the referenced node, then try again
                //AOM spec paragraph 7.2: 'proxy reference targets are expanded inline if the child archetype overrides them.'
                //also examples in ADL2 spec about internal references
                //so find the internal references here!
                //TODO: AOMUtils.pathAtSpecializationLevel(pathSegments.subList(0, pathSegments.size()-1), flatParent.specializationDepth());
                CComplexObjectProxy internalReference = new AOMPathQuery(attribute.getDifferentialPath()).findAnyInternalReference(newObject);
                if(internalReference != null) {
                    //in theory this can be a use node within a use node.
                    ComplexObjectProxyReplacement complexObjectProxyReplacement =
                            ComplexObjectProxyReplacement.getComplexObjectProxyReplacement(internalReference);
                    if(complexObjectProxyReplacement != null) {
                        complexObjectProxyReplacement.replace();
                        //and again!
                        flattenSingleAttribute(newObject, attribute);
                    } else {
                        throw new RuntimeException("cannot find target in CComplexObjectProxy");
                    }
                } else {
                    //lookup the parent and try to add the last attribute if it does not exist
                    List<PathSegment> pathSegments = new APathQuery(attribute.getDifferentialPath()).getPathSegments();
                    String pathMinusLastNode = PathUtil.getPath(pathSegments.subList(0, pathSegments.size()-1));
                    CObject parentObject = newObject.itemAtPath(pathMinusLastNode);
                    if(parentObject != null && parentObject instanceof CComplexObject) {
                        //attribute does not exist, but does exist in RM (or it would not have passed the ArchetypeValidator, or the person using
                        //this flattener does not care
                        CAttribute realAttribute = new CAttribute(pathSegments.get(pathSegments.size()-1).getNodeName());
                        ((CComplexObject) parentObject).addAttribute(realAttribute);
                        flattenAttribute(newObject, realAttribute, attribute);
                    }

                }
            }
            else if(object instanceof CAttribute) {
                CAttribute realAttribute = (CAttribute) object;
                flattenAttribute(newObject, realAttribute, attribute);
            } else if (object instanceof CObject) {
                //TODO: what does this mean?
            }

        } else {
            //this overrides the same path
            flattenAttribute(newObject, newObject.getAttribute(attribute.getRmAttributeName()), attribute);
        }
    }

    private Flattener getNewFlattener() {
        return new Flattener(repository, metaModels)
                .createOperationalTemplate(false) //do not create operational template except at the end.
                .useComplexObjectForArchetypeSlotReplacement(useComplexObjectForArchetypeSlotReplacement);
    }

    private Flattener useComplexObjectForArchetypeSlotReplacement(boolean useComplexObjectForArchetypeSlotReplacement) {
        this.useComplexObjectForArchetypeSlotReplacement = useComplexObjectForArchetypeSlotReplacement;
        return this;
    }


    private void flattenAttribute(CComplexObject root, CAttribute parent, CAttribute specialized) {
        if(parent == null) {
            CAttribute childCloned = specialized.clone();
            root.addAttribute(childCloned);
        } else {

            parent.setExistence(getPossiblyOverridenValue(parent.getExistence(), specialized.getExistence()));
            parent.setCardinality(getPossiblyOverridenValue(parent.getCardinality(), specialized.getCardinality()));

            if (specialized.getChildren().size() > 0 && specialized.getChildren().get(0) instanceof CPrimitiveObject) {
                //in case of a primitive object, just replace all nodes
                parent.setChildren(specialized.getChildren());
            } else {

                //ordering the children correctly is tricky.
                // First reorder parentCObjects if necessary, in the case that a sibling order refers to a redefined node
                // in the specialized archetype
                reorderSiblingOrdersReferringToSameLevel(specialized);

                //Now maintain an insertion anchor
                //for when a sibling node has been set somewhere.
                // insert everything after/before the anchor if it is set,
                // at the defined position from the spec if it is null
                SiblingOrder anchor = null;

                List<CObject> parentCObjects = parent.getChildren();

                for (CObject specializedChildCObject : specialized.getChildren()) {

                    //find matching parent and create the child node with it
                    CObject matchingParentObject = findMatchingParentCObject(specializedChildCObject, parentCObjects);
                    CObject specializedObject = createSpecializeCObject(parent, matchingParentObject, specializedChildCObject);

                    if(specializedChildCObject.getSiblingOrder() != null) {
                        //this node has a sibling order, insert in correct place
                        if(!shouldAddNewNode(specializedChildCObject, matchingParentObject, specialized.getChildren())) {
                            parent.removeChild(matchingParentObject.getNodeId());
                        }
                        parent.addChild(specializedObject, specializedChildCObject.getSiblingOrder());
                        if(specializedChildCObject.getSiblingOrder().isBefore()) {
                            anchor = specializedChildCObject.getSiblingOrder();
                        } else {
                            anchor = SiblingOrder.createAfter(specializedChildCObject.getNodeId());
                        }
                    } else if (anchor != null) {

                        if(!shouldAddNewNode(specializedChildCObject, matchingParentObject, specialized.getChildren())) {
                            parent.removeChild(matchingParentObject.getNodeId());

                        }
                        parent.addChild(specializedObject, anchor);

                        if(!anchor.isBefore()) {
                            anchor.setSiblingNodeId(specializedChildCObject.getNodeId());
                        }
                    } else { //no sibling order

                        if(matchingParentObject == null) {
                            //extension nodes should be added to the last position
                            parent.addChild(specializedObject);
                        } else {
                            parent.addChild(specializedObject, SiblingOrder.createAfter(findLastSpecializedChildDirectlyAfter(parent, matchingParentObject)));
                            if(!shouldAddNewNode(specializedChildCObject, matchingParentObject, specialized.getChildren())) {
                                //we should remove the parent
                                parent.removeChild(matchingParentObject.getNodeId());
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * If the following occurs:
     *
     * after[id3]
     * ELEMENT[id2]
     * ELEMENT[id3.1]
     *
     * Reorder it and remove sibling orders:
     *
     * ELEMENT[id3.1]
     * ELEMENT[id2]
     *
     * If sibling order do not refer to specialized nodes at this level, leaves them alone
     * @param parent the attribute to reorder the child nodes for
     */
    private void reorderSiblingOrdersReferringToSameLevel(CAttribute parent) {
        for(CObject cObject:new ArrayList<>(parent.getChildren())) {
            if(cObject.getSiblingOrder() != null) {
                String matchingNodeId = findCObjectMatchingSiblingOrder(cObject.getSiblingOrder(), parent.getChildren());
                if(matchingNodeId != null) {
                    parent.removeChild(cObject.getNodeId());
                    SiblingOrder siblingOrder = new SiblingOrder();
                    siblingOrder.setSiblingNodeId(matchingNodeId);
                    siblingOrder.setBefore(cObject.getSiblingOrder().isBefore());
                    parent.addChild(cObject, siblingOrder);
                    cObject.setSiblingOrder(null);//unset sibling order, it has been processed already
                }
            }
        }
    }

    private String findCObjectMatchingSiblingOrder(SiblingOrder siblingOrder, List<CObject> cObjectList) {
        for(CObject object:cObjectList) {
            if(isOverriddenIdCode(object.getNodeId(), siblingOrder.getSiblingNodeId())) {
                return object.getNodeId();
            }
        }
        return null;
    }

    /**
     * Give an attribute and a CObject that is a child of that attribute, find the node id of the last object that is
     * in the list of nodes directly after the child attribute that specialize the matching parent. If the parent
     * has not yet been specialized, returns the parent node id
     *
     * @param parent
     * @param matchingParentObject
     * @return
     */
    private String findLastSpecializedChildDirectlyAfter(CAttribute parent, CObject matchingParentObject) {
        int matchingIndex = parent.getIndexOfChildWithNodeId(matchingParentObject.getNodeId());
        String result = matchingParentObject.getNodeId();
        for(int i = matchingIndex+1; i < parent.getChildren().size(); i++) {
            if(isOverriddenIdCode(parent.getChildren().get(i).getNodeId(), matchingParentObject.getNodeId())) {
                result = parent.getChildren().get(i).getNodeId();
            }
        }
        return result;
    }

    private boolean shouldAddNewNode(CObject specializedChildCObject, CObject matchingParentObject, List<CObject> specializedChildren) {
        if(matchingParentObject == null) {
            return true;
        }
        List<CObject> allMatchingChildren = new ArrayList<>();
        for (CObject specializedChild : specializedChildren) {
            if (isOverridenCObject(specializedChild, matchingParentObject)) {
                allMatchingChildren.add(specializedChild);
            }

        }
        //the last matching child should possibly replace the parent, the rest should just add
        //if there is just one child, that's fine, it should still work
        if(allMatchingChildren.get(allMatchingChildren.size()-1).getNodeId().equalsIgnoreCase(specializedChildCObject.getNodeId())) {
            return !shouldReplaceParent(matchingParentObject, allMatchingChildren);
        }
        return true;
    }

    /**
     * Find the matching parent CObject given a specialized child. REturns null if not found.
     * @param specializedChildCObject
     * @param parentCObjects
     * @return
     */
    private CObject findMatchingParentCObject(CObject specializedChildCObject, List<CObject> parentCObjects) {
        for (CObject parentCObject : parentCObjects) {
            if (isOverridenCObject(specializedChildCObject, parentCObject)) {
                return parentCObject;
            }
        }
        return null;
    }

    private boolean isOverridenCObject(CObject specialized, CObject parent) {
        String specializedNodeId = specialized.getNodeId();
        String parentNodeId = parent.getNodeId();
        return isOverriddenIdCode(specializedNodeId, parentNodeId);
    }

    public static boolean isOverriddenIdCode(String specializedNodeId, String parentNodeId) {
        if(specializedNodeId.equalsIgnoreCase(parentNodeId)) {
            return true;
        }
        if(specializedNodeId.lastIndexOf('.') > 0) {
            specializedNodeId = specializedNodeId.substring(0, specializedNodeId.lastIndexOf('.'));//-1?
        }
        return specializedNodeId.equals(parentNodeId) || specializedNodeId.startsWith(parentNodeId + ".");
    }

    private List<Assertion> getPossiblyOverridenListValue(List<Assertion> parent, List<Assertion> child) {
        if(child != null && !child.isEmpty()) {
            return child;
        }
        return parent;
    }

    public <T> T getPossiblyOverridenValue(T parent, T specialized) {
        if(specialized != null) {
            return specialized;
        }
        return parent;
    }


}
