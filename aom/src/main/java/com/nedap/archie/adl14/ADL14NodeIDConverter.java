package com.nedap.archie.adl14;

import com.nedap.archie.adl14.log.ADL2ConversionLog;
import com.nedap.archie.adl14.log.ConvertedCodeResult;
import com.nedap.archie.adl14.log.CreatedCode;
import com.nedap.archie.adl14.log.ReasonForCodeCreation;
import com.nedap.archie.aom.*;
import com.nedap.archie.aom.primitives.CString;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ValueSet;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.aom.utils.NodeIdUtil;
import com.nedap.archie.base.Cardinality;
import com.nedap.archie.paths.PathSegment;
import com.nedap.archie.query.APathQuery;
import com.nedap.archie.rminfo.MetaModels;
import com.nedap.archie.rules.*;

import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.nedap.archie.aom.utils.AOMUtils.parentIsMultiple;

public class ADL14NodeIDConverter {

    private final Archetype archetype;
    private final Archetype flatParentArchetype;

    private final ADL14ConversionConfiguration conversionConfiguration;

    private final ADL14TermConstraintConverter termConstraintConverter;
    private final PreviousConversionApplier previousConversionApplier;
    private final ADL2ConversionResult conversionResult;
    private final MetaModels metaModels;

    private IdCodeGenerator idCodeGenerator;

    /**
     * Contains a mapping between the adl 1.4 codes and the generated adl 2 code
     */
    private final Map<String, ConvertedCodeResult> convertedCodes = new LinkedHashMap<>();
    private final Map<String, CreatedCode> createdCodes = new LinkedHashMap<>();
    private final Map<String, ValueSet> createdValueSets = new LinkedHashMap<>();
    private final Map<String, String> newCodeToOldCodeMap = new LinkedHashMap<>();

    public ADL14NodeIDConverter(MetaModels metaModels, Archetype archetype, Archetype flatParentArchetype, ADL14ConversionConfiguration configuration, ADL2ConversionLog oldLog, ADL2ConversionResult conversionResult) {
        this.metaModels = metaModels;
        this.conversionConfiguration = configuration;
        this.archetype = archetype;
        this.flatParentArchetype = flatParentArchetype;
        this.termConstraintConverter = new ADL14TermConstraintConverter(this, archetype, flatParentArchetype);
        this.previousConversionApplier = new PreviousConversionApplier(this, archetype, oldLog);
        this.conversionResult = conversionResult;
    }

    public ADL14ConversionConfiguration getConversionConfiguration() {
        return conversionConfiguration;
    }

    public ADL2ConversionLog convert() {
        metaModels.selectModel(archetype);

        correctItemsCardinality(archetype.getDefinition());
        List<String> unnecessaryCodes = findUnnecessaryCodes(archetype.getDefinition(),
                archetype.getTerminology().getTermDefinitions().get(archetype.getOriginalLanguage().getCodeString()));
        convert(archetype.getDefinition());

        //tricky stuff here:
        //apply the previous conversion. This does 3 things:
        //1. add synthesized id codes in the same was as before
        //2. create previously synthesized term codes plus binding again
        //3. create previously created value sets again
        //These processes are not tricky in itself, but in what effect it has on the rest of the conversion process
        //for example, if you add an at-code to the term bindings here, it must not be converted again
        //this is done by keeping a log of converted codes in this class, and not converting any term bindings
        //unless they are in the converted codes.
        previousConversionApplier.addCreatedCodes();
        previousConversionApplier.addValueSets();
        this.idCodeGenerator = new OutsideRangeIdCodeGenerator(archetype);
        termConstraintConverter.convert();

        convertTermBindings(archetype);
        generateMissingNodeIds(archetype.getDefinition());
        convertTermDefinitions(archetype, convertedCodes, unnecessaryCodes);
        previousConversionApplier.removeCreatedUnusedTermCodesAndValueSets();
        return new ADL2ConversionLog(/*convertedCodes*/ null, createdCodes, createdValueSets);
    }

