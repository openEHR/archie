package com.nedap.archie.serializer.adl.rules;

import com.nedap.archie.rules.RuleElement;
import com.nedap.archie.serializer.adl.ADLRulesSerializer;
import com.nedap.archie.serializer.adl.ADLStringBuilder;

/**
 * Created by pieter.bos on 15/06/16.
 */
public abstract class RuleElementSerializer<T extends RuleElement> {
    protected final ADLRulesSerializer serializer;
    protected final ADLStringBuilder builder;

    public RuleElementSerializer(ADLRulesSerializer serializer) {
        this.serializer = serializer;
        this.builder = serializer.getBuilder();
    }

    abstract public void serialize(T ruleElement);

    public String getSimpleCommentText(T ruleElement) {
        return null;
    }

    public boolean isEmpty(T ruleElement) {
        return false;
    }

}
