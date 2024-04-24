package com.nedap.archie.aom.utils;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeHRID;
import com.nedap.archie.aom.ArchetypeModelObject;
import com.nedap.archie.aom.ArchetypeSlot;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CAttributeTuple;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.primitives.CString;
import com.nedap.archie.aom.terminology.ValueSet;
import com.nedap.archie.definitions.AdlCodeDefinitions;
import com.nedap.archie.apath.PathSegment;
import com.nedap.archie.apath.PathUtil;
import com.nedap.archie.query.AOMPathQuery;
import com.nedap.archie.apath.APathQuery;
import com.nedap.archie.query.PartialMatch;
import com.nedap.archie.rminfo.*;
import com.nedap.archie.rules.Assertion;
import com.nedap.archie.rules.BinaryOperator;
import com.nedap.archie.rules.Constraint;
import com.nedap.archie.rules.Expression;
import com.nedap.archie.rules.OperatorKind;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class AOMUtils {

    private static Pattern idCodePattern = Pattern.compile("(id|at|ac)(0|[1-9][0-9]*)(\\.(0|[1-9][0-9]*))*");
    private static Pattern adl14CodePattern = Pattern.compile("(id|at|ac)([0-9]+)(\\.(0|[1-9][0-9]*))*");

    public static int getSpecializationDepthFromCode(String code) {
        if(code == null) {
            return -1;
        } else if(code.indexOf(AdlCodeDefinitions.SPECIALIZATION_SEPARATOR) < 0) {
            return 0;
        } else {
            return StringUtils.countMatches(code, String.valueOf(AdlCodeDefinitions.SPECIALIZATION_SEPARATOR));
        }
    }

    public static boolean isIdCode(String code) {
        return code.startsWith(AdlCodeDefinitions.ID_CODE_LEADER);
    }

    public static boolean isValueCode(String code) {
        return code.startsWith(AdlCodeDefinitions.VALUE_CODE_LEADER);
    }

    public static boolean isValueSetCode(String code) {
        return code.startsWith(AdlCodeDefinitions.VALUE_SET_CODE_LEADER);
    }

    public static boolean isValidValueSetCode(String code) {
        return isValueSetCode(code) && isValidCode(code);
    }

    public static boolean isValidCode(String code) {
        if(code == null) {
            return false;
        }
        return idCodePattern.matcher(code).matches();
    }

    /**
     * Get the numeric node id from a valid id code without any prefix (like at, ac or id)
     * @param nodeId the node id to strip the prefix of.
     * @return the numeric node id without any prefix
     */
    public static String stripPrefix(String nodeId) {
        if(AOMUtils.isValidCode(nodeId)) {
            return nodeId.substring(2);
        }
        return nodeId;
    }

    public static String pathAtSpecializationLevel(List<PathSegment> pathSegments, int level) {
        //todo: this doesn't clone the original
        for(PathSegment segment:pathSegments) {
            if(segment.getNodeId() != null && AOMUtils.isValidCode(segment.getNodeId()) && AOMUtils.getSpecializationDepthFromCode(segment.getNodeId()) > level) {
                segment.setNodeId(codeAtLevel(segment.getNodeId(), level));
            }
        }
        return PathUtil.getPath(pathSegments);
    }

    public static String codeAtLevel(String nodeId, int level) {
        NodeIdUtil nodeIdUtil = new NodeIdUtil(nodeId);
        List<Integer> codes = new ArrayList<>();
        for(int i = 0; i <= level && i < nodeIdUtil.getCodes().size();i++) {
            codes.add(nodeIdUtil.getCodes().get(i));
        }
        //remove leading .0 codes - they are not present in the code at the given level
        int numberOfCodesToRemove = 0;
        for(int i = codes.size()-1; i >= 0 ; i--) {
            if(codes.get(i).intValue() == 0) {
                numberOfCodesToRemove++;
            } else {
                break;
            }
        }
        if(numberOfCodesToRemove > 0) {
            codes = codes.subList(0, codes.size()-numberOfCodesToRemove);
        }
        return nodeIdUtil.getPrefix() + Joiner.on(AdlCodeDefinitions.SPECIALIZATION_SEPARATOR).join(codes);

    }

    public static boolean isOverridenCObject(CObject specialized, CObject parent) {
        return isOverriddenIdCode(specialized.getNodeId(), parent.getNodeId());
    }

    public static boolean isOverriddenIdCode(String specializedNodeId, String parentNodeId) {
        if(specializedNodeId.equalsIgnoreCase(parentNodeId)) {
            return true;
        }

        return specializedNodeId.toLowerCase().startsWith(parentNodeId.toLowerCase() + ".");
    }

    public static CodeRedefinitionStatus getSpecialisationStatusFromCode(String nodeId, int specialisationDepth) {

        if(specialisationDepth > getSpecializationDepthFromCode(nodeId)) {
            return CodeRedefinitionStatus.INHERITED;
        } else {
            boolean codeDefinedAtThisLevel = codeIndexAtLevel(nodeId, specialisationDepth) > 0;
            if(codeDefinedAtThisLevel) {
                if(specialisationDepth > 0 && codeExistsAtLevel(nodeId, specialisationDepth-1)) {
                    return CodeRedefinitionStatus.REDEFINED;
                } else {
                    return CodeRedefinitionStatus.ADDED;
                }

            } else if (specialisationDepth > 0 && codeExistsAtLevel(nodeId, specialisationDepth-1)) {
                return CodeRedefinitionStatus.INHERITED;
            } else {
                return CodeRedefinitionStatus.UNDEFINED;
            }
        }
    }

    public static int codeIndexAtLevel(String nodeId, int specialisationDepth) {
        NodeIdUtil nodeIdUtil = new NodeIdUtil(nodeId);
        if(specialisationDepth < 0 || specialisationDepth >= nodeIdUtil.getCodes().size()) {
            throw new IllegalArgumentException("code is not valid at specialization depth " + specialisationDepth);
        }
        return nodeIdUtil.getCodes().get(specialisationDepth);
    }

    public static ArchetypeModelObject getDifferentialPathFromParent(Archetype flatParent, CAttribute attributeWithDifferentialPath) {
        //adl workbench deviates from spec by only allowing differential paths at root, we allow them everywhere, according to spec
        ArchetypeModelObject parentAOMObject = flatParent.itemAtPath(pathAtSpecializationLevel(attributeWithDifferentialPath.getParent().getPathSegments(), flatParent.specializationDepth()));
        if (parentAOMObject != null && parentAOMObject instanceof CComplexObject) {
            CComplexObject parentObject = (CComplexObject) parentAOMObject;
            ArchetypeModelObject attributeInParent = parentObject.itemAtPath(
                    pathAtSpecializationLevel( //TODO: the ADL workbench does this, so /items[id9.1]/value is a valid differential path even in openEHR-EHR-CLUSTER.exam-uterine_cervix.v1.0.0. Should it be?
                            new APathQuery(attributeWithDifferentialPath.getDifferentialPath()).getPathSegments(),
                            flatParent.specializationDepth()
                    )
            );
            return attributeInParent;
        }
        return null;
    }

    /**
     * Returns true if at least one [idx] predicate is present in the path
     * @param path
     * @return
     */
    public static boolean isArchetypePath(String path) {
        APathQuery query = new APathQuery(path);
        for(PathSegment segment:query.getPathSegments()) {
            if(segment.getNodeId() != null || segment.getArchetypeRef() != null) {
                return true;
            }
        }
        return false;
    }

    //check if the last node id in the path has a bigger specialization level than the specialization level of the parent
    //but it does a little loop to check if it happens somewhere else as well. ok...
    public static boolean isPhantomPathAtLevel(List<PathSegment> pathSegments, int specializationDepth) {
        for(int i = pathSegments.size()-1; i >=0; i--) {
            String nodeId = pathSegments.get(i).getNodeId();
            if(nodeId != null && AOMUtils.isValidCode(nodeId) && specializationDepth > AOMUtils.getSpecializationDepthFromCode(nodeId)) {
                return codeExistsAtLevel(nodeId, specializationDepth);
            }
        }
        return false;
    }

    public static boolean codeExistsAtLevel(String nodeId, int specializationDepth) {
        NodeIdUtil nodeIdUtil = new NodeIdUtil(nodeId);
        int specializationDepthOfCode = AOMUtils.getSpecializationDepthFromCode(nodeId);
        if(specializationDepth <= specializationDepthOfCode) {
            String code = "";
            for(int i = 0; i <= specializationDepth; i++) {
                code += nodeIdUtil.getCodes().get(i);
            }
            return Integer.parseInt(code) > 0;
        }
        return false;
    }

    public static boolean archetypeRefMatchesSlotExpression(String archetypeRef, ArchetypeSlot slot) {
        if(!isSlotPresentAndOpen(slot.getIncludes()) && isSlotPresentAndOpen(slot.getExcludes())) {
            for (Assertion include : slot.getIncludes()) {
                if (matchesInclude(include.getExpression(), archetypeRef)) {
                    return true;
                }
            }
            return false;
        } else if (!isSlotPresentAndOpen(slot.getExcludes()) && isSlotPresentAndOpen(slot.getIncludes())) {
            for (Assertion exclude : slot.getExcludes()) {
                if (matchesExclude(exclude.getExpression(), archetypeRef)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @return true if the contents of an archetype slot are present, but contains an open slot, false otherwise
     */
    public static boolean isSlotPresentAndOpen(List<Assertion> slotAssertions) {
        if(slotAssertions == null || slotAssertions.isEmpty()) {
            return false;
        }
        return slotAssertions.get(0).matchesAny();
    }

    private static boolean matchesInclude(Expression expression, String archetypeRef) {
        Boolean result = matchesExpression(expression, archetypeRef);
        return result == null ? true : result;
    }

    private static boolean matchesExclude(Expression expression, String archetypeRef) {
        Boolean result = matchesExpression(expression, archetypeRef);
        return result == null ? false : result;
    }

    //TODO: because of the split in modules we cannot use the full RuleEvaluation here, which is a pity. So for now only the minor subset.
    private static Boolean matchesExpression(Expression expression, String archetypeRef) {
        if (expression instanceof BinaryOperator) {
            BinaryOperator binary = (BinaryOperator) expression;
            if (binary.getOperator() == OperatorKind.matches) {
                Expression rightOperand = binary.getRightOperand();
                if (rightOperand instanceof Constraint) {
                    Constraint<?> constraint = (Constraint<?>) rightOperand;
                    if(constraint.getItem() != null && constraint.getItem().getConstraint() != null && constraint.getItem().getConstraint().size() > 0 &&
                            constraint.getItem() instanceof CString) {
                        String pattern = ((CString) constraint.getItem()).getConstraint().get(0);
                        if (pattern.startsWith("^") || pattern.startsWith("/")) {
                            //regexp
                            pattern = pattern.substring(1, pattern.length() - 1);
                            return new ArchetypeHRID(archetypeRef).getSemanticId().matches(pattern) ||
                                    archetypeRef.matches(pattern);

                        } else {
                            //string
                            return archetypeRef.equals(pattern);
                        }
                    }
                }
            }
        }
        return null;// unsupported expression type
    }

    public static boolean codesConformant(String childNodeId, String parentNodeId) {
        return isValidCode(childNodeId) && childNodeId.startsWith(parentNodeId) &&
                (childNodeId.length() == parentNodeId.length() || (childNodeId.length() > parentNodeId.length() && childNodeId.charAt(parentNodeId.length()) == AdlCodeDefinitions.SPECIALIZATION_SEPARATOR));

    }

    public static CAttributeTuple findMatchingTuple(List<CAttributeTuple> attributeTuples, CAttributeTuple specializedTuple) {
        return attributeTuples.stream()
                .filter((existingTuple) -> existingTuple.getMemberNames().equals(specializedTuple.getMemberNames()))
                .findAny().orElse(null);
    }

    public static RMAttributeInfo getAttributeInfoAtPath(ModelInfoLookup selectedModel, String rmTypeName, String path) {
        if(!path.contains("/")) {
            //this is not a path
            return selectedModel.getAttributeInfo(rmTypeName, path);
        } else if (path.equals("/")) {
            throw new IllegalArgumentException("cannot retrieve attribute information for path '/'");
        }
        APathQuery query = new APathQuery(path);

        RMTypeInfo typeInfo = selectedModel.getTypeInfo(rmTypeName);

        RMAttributeInfo attribute = null;
        for (PathSegment segment : query.getPathSegments()) {
            if (typeInfo == null) {
                return null;
            }
            attribute = typeInfo.getAttribute(segment.getNodeName());
            if (attribute == null) {
                return null;
            }
            typeInfo = selectedModel.getTypeInfo(attribute.getTypeInCollection());
        }
        return attribute;
    }

    /** Get the maximum code used at the given specialization level. useful for generating new codes*/
    public static int getMaximumIdCode(int specializationDepth, Collection<String> usedIdCodes) {

        int maximumIdCode = 0;
        for(String code:usedIdCodes) {
            if (code.length() > 2) {
                int numberOfDots = getSpecializationDepthFromCode(code);
                if(specializationDepth == numberOfDots) {
                    try {
                        int numericCode = numberOfDots == 0 ? Integer.parseInt(code.substring(2)) : Integer.parseInt(code.substring(code.lastIndexOf('.') + 1));
                        maximumIdCode = Math.max(numericCode, maximumIdCode);
                    } catch (NumberFormatException ex) {
                        //TODO: get rid of this, temporary for term codes that still need conversion!
                    }
                }
            }
        }
        return maximumIdCode;
    }

    /** Get the maximum code used at the given specialization level. useful for generating new codes*/
    public static int getMaximumIdCode(int specializationDepth, String prefix, Collection<String> usedIdCodes) {
        if(specializationDepth == 0) {
            throw new IllegalArgumentException("can only get the maximum code with prefix at a specialization depth > 0");
        }
        int maximumIdCode = 0;
        for(String code:usedIdCodes) {
            if(code.startsWith(prefix + ".")) {
                int numberOfDots = CharMatcher.is(AdlCodeDefinitions.SPECIALIZATION_SEPARATOR).countIn(code);
                if(specializationDepth == numberOfDots) {
                    int numericCode = Integer.parseInt(code.substring(code.lastIndexOf('.')+1));
                    maximumIdCode = Math.max(numericCode, maximumIdCode);
                }
            }
        }
        return maximumIdCode;
    }

    public static boolean isPathInArchetypeOrRm(MetaModel metaModel, String path, Archetype template) {
        AOMPathQuery aomPathQuery = new AOMPathQuery(path);
        PartialMatch partial = aomPathQuery.findPartial(template.getDefinition());
        if(partial.isFullMatch()) {
            return true;
        } else {
            if(isArchetypePath(partial.getRemainingPath())) {
                // the remaining path is an archetype path, so cannot be found purely in the RM without
                //further constraints
                return false;
            }
            //we have a partial match left, search for it in the RM
            //in case there is no match at all, getFoundObjects() will contain the root node, so this is safe
            for (ArchetypeModelObject archetypeModelObject : partial.getFoundObjects()) {
                if (archetypeModelObject instanceof CObject) {
                    if (metaModel.hasReferenceModelPath(((CObject) archetypeModelObject).getRmTypeName(), partial.getRemainingPath())) {
                        return true;
                    }
                } else if (archetypeModelObject instanceof CAttribute) {
                    CAttribute attribute = (CAttribute) archetypeModelObject;
                    //matched an attribute. So if even one object matches, return true
                    for(CObject child:attribute.getChildren()) {
                        if (metaModel.hasReferenceModelPath(child.getRmTypeName(), partial.getRemainingPath())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Given a code such as 'id4.1.0.0.1', return the nearest code that exists in a parent. In this example,
     * returns id4.1, so removes all zeros.
     * @param nodeId
     * @return
     */
    public static String getCodeInNearestParent(String nodeId) {

        NodeIdUtil nodeIdUtil = new NodeIdUtil(nodeId);

        List<Integer> codes = nodeIdUtil.getCodes();
        int newDepth = 0;
        for(int i = codes.size()-2; i >= 0; i--) {
            if(codes.get(i) != 0) {
                newDepth = i;
                break;
            }
        }
        return nodeIdUtil.getPrefix() + Joiner.on('.').join(codes.subList(0, newDepth+1));

    }

    public static boolean isValidADL14Code(String code) {
        if(code == null) {
            return false;
        }
        return adl14CodePattern.matcher(code).matches();
    }

    /**
     * Returns the expanded set of members of a value set, replacing any ac-codes with at-codes if this is a flat archetype
     * replaces each ac-code in valueSet with its expanded list of at-codes in allValueSets.
     *
     * @param allValueSets all value sets in the given archetype or component terminology that applies here
     * @param valueSet the ValueSet to expand
     * @return the expanded value set, where each ac code has been replaced with a number of at codes.
     */
    public static Set<String> getExpandedValueSetMembers(Map<String, ValueSet> allValueSets, ValueSet valueSet) {
        Set<String> result = new LinkedHashSet<>();
        for(String member:valueSet.getMembers()) {
            if(AOMUtils.isValueSetCode(member)) {
                ValueSet includedValueSet = allValueSets.get(member);
                if(includedValueSet == null) {
                    result.add(member);//cannot expand that which we cannot find
                } else {
                    //TODO: potential loop here, resulting in StackOverflowexception. Add validation somewhere?
                    result.addAll(getExpandedValueSetMembers(allValueSets, includedValueSet));
                }
            } else {
                result.add(member);
            }
        }
        return result;
    }

    /**
     * Returns true of the given value set members contain the given code, or if code is a specialization of one of the
     * codes of the value set. False otherwise.
     * @param valueSetMembers the value set members to check against
     * @param code the code to check
     * @return true if code is part of valueSetMembers or a specialisation of one of its members, false otherwise
     */
    public static boolean valueSetContainsCodeOrParent(Collection<String> valueSetMembers, String code) {
        for(String value:valueSetMembers) {
            if(AOMUtils.codesConformant(code, value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the parent attribute of the given CObject is a container attribute.
     *
     * @see MetaModelInterface#isMultiple(String, String)
     */
    public static boolean parentIsMultiple(CObject cObject, Archetype flatParentArchetype, MetaModels metaModels) {
        if(cObject.getParent() != null) {

            CAttribute parent = cObject.getParent();
            CObject owningObject = parent.getParent();
            if (parent.getDifferentialPath() != null && flatParentArchetype != null) {
                CAttribute attributeFromParent = (CAttribute) AOMUtils.getDifferentialPathFromParent(flatParentArchetype, parent);
                if(attributeFromParent != null) {
                    owningObject = attributeFromParent.getParent();
                }

            }
            if(owningObject != null) {
                return metaModels.isMultiple(owningObject.getRmTypeName(), parent.getRmAttributeName());
            }
        }
        return false;
    }
}