    /**
     * Correct invalid CLUSTER.items cardinality, since for a cluster cardinality must be >= 1
     */
    private void correctItemsCardinality(CObject cObject) {
        for (CAttribute attribute : cObject.getAttributes()) {
            if (attribute.getRmAttributeName().equalsIgnoreCase("items") && cObject.getRmTypeName().equalsIgnoreCase("CLUSTER") && attribute.getCardinality() != null) {
                Cardinality cardinality = attribute.getCardinality();
                if (cardinality.getInterval().isUpperUnbounded() && cardinality.getInterval().getLower() == 0 && cardinality.getInterval().isLowerIncluded()) {
                    cardinality.getInterval().setLower(1);
                }

            }
            for (CObject child : attribute.getChildren()) {
                correctItemsCardinality(child);
            }
        }

    }

    /**
     * Find which node identifiers don't need to be in the archetype terminology in ADL2.
     * <p>
     * The identifiers of single object nodes defined under single-valued attributes don't need terminology definitions,
     * so filter those which have a short text and "@ internal @" in the description in the original language.
     *
     * @see <a href="https://specifications.openehr.org/releases/AM/latest/ADL2.html#_node_identifiers_2">ADL2 - 4.3.4.1. Node Identifiers</a>
     */
    private List<String> findUnnecessaryCodes(CObject cObject, Map<String, ArchetypeTerm> originalLanguageTermDefinitions) {
        List<String> result = new ArrayList<>();

        String code = cObject.getNodeId();
        ArchetypeTerm term = originalLanguageTermDefinitions.get(code);
        if (term != null && term.getText() != null && term.getText().length() < 19
                && term.getDescription() != null && term.getDescription().contains("@ internal @")) {
            if (!cObject.isRootNode() && !parentIsMultiple(cObject, flatParentArchetype, metaModels)) {
                result.add(code);
            }
        }

        for (CAttribute attribute : cObject.getAttributes()) {
            for (CObject child : attribute.getChildren()) {
                result.addAll(findUnnecessaryCodes(child, originalLanguageTermDefinitions));
            }
        }
        return result;
    }

    /**
     * Replace old id's in term definition with the new codes
     */
    public static void convertTermDefinitions(Archetype archetype, Map<String, ConvertedCodeResult> convertedCodes, List<String> unnecessaryCodes) {
        //process the codes in alphabetical order, high to low, to prevent overwriting codes
        //even better would probably be to create an empty terminology and separate all new+converted codes and old codes
        //instead of doing this in place. Worth a refactor perhaps?
        ArrayList<ConvertedCodeResult> sortedCodes = new ArrayList<>(convertedCodes.values());
        Comparator<ConvertedCodeResult> comparator = Comparator.comparing(ConvertedCodeResult::getOriginalCode);
        sortedCodes.sort(comparator.reversed());

        for (ConvertedCodeResult convertedCode : sortedCodes) {
            for (String language : archetype.getTerminology().getTermDefinitions().keySet()) {
                Map<String, ArchetypeTerm> terms = archetype.getTerminology().getTermDefinitions().get(language);
                ArchetypeTerm term = terms.remove(convertedCode.getOriginalCode());
                if (term != null && !unnecessaryCodes.contains(convertedCode.getOriginalCode())) {
                    for (String newCode : convertedCode.getConvertedCodes()) {
                        ArchetypeTerm newTerm = new ArchetypeTerm();
                        newTerm.setCode(newCode);
                        newTerm.setText(term.getText());
                        newTerm.setDescription(term.getDescription());
                        newTerm.putAll(term.getOtherItems());
                        terms.put(newCode, newTerm);
                    }
                }
            }
        }

        List<String> termsToRemove = new ArrayList<>();
        for (String language : archetype.getTerminology().getTermDefinitions().keySet()) {
            Map<String, ArchetypeTerm> terms = archetype.getTerminology().getTermDefinitions().get(language);
            for (String key : terms.keySet()) {
                if (unnecessaryCodes.contains(key)) {
                    termsToRemove.add(key);
                }
            }
            for (String term : termsToRemove) {
                terms.remove(term);
            }
        }

        //the terminology can still contain old unused codes now. The archetype validation will warn about that later
    }


