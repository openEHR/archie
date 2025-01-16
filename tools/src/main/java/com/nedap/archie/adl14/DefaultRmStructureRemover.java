package com.nedap.archie.adl14;

import com.nedap.archie.adlparser.modelconstraints.BMMConstraintImposer;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CAttributeTuple;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.base.MultiplicityInterval;
import com.nedap.archie.rminfo.MetaModels;

import java.util.ArrayList;
import java.util.List;

/**
 * Removes anything in an archetype that is exactly the default of the reference model. That includes:
 * <ul>
 *  <li>attribute cardinality and existence that is exactly the attribute default</li>
 *  <li>C_OBJECT occurrences that are exactly the default of the C_OBJECT</li>
 *  <li>Optionally removed attributes that are empty (without existence, cardinality or children) after removing default cardinality and existence</li>
 * </ul>
 */
public class DefaultRmStructureRemover {

    private final MetaModels metaModels;
    private final boolean removeEmptyAttributes;
    private BMMConstraintImposer constraintImposer;

    /**
     * Construct a DefaultRmStructureRemover
     *
     * @param metaModels            the metamodels containing metamodel information for the preset archetypes
     * @param removeEmptyAttributes if true, will remove empty attributes. If false, will not
     */
    public DefaultRmStructureRemover(MetaModels metaModels, boolean removeEmptyAttributes) {
        this.metaModels = metaModels;
        this.removeEmptyAttributes = removeEmptyAttributes;
    }

    public void removeRMDefaults(Archetype archetype) {
        this.metaModels.selectModel(archetype);
        if (metaModels.getSelectedModel() == null) {
            throw new IllegalArgumentException("cannot find model for argument, so cannot remove default multiplicity");
        }
        this.constraintImposer = new BMMConstraintImposer(metaModels.getSelectedBmmModel());
        removeRMDefaults(archetype.getDefinition());
    }

    private void removeRMDefaults(CObject object) {
        // Remove occurrences if they are equal to the default occurrences of the object
        if (object.getOccurrences() != null) {
            MultiplicityInterval defaultRMOccurrences = object.getDefaultRMOccurrences(metaModels::referenceModelPropMultiplicity);
            if (defaultRMOccurrences.equals(object.getOccurrences())) {
                object.setOccurrences(null);
            }
        }

        // Remove default multiplicities of attributes
        if (object instanceof CComplexObject) {
            CComplexObject complexObject = (CComplexObject) object;
            List<CAttribute> attributesToRemove = new ArrayList<>();
            for (CAttribute attribute : object.getAttributes()) {
                removeMultiplicities(attribute);
                if (removeEmptyAttributes) {
                    // Remove all empty attributes. They are 'attribute matches {*}' in ADL 1.4, and should not be present in ADL 2
                    if (attribute.getCardinality() == null && attribute.getExistence() == null && (attribute.getChildren() == null || attribute.getChildren().isEmpty())) {
                        if (!isInTuple(complexObject, attribute)) {
                            attributesToRemove.add(attribute);
                        }
                    }
                }
            }
            for (CAttribute attributeToRemove : attributesToRemove) {
                complexObject.removeAttribute(attributeToRemove);
            }
        }
    }

    private void removeMultiplicities(CAttribute attribute) {
        // Remove existence and cardinality if they are equal to the default existence and cardinality of the attribute
        CAttribute defaultAttribute = constraintImposer.getDefaultAttribute(attribute.getParent().getRmTypeName(), attribute.getRmAttributeName());
        if (attribute.getExistence() != null) {
            if (defaultAttribute != null && defaultAttribute.getExistence() != null && defaultAttribute.getExistence().equals(attribute.getExistence())) {
                attribute.setExistence(null);
            }
        }
        if (attribute.getCardinality() != null) {
            if (defaultAttribute != null && defaultAttribute.getCardinality() != null) {
                if (defaultAttribute.getCardinality().equals(attribute.getCardinality())) {
                    attribute.setCardinality(null);
                }
            }
        }

        // Remove default occurrences of child objects
        for (CObject child : attribute.getChildren()) {
            removeRMDefaults(child);
        }
    }

    private boolean isInTuple(CComplexObject complexObject, CAttribute attribute) {
        if (complexObject.getAttributeTuples() == null) {
            return false;
        }
        for (CAttributeTuple tuple : complexObject.getAttributeTuples()) {
            if (tuple.getMember(attribute.getRmAttributeName()) != null) {
                return true;
            }
        }
        return false;
    }
}
