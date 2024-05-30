package com.nedap.archie.rules.evaluation;

import com.nedap.archie.rm.archetyped.Pathable;
import com.nedap.archie.rm.composition.Composition;
import com.nedap.archie.rm.composition.Section;
import com.nedap.archie.rm.datastructures.Cluster;
import com.nedap.archie.rm.datastructures.ItemStructure;

public class PathsThatMustNotExistFixer {

    private final RuleEvaluation<?> ruleEvaluator;

    public PathsThatMustNotExistFixer(RuleEvaluation<?> evaluation) {
        this.ruleEvaluator = evaluation;
    }

    public void fixPathsThatMustNotExist(AssertionResult assertionResult) {
        assertionResult.getPathsThatMustNotExist().forEach(this::removePathThatMustNotExist);
    }

    private void removePathThatMustNotExist(String path) {
        ruleEvaluator.findList(path).forEach(match -> {
            if (match instanceof Pathable) {
                Pathable item = (Pathable) match;
                Pathable parent = item.getParent();
                if (parent instanceof ItemStructure) ((ItemStructure) parent).getItems().remove(item);
                else if (parent instanceof Cluster) ((Cluster) parent).getItems().remove(item);
                else if (parent instanceof Composition) ((Composition) parent).getContent().remove(item);
                else if (parent instanceof Section) ((Section) parent).getItems().remove(item);
            }
        });
        ruleEvaluator.refreshQueryContext();
    }
}
