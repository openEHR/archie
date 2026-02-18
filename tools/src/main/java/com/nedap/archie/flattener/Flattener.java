package com.nedap.archie.flattener;

import com.nedap.archie.adlparser.modelconstraints.ReflectionConstraintImposer;
import com.nedap.archie.aom.*;
import com.nedap.archie.aom.utils.ArchetypeParsePostProcessor;
import com.nedap.archie.rminfo.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import static com.nedap.archie.flattener.FlattenerUtil.getPossiblyOverridenListValue;
import static com.nedap.archie.flattener.FlattenerUtil.getPossiblyOverridenValue;

/**
 * Flattener. For single use only, create a new flattener for every flatten-action you want to do!
 *
 * Created by pieter.bos on 21/10/15.
 */
public class Flattener implements IAttributeFlattenerSupport {

    private final MetaModelProvider metaModelProvider;
    //to be able to store Template Overlays transparently during flattening
    private OverridingArchetypeRepository repository;

    private MetaModel metaModel;

    private Archetype parent;
    private Archetype child;

    private Archetype result;
    private final FlattenerConfiguration config;

    private RulesFlattener rulesFlattener = new RulesFlattener();
    private AnnotationsAndOverlaysFlattener annotationsAndOverlaysFlattener = new AnnotationsAndOverlaysFlattener();

    CAttributeFlattener cAttributeFlattener = new CAttributeFlattener(this);
    private TupleFlattener tupleFlattener = new TupleFlattener();

    private OperationalTemplateCreator optCreator = new OperationalTemplateCreator(this);



    public Flattener(ArchetypeRepository repository, ReferenceModels models) {
        this(repository, new SimpleMetaModelProvider(models, null));
    }

    /**
     * @deprecated Use {@link #Flattener(ArchetypeRepository, MetaModelProvider)} instead.
     */
    @Deprecated
    public Flattener(ArchetypeRepository repository, MetaModels models) {
        this(repository, (MetaModelProvider) models);
    }

    public Flattener(ArchetypeRepository repository, MetaModelProvider metaModelProvider) {
        this(repository, metaModelProvider, FlattenerConfiguration.forFlattened());
    }

    /**
     * @deprecated Use {@link #Flattener(ArchetypeRepository, MetaModelProvider, FlattenerConfiguration)} instead.
     */
    @Deprecated
    public Flattener(ArchetypeRepository repository, MetaModels models, FlattenerConfiguration configuration) {
        this(repository, (MetaModelProvider) models, configuration);
    }

    public Flattener(ArchetypeRepository repository, MetaModelProvider metaModelProvider, FlattenerConfiguration configuration) {
        this.repository = new OverridingArchetypeRepository(repository);
        this.metaModelProvider = metaModelProvider;
        this.config = configuration.clone();
    }

    /**
     * Create operational templates in addition to flattening. Default is false;
     * @param makeTemplate
     * @return
     */
    public Flattener createOperationalTemplate(boolean makeTemplate) {
        config.setCreateOperationalTemplate(makeTemplate);
        if(makeTemplate) {
            config.setRemoveZeroOccurrencesObjects(true);
        }
        return this;
    }

    /**
     * Remove zero occurrences constraints, instead of leaving them but removing all of their children
     *
     * Default is false
     * @param remove
     * @return
     */
    public Flattener removeZeroOccurrencesConstraints(boolean remove) {
        config.setRemoveZeroOccurrencesObjects(remove);
        return this;
    }

    /**
     * if this flattener is setup to create operational templates, also set it to remove all languages from the terminology
     * except for the given languages
     * @param languages
     * @return
     */
    public Flattener keepLanguages(String... languages) {
        config.setLanguagesToKeep(languages);
        return this;
    }

    public Flattener removeLanguagesFromMetadata(boolean remove) {
        config.setRemoveLanguagesFromMetaData(remove);
        return this;
    }