    /**
     * Replace old id's in term bindings with the new codes
     */
    private void convertTermBindings(Archetype archetype) {
        if (archetype.getTerminology().getTermBindings() != null) {
            Map<String, Map<String, URI>> newTermBindings = new LinkedHashMap<>();
            for (String terminologyId : archetype.getTerminology().getTermBindings().keySet()) {
                Map<String, URI> termbindingMap = archetype.getTerminology().getTermBindings().get(terminologyId);
                Map<String, URI> newTermbindingsMap = new LinkedHashMap<>();
                newTermBindings.put(terminologyId, newTermbindingsMap);
                for (String valueOrPath : termbindingMap.keySet()) {
                    if (valueOrPath.startsWith("/")) {
                        String newPath = convertPath(valueOrPath);
                        newTermbindingsMap.put(newPath, termbindingMap.get(valueOrPath));
                    } else if (AOMUtils.isValueCode(valueOrPath)) {
                        if (convertedCodes.containsKey(valueOrPath)) {
                            for (String newCode : convertedCodes.get(valueOrPath).getConvertedCodes()) {
                                newTermbindingsMap.put(newCode, termbindingMap.get(valueOrPath));
                            }
                        } else {

                            newTermbindingsMap.put(valueOrPath, termbindingMap.get(valueOrPath));
                            conversionResult.getLog().addWarningWithLocation(ADL14ConversionMessageCode.WARNING_UNKNOWN_CODE_TYPE_IN_TERMBINDING, valueOrPath, valueOrPath);
                        }
                    } else if (AOMUtils.isValueSetCode(valueOrPath)) {
                        if (convertedCodes.containsKey(valueOrPath)) {
                            for (String newCode : convertedCodes.get(valueOrPath).getConvertedCodes()) {
                                newTermbindingsMap.put(newCode, termbindingMap.get(valueOrPath));
                            }
                        } else {
                            //unused value set code, this can be converted
                            newTermbindingsMap.put(convertCode(valueOrPath, "ac"), termbindingMap.get(valueOrPath));
                        }
                    } else if (AOMUtils.isIdCode(valueOrPath)) {
                        if (convertedCodes.containsKey(valueOrPath)) {
                            for (String newCode : convertedCodes.get(valueOrPath).getConvertedCodes()) {
                                newTermbindingsMap.put(newCode, termbindingMap.get(valueOrPath));
                            }
                        } else {
                            //this is very strange, a term binding in an ADL 1.4 archetype starting with id. Warn and just add
                            newTermbindingsMap.put(valueOrPath, termbindingMap.get(valueOrPath));
                            conversionResult.getLog().addWarningWithLocation(ADL14ConversionMessageCode.WARNING_UNKNOWN_CODE_TYPE_IN_TERMBINDING, valueOrPath, valueOrPath);
                        }
                    } else {
                        newTermbindingsMap.put(valueOrPath, termbindingMap.get(valueOrPath));
                        conversionResult.getLog().addWarningWithLocation(ADL14ConversionMessageCode.WARNING_UNKNOWN_CODE_TYPE_IN_TERMBINDING, valueOrPath, valueOrPath);
                    }
                }
            }
            archetype.getTerminology().setTermBindings(newTermBindings);
        }
    }

