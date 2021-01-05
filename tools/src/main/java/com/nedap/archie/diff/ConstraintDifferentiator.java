package com.nedap.archie.diff;

import com.nedap.archie.adlparser.modelconstraints.ModelConstraintImposer;
import com.nedap.archie.aom.*;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.base.Cardinality;
import com.nedap.archie.base.MultiplicityInterval;
import com.nedap.archie.base.terminology.TerminologyCode;
import com.nedap.archie.query.ComplexObjectProxyReplacement;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConstraintDifferentiator {

    private final Archetype flatParent;
    private final ModelConstraintImposer constraintImposer;
    private boolean specializeDifferentTerm = false;

    ConstraintDifferentiator(ModelConstraintImposer constraintImposer, Archetype flatParent) {
        this.flatParent = flatParent;
        this.constraintImposer = constraintImposer;
    }

    public void removeUnspecializedConstraints(Archetype result, Archetype flatParent) {
        removeUnspecializedDefinition(result.getDefinition(), flatParent.getDefinition());

    }

    // remove all attribute constraints (cardinality, existence) that are default
    public void removeUnspecializedDefinition(CObject cObject, @Nullable CObject cObjectInParent) {

        if(cObject instanceof CComplexObject) {
            CComplexObject cComplexObject = (CComplexObject) cObject;

            removeUnspecializedAttributeTuples(cObjectInParent, cComplexObject);
            removeUnspecializedAttributes(cComplexObject, cObjectInParent);

            removeUnspecializedOccurrences(cObject, cObjectInParent);

        }
    }

    public void removeUnspecializedAttributes(CComplexObject cComplexObject, @Nullable CObject cObjectInParent) {
        if(cObjectInParent instanceof CComplexObjectProxy) {
            ComplexObjectProxyReplacement replacement = ComplexObjectProxyReplacement.getComplexObjectProxyReplacement((CComplexObjectProxy) cObjectInParent);
            cObjectInParent = replacement.getReplacement();
        }
        List<CAttribute> attributesToRemove = new ArrayList<>();
        for(CAttribute attribute:cComplexObject.getAttributes()) {
            //since it is flat, we can ignore differential paths here
            if(attribute.getDifferentialPath() != null) {
                throw new IllegalArgumentException("cannot handle differential paths in the differentiator - supply flattened archetype please");
            }
            if(attribute.getSocParent() != null) {
                //part of a tuple. processed by tuples already
            } else {
                CAttribute attributeInParent = cObjectInParent == null ? null : cObjectInParent.getAttribute(attribute.getRmAttributeName());
                removeUnspecializedCObjects(attribute, attributeInParent);
                removeUnspecializedCardinality(attribute, attributeInParent);
                removeUnspecializedExistence(attribute, attributeInParent);
                removeDefaultCardinalityAndExistence(cComplexObject, attribute, attributeInParent);

                if(shouldRemoveAttribute(attribute, attributeInParent)) {
                    attributesToRemove.add(attribute);
                }
            }
        }

        for(CAttribute attribute:attributesToRemove) {
            cComplexObject.removeAttribute(attribute);
        }
    }

    private void removeUnspecializedExistence(CAttribute attribute, CAttribute attributeInParent) {
        if(attribute.getExistence() == null || attributeInParent == null || attributeInParent.getExistence() == null) {
            return;
        }
        if(attribute.getExistence().equals(attributeInParent.getExistence())) {
            attribute.setExistence(null);
        }
    }

    public void removeUnspecializedCardinality(CAttribute attribute, CAttribute attributeInParent) {
        if(attribute.getCardinality() == null || attributeInParent == null || attributeInParent.getCardinality() == null) {
            return;
        }
        if(attribute.getCardinality().equals(attributeInParent.getCardinality())) {
            attribute.setCardinality(null);
        }

    }

    public void removeUnspecializedCObjects(CAttribute attribute, CAttribute attributeInParent) {
        SiblingOrder anchor = null;
        List<CObject> cObjectsToRemove = new ArrayList<>();
        for (CObject childCObject : attribute.getChildren()) {
            if(childCObject.getSiblingOrder() != null) {
                anchor = childCObject.getSiblingOrder();
            }
            @Nullable
            CObject childCObjectInParent = findCObject(attributeInParent, childCObject);
            removeUnspecializedDefinition(childCObject, childCObjectInParent);
            removeUnspecializedOccurrences(childCObject, childCObjectInParent);
            if(shouldRemoveUnspecializedCObject(childCObject, childCObjectInParent, anchor)) {
                cObjectsToRemove.add(childCObject);
            }
        }
        for(CObject toRemove:cObjectsToRemove) {
            toRemove.getParent().removeChild(toRemove);
        }
    }

    public void removeUnspecializedAttributeTuples(@Nullable CObject cObjectInParent, CComplexObject cComplexObject) {
        List<CAttributeTuple> attributeTuplesToRemove = new ArrayList<>();
        for (CAttributeTuple tuple : cComplexObject.getAttributeTuples()) {
            if (shouldRemoveUnspecializedTuple(tuple, (CComplexObject) cObjectInParent)) {
                attributeTuplesToRemove.add(tuple);
            }
        }
        for (CAttributeTuple tuple : attributeTuplesToRemove) {
            cComplexObject.removeAttributeTuple(tuple.getMemberNames());
        }
    }

    private boolean shouldRemoveUnspecializedTuple(CAttributeTuple tuple, CComplexObject cObjectInParent) {
        if(cObjectInParent == null) {
            return false;
        }
        int indexOfMatchingAttributeTuple = cObjectInParent.getIndexOfMatchingAttributeTuple(tuple.getMemberNames());
        if(indexOfMatchingAttributeTuple < 0) {
            //it does not exist in parent, so keep it in diff
            return false;
        }
        CAttributeTuple tupleInParent = cObjectInParent.getAttributeTuples().get(indexOfMatchingAttributeTuple);
        if(tuple.getTuples().size() != tupleInParent.getTuples().size()) {
            return false; //different tuples. keep in diff.
        }
        for(int i = 0; i < tuple.getTuples().size(); i++) {
            CPrimitiveTuple primitiveTuple = tuple.getTuples().get(i);
            CPrimitiveTuple primitiveTupleInParent = tupleInParent.getTuples().get(i);
            for(int j = 0; j < primitiveTuple.getMembers().size(); j++) {
                CPrimitiveObject member = primitiveTuple.getMember(j);
                CPrimitiveObject memberInParent = primitiveTupleInParent.getMember(j);
                if(!PrimitiveObjectEqualsChecker.isEqual(member, memberInParent)) {
                    return false; //at least one difference, keep
                }
            }
        }
        //exactly the same tuples. can be removed
        return true;

    }

    private boolean shouldRemoveAttribute(CAttribute attribute, CAttribute attributeInParent) {
        return attributeInParent != null
                && (attribute.getChildren().isEmpty()
                && attribute.getCardinality() == null
                && attribute.getExistence() == null);
    }

    private boolean shouldRemoveUnspecializedCObject(CObject childCObject, CObject childCObjectInParent, SiblingOrder anchor) {
        if(anchor != null) {
            //once a sibling order has been set, all elements after that should remain or it will mess up the anchoring
            return false;
        }
        //since all occurrences and child objects have been removed if possible, if it has any left, keep this here
        if(childCObject.getOccurrences() != null
                || childCObjectInParent == null
                || childCObject.getAttributes().size() > 0
                || childCObject.getSiblingOrder() != null) {
            return false;
        }
        if(childCObject instanceof ArchetypeSlot && childCObjectInParent instanceof ArchetypeSlot) {
            ArchetypeSlot slot = (ArchetypeSlot) childCObject;
            ArchetypeSlot parentSlot = (ArchetypeSlot) childCObjectInParent;
            if(slot.isClosed() != parentSlot.isClosed()) {
                return false;
            }
        }
        if(childCObject instanceof CArchetypeRoot) {
            if(childCObjectInParent instanceof ArchetypeSlot) {
                return false;
            } else if(childCObjectInParent instanceof CArchetypeRoot) {
                if (!((CArchetypeRoot) childCObject).getArchetypeRef().equals(((CArchetypeRoot) childCObjectInParent).getArchetypeRef())) {
                    return false;
                }
            }
        }

        if(childCObject instanceof CPrimitiveObject) {

            if(PrimitiveObjectEqualsChecker.isEqual((CPrimitiveObject) childCObject, (CPrimitiveObject) childCObjectInParent)) {
                return true;
            }
        } else {
            //no children, no occurrences, child object is in parent. check specialization id
            if(childCObject.specialisationDepth() == childCObjectInParent.specialisationDepth()) {
                if(!specializeDifferentTerm) {
                    return true;
                } else {
                    Archetype archetype = childCObject.getArchetype();
                    TerminologyCode originalLanguage = archetype.getOriginalLanguage();
                    ArchetypeTerm term = archetype.getTerm(childCObject, originalLanguage.getCodeString());
                    ArchetypeTerm parentTerm = flatParent.getTerm(childCObjectInParent, originalLanguage.getCodeString());
                    if(term == null || parentTerm == null) {
                        //can't be handled, get rid of it
                        return true;
                    } else {
                        //delete if term is the same. Otherwise, it will be specialized later.
                        return Objects.equals(term.getText(), parentTerm.getText()) && Objects.equals(term.getDescription(), parentTerm.getDescription());
                    }
                }
            }
            return false;
        }
        return false;

    }

    private void removeUnspecializedOccurrences(CObject cObject, CObject cObjectInParent) {
        if(cObject.getOccurrences() == null || cObjectInParent == null || cObjectInParent.getOccurrences() == null) {
            return;
        }
        if(cObject.getOccurrences().equals(cObjectInParent.getOccurrences())) {
            cObject.setOccurrences(null);
        }
    }

    private CObject findCObject(CAttribute attributeInParent, CObject cObjectInChild) {
        if (attributeInParent == null) {
            return null;
        }
        if(cObjectInChild instanceof CPrimitiveObject) {
            int indexOfChildWithMatchingRmTypeName = attributeInParent.getIndexOfChildWithMatchingRmTypeName(cObjectInChild.getRmTypeName());
            if(indexOfChildWithMatchingRmTypeName > -1) {
                return attributeInParent.getChildren().get(indexOfChildWithMatchingRmTypeName);
            }
            return null;
        } else {
            String codeAtParentLevel = AOMUtils.codeAtLevel(cObjectInChild.getNodeId(), flatParent.specializationDepth());
            return attributeInParent.getChild(codeAtParentLevel);
        }
    }


    /**
     * Remove the default cardinality and existence, if they are the same as in the reference model
     */
    private void removeDefaultCardinalityAndExistence(CObject cObject, CAttribute attribute, CAttribute attributeInParent) {

        CAttribute defaultAttribute = constraintImposer.getDefaultAttribute(cObject.getRmTypeName(), attribute.getRmAttributeName());
        if(defaultAttribute != null) {
            //if it is null, it will be caught by the validator because the property does not exist
            if(attributeInParent != null && attributeInParent.getCardinality() != null) {
                //parent cardinality set, so even if it is default, do not remove cardinality because it would fall back to potentially not default parent cardinality.
                //It is removed by removeUnspecializedExistence instead
            }  else if (attribute.getCardinality() != null && defaultAttribute.getCardinality() != null) {
                Cardinality cardinality = attribute.getCardinality();
                Cardinality defaultCardinality = defaultAttribute.getCardinality();
                if (cardinality.getInterval().equals(defaultCardinality.getInterval()) && cardinality.isOrdered() == defaultCardinality.isOrdered() && cardinality.isUnique() == defaultCardinality.isUnique()) {
                    attribute.setCardinality(null);
                }
            }
            if(attributeInParent != null && attributeInParent.getExistence() != null) {
                //parent existence set, so even if it is default, do not remove existence because it would fall back to potentially not default parent existence.
                //It is removed by removeUnspecializedExistence instead
            } else if (attribute.getExistence() != null && defaultAttribute.getExistence() != null) {
                MultiplicityInterval existence = attribute.getExistence();
                MultiplicityInterval defaultExistence = defaultAttribute.getExistence();
                if(existence.equals(defaultExistence)) {
                    attribute.setExistence(null);
                }
            }
        }
    }

}
