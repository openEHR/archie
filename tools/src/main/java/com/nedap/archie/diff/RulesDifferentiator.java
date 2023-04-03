package com.nedap.archie.diff;

import com.nedap.archie.aom.Archetype;
import java.util.stream.Collectors;

public class RulesDifferentiator {

    /**
     * Removes rules from the result archetypes which are also present in the flatParent archetype
     *      - The differentiator does not take into account changing or deletion of rules in the flat archetype
     */
    public void differentiate(Archetype result, Archetype flatParent) {
        if (result.getRules() == null
                || result.getRules().getRules() == null
                || result.getRules().getRules().isEmpty()) {
            // There are no rules in the result archetype
            return;
        }

        if (flatParent.getRules() == null
                || flatParent.getRules().getRules() == null
                || flatParent.getRules().getRules().isEmpty()) {
            // Parent has no rules, all rules can stay in the child archetype
            return;
        }

        // For each rule in the result archetype, check if the exact rule is in the flat parent archetype
        // If it is, remove it from the result archetype
        result.getRules().getRules().removeAll(
                result.getRules().getRules().stream().filter(
                        rule -> flatParent.getRules().getRules().contains(rule)
                ).collect(Collectors.toList())
        );
    }
}
