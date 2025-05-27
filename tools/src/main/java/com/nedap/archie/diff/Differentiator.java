package com.nedap.archie.diff;

import com.nedap.archie.adl14.DefaultRmStructureRemover;
import com.nedap.archie.adlparser.modelconstraints.BMMConstraintImposer;
import com.nedap.archie.adlparser.modelconstraints.ModelConstraintImposer;
import com.nedap.archie.adlparser.modelconstraints.ReflectionConstraintImposer;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.rminfo.MetaModel;
import com.nedap.archie.rminfo.MetaModelProvider;
import com.nedap.archie.rminfo.MetaModels;

public class Differentiator {

    private final MetaModelProvider metaModelProvider;

    /**
     * @deprecated Use {@link #Differentiator(MetaModelProvider)} instead.
     */
    @Deprecated
    public Differentiator(MetaModels metaModels) {
        this((MetaModelProvider) metaModels);
    }

    public Differentiator(MetaModelProvider metaModelProvider) {
        this.metaModelProvider = metaModelProvider;
    }

    public Archetype differentiate(Archetype flatChild, Archetype flatParent) {
        return differentiate(flatChild, flatParent, true);
    }

    public Archetype differentiate(Archetype flatChild, Archetype flatParent, boolean addSiblingOrder) {
        MetaModel metaModel = metaModelProvider.selectAndGetMetaModel(flatChild);
        ModelConstraintImposer constraintImposer;
        if(metaModel.getBmmModel() != null) {
            constraintImposer = new BMMConstraintImposer(metaModel.getBmmModel());
        } else {
            constraintImposer = new ReflectionConstraintImposer(metaModel.getModelInfoLookup());
        }
        Archetype result = flatChild.clone();
        UnconstrainedIntervalRemover.removeUnconstrainedIntervals(result);

        if(addSiblingOrder) {
            new LCSOrderingDiff(metaModel).addSiblingOrder(result, flatChild, flatParent);
        }
        new ConstraintDifferentiator(constraintImposer, flatParent).removeUnspecializedConstraints(result, flatParent);
        new AnnotationDifferentiator().differentiate(result, flatParent);

        new DifferentialPathGenerator().replace(result);
        new TerminologyDifferentiator().differentiate(result);

        new DefaultRmStructureRemover(metaModelProvider, false).removeRMDefaults(result);
        result.setDifferential(true);

        return result;
    }


}