    /**
     * Add missing node id's for CObjects, as long as they are not CPrimitiveObjects
     */
    private void generateMissingNodeIds(CObject cObject) {

        if (!(cObject instanceof CPrimitiveObject) && cObject.getNodeId() == null) {
            String path = cObject.getPath();
            if (archetype.getParentArchetypeId() != null && flatParentArchetype != null) {
                //get the matching path in the parent archetype id. Find this node
                //if found, this is a specialization of said node and needs to be checked for differences and/or
                //given the same node id
                //if not found, generate/synthesize a new node id.
                String parentPath = AOMUtils.pathAtSpecializationLevel(cObject.getPathSegments(), archetype.specializationDepth() - 1);

                CAttribute cAttributeInParent = flatParentArchetype.itemAtPath(parentPath);
                if (cAttributeInParent != null) {
                    List<CObject> childrenWithSameRmTypeName = cAttributeInParent.getChildrenByRmTypeName(cObject.getRmTypeName());
                    if (childrenWithSameRmTypeName.size() == 1) {
                        cObject.setNodeId(childrenWithSameRmTypeName.get(0).getNodeId());
                    } else if (childrenWithSameRmTypeName.size() > 1) {
                        //this is a bit odd - it now specializes the first child it finds. Let's warn
                        synthesizeNodeId(cObject, path);
                        conversionResult.getLog().addWarningWithLocation(ADL14ConversionMessageCode.WARNING_SPECIALIZED_FIRST_MATCHING_CHILD, cObject.path());
                    } else if (cAttributeInParent.getChildren().size() == 1 || cObject.getParent().getChildren().size() == 1) {
                        if (this.metaModels.rmTypesConformant(cObject.getRmTypeName(), cAttributeInParent.getChildren().get(0).getRmTypeName())) {
                            //this replaces a parent node, so a specialisation. add id code and possibly a term
                            createSpecialisedNodeId(cObject, path, Arrays.asList(cAttributeInParent.getChildren().get(0)));
                        } else {
                            // doesn't conform to parent type, but we assume does to RM type.
                            // E.g. DV_INTERVAL<> being added alongside a DV_QUANTITY
                            synthesizeNodeId(cObject, path);
                        }
                    } else {
                        //TODO: this comes from ADL workbench. Does it cover all cases? I mean the else directly above could apply,
                        //or there could be a conformant node that can be specialised - this does not check
                        //TODO: log error, but continue instead of throwing exception?
                        throw new RuntimeException("cannot convert node id at path " + path);//TODO: proper exception
                    }
                } else {
                    synthesizeNodeId(cObject, path);
                }
            } else {
                synthesizeNodeId(cObject, path);
            }
        }

        for (CAttribute attribute : cObject.getAttributes()) {
            for (CObject object : attribute.getChildren()) {
                generateMissingNodeIds(object);
            }
        }
    }

    /**
     * Object is a specialised node, create a specialised nodeId and add it to the terminology
     */
    private void createSpecialisedNodeId(CObject cObject, String path, List<CObject> childrenWithSameRmTypeName) {
        cObject.setNodeId(idCodeGenerator.generateNextSpecializedIdCode(childrenWithSameRmTypeName.get(0).getNodeId()));
        CreatedCode createdCode = new CreatedCode(cObject.getNodeId(), ReasonForCodeCreation.C_OBJECT_WITHOUT_NODE_ID);
        createdCode.setRmTypeName(cObject.getRmTypeName());
        createdCode.setPathCreated(path);
        addCreatedCode(cObject.getNodeId(), createdCode);
        createTermForNewCode(cObject);
    }

    /**
     * Object needs a new nodeId, generate the next valid nodeId and add in to the terminology
     */
    private void synthesizeNodeId(CObject cObject, String path) {
        if (conversionConfiguration.getNodeIdCodeSystem().equals(ADL14ConversionConfiguration.NODE_ID_CODE_SYSTEM.ID_CODED)) {
            cObject.setNodeId(idCodeGenerator.generateNextIdCode());
        } else {
            cObject.setNodeId(idCodeGenerator.generateNextValueCode());
        }
        CreatedCode createdCode = new CreatedCode(cObject.getNodeId(), ReasonForCodeCreation.C_OBJECT_WITHOUT_NODE_ID);
        createdCode.setRmTypeName(cObject.getRmTypeName());
        createdCode.setPathCreated(path);
        addCreatedCode(cObject.getNodeId(), createdCode);
        createTermForNewCode(cObject);
    }

    /**
     * Create an instance of the new nodeId to the terminology
     */
    private void createTermForNewCode(CObject cObject) {
        if (cObject.getParent().isMultiple()) {
            for (String language : archetype.getTerminology().getTermDefinitions().keySet()) {
                //TODO: add new archetype term to conversion log?
                ArchetypeTerm term = termConstraintConverter.getTerm(language, cObject);
                if (term != null) {
                    ArchetypeTerm newTerm = new ArchetypeTerm();
                    newTerm.setCode(cObject.getNodeId());
                    newTerm.setText(term.getText() + " (synthesised)");
                    newTerm.setDescription(term.getDescription() + " (synthesised)");
                    archetype.getTerminology().getTermDefinitions().get(language).put(newTerm.getCode(), newTerm);
                }
            }
        }
    }

    protected void addCreatedCode(String code, CreatedCode createdCode) {
        this.createdCodes.put(code, createdCode);
    }

    protected void addCreatedValueSet(String nodeId, ValueSet valueSet) {
        this.createdValueSets.put(nodeId, valueSet);
    }

