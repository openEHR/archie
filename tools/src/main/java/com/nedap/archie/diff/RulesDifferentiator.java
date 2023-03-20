package com.nedap.archie.diff;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.rules.RuleStatement;

import java.util.ArrayList;
import java.util.List;

public class RulesDifferentiator {

    public void differentiate(Archetype result, Archetype flatParent) {
        if (flatParent.getRules() == null
                || flatParent.getRules().getRules() == null
                || flatParent.getRules().getRules().size() == 0) {
            // Parent has no rules, all rules can stay in the child archetype
            return;
        }

        if (result.getRules().getRules().size() == flatParent.getRules().getRules().size()) {
            // If the amount of rules is the same, can you assume there are no new rules in the child archetype?
            result.setRules(null);
            return;
        }

        List<RuleStatement> rulesToRemove = new ArrayList<>();
        for (RuleStatement rule : result.getRules().getRules()) {
            if (flatParent.getRules().getRules().contains(rule)) {
                rulesToRemove.add(rule);
            }
        }
        for (RuleStatement rule : rulesToRemove) {
            result.getRules().getRules().remove(rule);
        }
    }
}
