package com.nedap.archie.aom.utils;

import com.nedap.archie.aom.*;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.rules.Assertion;
import com.nedap.archie.rules.Constraint;
import com.nedap.archie.rules.ExpressionVariable;
import com.nedap.archie.rules.Function;
import com.nedap.archie.rules.Operator;
import com.nedap.archie.rules.RuleElement;

import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Sets some values that are not directly in ADL or ODIN, such as original language in terminology, etc.
 */
public class ArchetypeParsePostProcessor {

    public static void fixArchetype(Archetype archetype) {
        if (archetype.getTerminology() != null) {
            ArchetypeTerminology terminology = archetype.getTerminology();
            //codes are in model, but do not appear in odin. Set them here
            fillArchetypeTermCodes(terminology.getTermDefinitions());
            fillArchetypeTermCodes(terminology.getTerminologyExtracts());
            archetype.getTerminology().setConceptCode(archetype.getDefinition().getNodeId());
            String originalLanguage = null;
            if (archetype.getOriginalLanguage() != null) {
                originalLanguage = archetype.getOriginalLanguage().getCodeString();
            }
            archetype.getTerminology().setOriginalLanguage(originalLanguage);
        }
        fixDefinitionAndRules(archetype, false);
        if (archetype instanceof Template) {
            for (TemplateOverlay overlay : ((Template) archetype).getTemplateOverlays()) {
                fixDefinitionAndRules(overlay, true);
            }
        }
    }

    /**
     * Sets the parents in the definition (not needed for template overlays) and the primitive node ids
     * (id9999/at9999) that cannot be derived:
     * <ul>
     *     <li>Primitive objects in the definition get their node id from their archetype, through their parents.</li>
     *     <li>Primitive objects in slot assertions and rules have no parent. In at-coded archetypes and template
     *     overlays they get at9999 here. In id-coded ones nothing is needed, because id9999 is the default.</li>
     * </ul>
     */
    private static void fixDefinitionAndRules(Archetype archetype, boolean isTemplateOverlay) {
        boolean atCoded = AOMUtils.isAtCoded(archetype);
        if (archetype.getDefinition() == null || (isTemplateOverlay && !atCoded)) {
            return;
        }
        String primitiveNodeId = AOMUtils.getPrimitiveNodeId(archetype);
        Stack<CObject> workList = new Stack<>();
        workList.add(archetype.getDefinition());
        while (!workList.empty()) {
            CObject cObject = workList.pop();
            if (atCoded && cObject instanceof ArchetypeSlot) {
                ArchetypeSlot slot = (ArchetypeSlot) cObject;
                setPrimitiveNodeIds(slot.getIncludes(), primitiveNodeId);
                setPrimitiveNodeIds(slot.getExcludes(), primitiveNodeId);
            }
            if (isTemplateOverlay) {
                for (CAttribute attribute : cObject.getAttributes()) {
                    workList.addAll(attribute.getChildren());
                }
            } else {
                setParents(cObject, workList);
            }
        }
        if (atCoded && archetype.getRules() != null) {
            setPrimitiveNodeIds(archetype.getRules().getRules(), primitiveNodeId);
        }
    }

    private static void setPrimitiveNodeIds(List<? extends RuleElement> elements, String primitiveNodeId) {
        if (elements != null) {
            for (RuleElement element : elements) {
                setPrimitiveNodeIds(element, primitiveNodeId);
            }
        }
    }

    private static void setPrimitiveNodeIds(RuleElement element, String primitiveNodeId) {
        if (element instanceof Constraint) {
            CPrimitiveObject<?, ?> item = ((Constraint<?>) element).getItem();
            if (item != null) {
                item.setNodeId(primitiveNodeId);
            }
        } else if (element instanceof Operator) {
            setPrimitiveNodeIds(((Operator) element).getOperands(), primitiveNodeId);
        } else if (element instanceof Function) {
            setPrimitiveNodeIds(((Function) element).getArguments(), primitiveNodeId);
        } else if (element instanceof Assertion) {
            setPrimitiveNodeIds(((Assertion) element).getExpression(), primitiveNodeId);
            setPrimitiveNodeIds(((Assertion) element).getVariables(), primitiveNodeId);
        } else if (element instanceof ExpressionVariable) {
            setPrimitiveNodeIds(((ExpressionVariable) element).getExpression(), primitiveNodeId);
        }
    }

    private static void setParents(CObject cObject, Stack<CObject> workList) {
        for (CAttribute attribute : cObject.getAttributes()) {

            attribute.setParent(cObject);
            for (CObject child : attribute.getChildren()) {
                child.setParent(attribute);
            }
            workList.addAll(attribute.getChildren());
        }
        if (cObject instanceof CComplexObject) {
            //TODO: fix tuple so it has proper multiple references to the separate structures, instead of a complete duplication of data in json
            CComplexObject cComplexObject = (CComplexObject) cObject;
            for (CAttributeTuple tuple : cComplexObject.getAttributeTuples()) {
                for (CAttribute attribute : tuple.getMembers()) {
                    attribute.setSocParent(tuple);
                    attribute.setParent(cObject);
                    workList.addAll(attribute.getChildren());
                    cComplexObject.replaceAttribute(attribute);
                }
                for (CPrimitiveTuple primitiveTuple : tuple.getTuples()) {
                    int index = 0;
                    for (CPrimitiveObject<?, ?> object : primitiveTuple.getMembers()) {
                        if (index < tuple.getMembers().size()) {
                            CAttribute attribute = tuple.getMember(index);
                            object.setSocParent(primitiveTuple);
                            object.setParent(attribute);
                            index++;
                        }

                    }
                }
            }

        }
    }

    private static void fillArchetypeTermCodes(Map<String, Map<String, ArchetypeTerm>> termSet) {
        if (termSet != null) {
            for (Map<String, ArchetypeTerm> language : termSet.values()) {
                for (String term : language.keySet()) {
                    language.get(term).setCode(term);
                }
            }
        }
    }
}