    public Archetype flatten(Archetype toFlatten) {
        if(parent != null) {
            throw new IllegalStateException("You've used this flattener before - single use instance, please create a new one!");
        }

        metaModel = metaModelProvider.selectAndGetMetaModel(toFlatten);

        //validate that we can legally flatten first
        String parentId = toFlatten.getParentArchetypeId();
        if(parentId == null) {
            if(config.isCreateOperationalTemplate()) {
                OperationalTemplate template = optCreator.createOperationalTemplate(toFlatten);
                result = template;
                //make an operational template by just filling complex object proxies and archetype slots
                optCreator.fillSlots(template);
                optCreator.expandValueSets((OperationalTemplate) result);
                fillOptEmptyOccurrences(result);
                TerminologyFlattener.filterLanguages(template, config.isRemoveLanguagesFromMetaData(), config.getLanguagesToKeep());
                result = template;
            } else {
                result = toFlatten.clone();
            }
            result.getDefinition().setArchetype(result);
            result.setDifferential(false);
            result.setGenerated(true);
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
            Flattener parentFlattener = getNewFlattenerForParent();
            parent = parentFlattener.flatten(parent);
            // Add the template overlays from the parents (if any) to the repository,
            // so template overlays specializing other template overlays can be flattened.
            parentFlattener.getRepository().getExtraArchetypes().forEach(
                    a -> repository.addExtraArchetype(a)
            );
        }


        this.result = null;
        if(config.isCreateOperationalTemplate()) {
            result = optCreator.createOperationalTemplate(parent);
            optCreator.overrideArchetypeId(result, child);
        } else {
            result = child.clone();

            Archetype clonedParent = parent.clone();
            //definition, terminology and rules will be replaced later, but must be set to that of the parent
            // for this flattener to work correctly. I would not write it this way when creating another flattener, but
            //it's the way it is :)
            //parent needs to be cloned because this updates references to parent archetype as well
            result.setDefinition(clonedParent.getDefinition());
            result.setTerminology(clonedParent.getTerminology());
            result.setRules(clonedParent.getRules());
        }
        annotationsAndOverlaysFlattener.flattenAnnotations(parent, child, result);
        annotationsAndOverlaysFlattener.flattenRmOverlay(parent, child, result);

        //1. redefine structure
        //2. fill archetype slots if we are creating an operational template
        flattenDefinition(result, child);

        if(config.isCreateOperationalTemplate() && config.isRemoveZeroOccurrencesObjects()) {
            optCreator.removeZeroOccurrencesConstraints(result);
        } else {
            prohibitZeroOccurrencesConstraints(result);
        }

        // Replaces dashes and convert to lower case, as dashes and variables starting with an upper case letter
        // aren't allowed everywhere.
        String prefix = child.getArchetypeId().getConceptId().replace("-", "_").toLowerCase() + "_";
        //Use empty tagPrefix here. If not empty, overridden rules in specialized archetype will not overwrite base rules,
        //but be added to the rules section additionally to the base rules.
        rulesFlattener.combineRules(child, result, prefix, "", "", true /* override statements with same tag */);
        if(config.isCreateOperationalTemplate()) {
            optCreator.fillSlots((OperationalTemplate) result);

        }
        fillOptEmptyOccurrences(result);
        TerminologyFlattener.flattenTerminology(result, child);

        if(config.isCreateOperationalTemplate()) {
            optCreator.expandValueSets((OperationalTemplate) result);
            TerminologyFlattener.filterLanguages((OperationalTemplate) result, config.isRemoveLanguagesFromMetaData(), config.getLanguagesToKeep());
        }
        result.getDefinition().setArchetype(result);
        result.setDescription(child.getDescription());
        result.setOtherMetaData(child.getOtherMetaData());
        result.setBuildUid(child.getBuildUid());
        result.setOriginalLanguage(child.getOriginalLanguage());
        result.setTranslations(child.getTranslations());

        if(child instanceof Template && !config.isCreateOperationalTemplate()) {
            Template resultTemplate = (Template) result;
            resultTemplate.setTemplateOverlays(new ArrayList<>());
            Template childTemplate = (Template) child;
            //we need to add the flattened template overlays. For operational template these have been added to the archetype structure, so not needed
            for(TemplateOverlay overlay:((Template) child).getTemplateOverlays()){
                TemplateOverlay flatOverlay = (TemplateOverlay) getNewFlattener().flatten(overlay);
                ResourceDescription description = (ResourceDescription) result.getDescription().clone();
                //not sure whether to do this or to implement these methods using the owningTemplate param.
                //in many cases you do want this information...
                flatOverlay.setDescription(description);
                flatOverlay.setOriginalLanguage(result.getOriginalLanguage());
                flatOverlay.setTranslationList(result.getTranslationList());
                ArchetypeParsePostProcessor.fixArchetype(flatOverlay);
                resultTemplate.getTemplateOverlays().add(flatOverlay);
            }
        }

        this.removeSiblingOrder(result);

        result.setDifferential(false);//mark this archetype as being flat
        result.setGenerated(true);

        ArchetypeParsePostProcessor.fixArchetype(result);

        //set the single/multiple attributes correctly
        new ReflectionConstraintImposer(metaModel)
                .setSingleOrMultiple(result.getDefinition());

        return result;
    }

