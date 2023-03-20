package com.nedap.archie.rules;

import java.util.Objects;

/**
 * Temporary placeholder for rules.
 * Created by pieter.bos on 15/10/15.
 */
public class RuleStatement extends RuleElement {

    private String ruleContent;


    public String getRuleContent() {
        return ruleContent;
    }

    public void setRuleContent(String ruleContent) {
        this.ruleContent = ruleContent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RuleStatement)) return false;
        if (!super.equals(o)) return false;
        RuleStatement that = (RuleStatement) o;
        return Objects.equals(ruleContent, that.ruleContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ruleContent);
    }
}
