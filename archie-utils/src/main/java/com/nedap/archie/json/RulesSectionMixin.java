package com.nedap.archie.json;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(converter = RulesSectionToListConverter.class)
public interface RulesSectionMixin {

}
