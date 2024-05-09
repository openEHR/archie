package com.nedap.archie.rules.evaluation;

import com.nedap.archie.rm.archetyped.Pathable;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rmobjectvalidator.ValidationConfiguration;

@Deprecated
public class ParsedRulesEvaluationBackwardsCompatibilityTest extends ParsedRulesEvaluationTest {
    @Override
    RuleEvaluation<Pathable> getRuleEvaluation() {
        return new RuleEvaluation<>(
                ArchieRMInfoLookup.getInstance(),
                new ValidationConfiguration.Builder().build(),
                archetype
        );
    }
}