    /**
     * Convert node id's for cObject and all child objects within
     */
    private void convert(CObject cObject) {
        if (cObject instanceof CComplexObject) {
            calculateNewNodeId(cObject);
        } else if (cObject instanceof ArchetypeSlot) {
            calculateNewNodeId(cObject);
            if (cObject.getNodeId() != null) {
                //VSSID validation does not exist in ADL 1.4. Fix it here

                if (flatParentArchetype != null) {
                    String parentPath = AOMUtils.pathAtSpecializationLevel(cObject.getPathSegments(), archetype.specializationDepth() - 1);
                    CObject cObjectInParent = flatParentArchetype.itemAtPath(parentPath);
                    if (cObjectInParent instanceof ArchetypeSlot && !cObjectInParent.getNodeId().equalsIgnoreCase(cObject.getNodeId())) {
                        //specializing a node id for an archetype slot is not allowed in ADL 2. Set to parent node id.
                        cObject.setNodeId(cObjectInParent.getNodeId());
                        //TODO: remove id code from terminology as well
                    }
                }
                ArchetypeSlot slot = (ArchetypeSlot) cObject;
                for (Assertion assertion : slot.getIncludes()) {
                    if (assertion.getExpression() != null) {
                        fixArchetypeSlotExpression(assertion.getExpression());
                    }
                }
                for (Assertion assertion : slot.getExcludes()) {
                    if (assertion.getExpression() != null) {
                        fixArchetypeSlotExpression(assertion.getExpression());
                    }
                }
            }
        } else if (cObject instanceof CComplexObjectProxy) {
            CComplexObjectProxy proxy = (CComplexObjectProxy) cObject;
            calculateNewNodeId(cObject);
            proxy.setTargetPath(convertPath(proxy.getTargetPath()));
        }
        for (CAttribute attribute : cObject.getAttributes()) {
            for (CObject object : attribute.getChildren()) {
                convert(object);
            }
        }
    }

