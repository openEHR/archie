package com.nedap.archie.json;

@com.fasterxml.jackson.databind.annotation.JsonSerialize(converter = RulesSectionToListConverter.class)
@tools.jackson.databind.annotation.JsonSerialize(converter = com.nedap.archie.json3.RulesSectionToListConverter.class)
public interface RulesSectionMixin {

}
