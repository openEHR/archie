package com.nedap.archie.adl14;

import com.nedap.archie.adlparser.modelconstraints.BMMConstraintImposer;
import com.nedap.archie.aom.*;
import com.nedap.archie.base.MultiplicityInterval;
import com.nedap.archie.rminfo.MetaModel;
import com.nedap.archie.rminfo.MetaModelProvider;
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

    private final MetaModelProvider metaModelProvider;
    private final boolean removeEmptyAttributes;

    private MetaModel metaModel;
    private BMMConstraintImposer constraintImposer;

    /**
     * Construct a DefaultRmStructureRemover that does not remove empty attributes
     * @param metaModels the metamodels containing metamodel information for the preset archetypes
     * @deprecated Use {@link #DefaultRmStructureRemover(MetaModelProvider)} instead.
     */
    @Deprecated
    public DefaultRmStructureRemover(MetaModels metaModels) {
        this(metaModels, false);
    }

    /**
     * Construct a DefaultRmStructureRemover that does not remove empty attributes
     * @param metaModelProvider the metamodel provider for the preset archetypes
     */
    public DefaultRmStructureRemover(MetaModelProvider metaModelProvider) {
        this(metaModelProvider, false);
    }

    /**
     * Construct a DefaultRmStructureRemover
     *
     * @param metaModels            the metamodels containing metamodel information for the preset archetypes
     * @param removeEmptyAttributes if true, will remove empty attributes. If false, will not
     * @deprecated Use {@link #DefaultRmStructureRemover(MetaModelProvider, boolean)} instead.
     */
    @Deprecated
    public DefaultRmStructureRemover(MetaModels metaModels, boolean removeEmptyAttributes) {
        this((MetaModelProvider) metaModels, removeEmptyAttributes);
    }

    /**
     * Construct a DefaultRmStructureRemover
     *
     * @param metaModelProvider     the metamodel provider for the preset archetypes
     * @param removeEmptyAttributes if true, will remove empty attributes. If false, will not
     */
    public DefaultRmStructureRemover(MetaModelProvider metaModelProvider, boolean removeEmptyAttributes) {
        this.metaModelProvider = metaModelProvider;
        this.removeEmptyAttributes = removeEmptyAttributes;
    }

    public void removeRMDefaults(Archetype archetype) {
        metaModel = this.metaModelProvider.selectAndGetMetaModel(archetype);
        this.constraintImposer = new BMMConstraintImposer(metaModel.getBmmModel());
        removeRMDefaults(archetype.getDefinition());
    }

    private void removeRMDefaults(CObject object) {
        // Remove occurrences if they are equal to the default occurrences of the object
        if (object.getOccurrences() != null) {
            MultiplicityInterval defaultRMOccurrences = object.getDefaultRMOccurrences(metaModel::referenceModelPropMultiplicity);
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