    private void fillOptEmptyOccurrences(Archetype result) {
        if(config.isCreateOperationalTemplate() && config.isFillEmptyOccurrences()) {
            optCreator.fillEmptyOccurrences(result);
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
                    // Remove children, but do not remove attribute itself to make sure it stays prohibited
                    FlattenerUtil.removeAnnotationsForArchetypeConstraints(archetype, attribute.getChildren());
                    attribute.setChildren(new ArrayList<>());
                } else {
                    List<CObject> objectsToRemove = new ArrayList<>();
                    for (CObject child : attribute.getChildren()) {
                        if (!child.isAllowed()) {
                            FlattenerUtil.removeAnnotationsForArchetypeConstraints(archetype, Collections.singletonList(child));
                            if(child instanceof CComplexObject) {
                                ((CComplexObject) child).setAttributes(new ArrayList<>());
                            }
                            if(config.isRemoveZeroOccurrencesObjects()) {
                                objectsToRemove.add(child);
                            }
                        } else {
                            workList.push(child);
                        }
                    }
                    attribute.getChildren().removeAll(objectsToRemove);
                }
            }
        }
    }

    private void removeSiblingOrder(Archetype archetype) {
        Stack<CObject> workList = new Stack<>();
        workList.push(archetype.getDefinition());
        while(!workList.isEmpty()) {
            CObject object = workList.pop();
            for (CAttribute attribute : object.getAttributes()) {
                for (CObject child : attribute.getChildren()) {
                    workList.push(child);
                    if (child.getSiblingOrder() != null) {
                        child.setSiblingOrder(null);
                    }
                }
            }
        }
    }

    private void flattenDefinition(Archetype parent, Archetype specialized) {
        parent.setArchetypeId(specialized.getArchetypeId()); //TODO: override all metadata?
        createSpecializeCObject(null, parent.getDefinition(), specialized.getDefinition());

    }


    @Override
    public CObject createSpecializeCObject(CAttribute attribute, CObject parent, CObject specialized) {
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
            if(newObject.getOccurrences() == null && parent.getOccurrences() != null) {
                newObject.setOccurrences(parent.getOccurrences());
            }
            if(newObject.getDeprecated() == null && parent.getDeprecated() != null) {
                newObject.setDeprecated(parent.getDeprecated());
            }
        }
        return newObject;
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
            cAttributeFlattener.flattenSingleAttribute(newObject, attribute);
        }
        for(CAttributeTuple tuple:specialized.getAttributeTuples()) {
            tupleFlattener.flattenTuple(newObject, tuple);
        }
    }

    /**
     * Get a new flattener to flatten parent archetypes. Works the same as {@link #getNewFlattener()}, except that
     * it will remove zero occurrences constraints in parents if so configured.
     *
     * @return
     */
    protected Flattener getNewFlattenerForParent() {
        Flattener result = new Flattener(repository, metaModelProvider, config)
                .createOperationalTemplate(false); //do not create operational template except at the end.
        if(config.isRemoveZeroOccurrencesInParents()) {
            //remove all zero occurrences objects EXCEPT in the top level archetype
            //so that you can see that things have been removed that you can still edit - but not others
            result.removeZeroOccurrencesConstraints(true);
        }
        return result;
    }

    /**
     * Get a new flattener with the same configuration as this, except that it will not create operational templates
     * <br>
     * The not creating operational templates is because the operational template creator needs to be done only for the
     * final result, not the intermediate steps
     * @return
     */
    protected Flattener getNewFlattener() {
        return new Flattener(repository, metaModelProvider, config);
    }

    private Flattener useComplexObjectForArchetypeSlotReplacement(boolean useComplexObjectForArchetypeSlotReplacement) {
        config.setUseComplexObjectForArchetypeSlotReplacement(useComplexObjectForArchetypeSlotReplacement);
        return this;
    }

    public boolean isUseComplexObjectForArchetypeSlotReplacement() {
        return config.isUseComplexObjectForArchetypeSlotReplacement();
    }

    @Override
    public MetaModel getMetaModel() {
        return metaModel;
    }

    @Override
    @Deprecated
    public MetaModels getMetaModels() {
        if(metaModelProvider instanceof MetaModels) {
            return (MetaModels) metaModelProvider;
        } else {
            throw new IllegalStateException("MetaModels not available");
        }
    }

    @Override
    public FlattenerConfiguration getConfig() {
        return config;
    }


    public boolean getCreateOperationalTemplate() {
        return config.isCreateOperationalTemplate();
    }

    protected RulesFlattener getRulesFlattener() {
        return rulesFlattener;
    }

    protected AnnotationsAndOverlaysFlattener getAnnotationsAndOverlaysFlattener() { return annotationsAndOverlaysFlattener; }

    public OverridingArchetypeRepository getRepository() {
        return repository;
    }

    FlattenerConfiguration getConfiguration() {
        return config;
    }
}
