package com.nedap.archie.template.betterjson.parser;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"referenceType"})
public interface CArchetypeRootMixin {

}
