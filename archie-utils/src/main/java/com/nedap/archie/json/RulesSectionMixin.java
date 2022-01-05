package com.nedap.archie.json;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(converter = RulesSectionToListConverter.class)
@JsonDeserialize(converter = ListToRulesSectionConverter.class)
public interface RulesSectionMixin {

}