    /**
     * Fix archetype slot regex for version of archetype
     * ADL1.4: \.v1
     * ADL2:   \.v1\..*
     */
    private static final Pattern ARCHETYPE_ID_ENDS_WITH_VERSION_PATTERN = Pattern.compile(".*v[0-9]+");
    private static final Pattern ARCHETYPE_ID_ENDS_WITH_VERSION_PATTERN_REPLACE = Pattern.compile("(?<version>\\.v[0-9]+)(?<end>([\\|]|\\z))");//\z means end of input
    private static void fixArchetypeSlotExpression(Expression expression) {
        //match patterns in the form archetype_id/value matches {/regexp/} or archetype_id/value matches {"string"}
        if (expression instanceof BinaryOperator) {
            BinaryOperator binary = (BinaryOperator) expression;
            if (binary.getOperator() == OperatorKind.matches) {
                Expression rightOperand = binary.getRightOperand();
                if (rightOperand instanceof Constraint) {
                    Constraint<?> constraint = (Constraint<?>) rightOperand;
                    if (constraint.getItem() != null && constraint.getItem().getConstraint() != null && !constraint.getItem().getConstraint().isEmpty() &&
                            constraint.getItem() instanceof CString) {
                        CString cString = (CString) constraint.getItem();
                        if (cString.getConstraint() == null || cString.getConstraint().isEmpty()) {
                            return;
                        }
                        String pattern = cString.getConstraint().get(0);
                        if (pattern.startsWith("^") || pattern.startsWith("/")) {
                            //regexp
                            pattern = pattern.substring(1, pattern.length() - 1);
                            Matcher matcher = ARCHETYPE_ID_ENDS_WITH_VERSION_PATTERN_REPLACE.matcher(pattern);
                            if (matcher.find()) {
                                pattern = "/" + matcher.replaceAll("${version}\\\\..*${end}") + "/";
                                cString.getConstraint().remove(0);
                                cString.getConstraint().add(pattern);
                            }
                        } else {
                            //string instead of regexp. does this happen in any archetypes?
                            if (ARCHETYPE_ID_ENDS_WITH_VERSION_PATTERN.matcher(pattern).matches()) {
                                cString.getConstraint().remove(0);
                                cString.getConstraint().add(pattern + ".*");
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * If the object has a nodeId
     * - replace it with a new nodeId
     * - store the old and new nodeId as a converted code
     */
    private void calculateNewNodeId(CObject cObject) {
        if (cObject.getNodeId() != null) {
            String oldNodeId = cObject.getNodeId();
            String newNodeId;
            if (conversionConfiguration.getNodeIdCodeSystem().equals(ADL14ConversionConfiguration.NODE_ID_CODE_SYSTEM.ID_CODED)) {
                newNodeId = convertNodeId(oldNodeId);
                addConvertedCode(oldNodeId, newNodeId);
                cObject.setNodeId(newNodeId);
            }
        }
    }

    /**
     * Stores the old and new nodeIds in a map, so that all old nodeId can be converted into the same new nodeId
     */
    protected void addConvertedCode(String oldNodeId, String newNodeId) {
        if (convertedCodes.containsKey(oldNodeId)) {
            convertedCodes.get(oldNodeId).addNewCode(newNodeId);
        } else {
            convertedCodes.put(oldNodeId, new ConvertedCodeResult(oldNodeId, newNodeId));
        }
        this.newCodeToOldCodeMap.put(newNodeId, oldNodeId);
    }

    /**
     * Convert old code into an id code
     */
    public String convertNodeId(String oldNodeId) {
        ConvertedCodeResult convertedCodeResult = convertedCodes.get(oldNodeId);
        if (convertedCodeResult != null && convertedCodeResult.hasIdCode()) {
            return convertedCodeResult.getIdCode();
        }
        return convertCode(oldNodeId, "id");
    }

    /**
     * Convert old code into an at code
     */
    protected String convertIntoAtCode(String oldCode) {
        ConvertedCodeResult convertedCodeResult = convertedCodes.get(oldCode);
        if (convertedCodeResult != null && convertedCodeResult.hasValueCode()) {
            return convertedCodeResult.getValueCode();
        }
        return convertCode(oldCode, "at");
    }

    /**
     * Convert old code into an ac code
     */
    public String convertValueSetCode(String oldCode) {
        ConvertedCodeResult convertedCodeResult = convertedCodes.get(oldCode);
        if (convertedCodeResult != null && convertedCodeResult.hasValueCode()) {
            return convertedCodeResult.getValueCode();
        }
        return convertCode(oldCode, "ac");
    }

    /**
     * Convert old code into a code with prefix 'newCodePrefix'
     * - Takes into account removing the leading zero's of the ADL1.4 codes
     * - Takes into account that ADL1.4 starts with code 0 at the root and ADL2 starts with code 1
     */
    public static String convertCode(String oldCode, String newCodePrefix) {
        NodeIdUtil nodeIdUtil = new NodeIdUtil(oldCode);
        if (nodeIdUtil.getCodes().isEmpty()) {
            return oldCode;
        }
        nodeIdUtil.setPrefix(newCodePrefix); //will automatically strip the leading zeroes due to integer-parsing
        if (!oldCode.startsWith("at0.") && !oldCode.startsWith("ac0.")) {
            //a bit tricky, since the root of an archetype starts with at0000.0, but that's different from this I guess
            nodeIdUtil.getCodes().set(0, nodeIdUtil.getCodes().get(0) + 1); //increment with 1, old is 0-based
        }
        return nodeIdUtil.toString();
    }

    /**
     * Convert all old codes in a path in the new codes
     */
    public String convertPath(String key) {
        APathQuery aPathQuery = new APathQuery(key);
        for (PathSegment segment : aPathQuery.getPathSegments()) {
            if (conversionConfiguration.getNodeIdCodeSystem().equals(ADL14ConversionConfiguration.NODE_ID_CODE_SYSTEM.ID_CODED) && segment.getNodeId() != null) {
                segment.setNodeId(convertNodeId(segment.getNodeId()));
            }
        }
        return aPathQuery.toString();
    }

    /**
     * Given the new nodeId, return the old code of this object
     */
    public String getOldCodeForNewCode(String newNodeId) {
        return this.newCodeToOldCodeMap.get(newNodeId);
    }

    public ADL2ConversionResult getConversionResult() {
        return conversionResult;
    }

    protected IdCodeGenerator getIdCodeGenerator() {
        return idCodeGenerator;
    }
}