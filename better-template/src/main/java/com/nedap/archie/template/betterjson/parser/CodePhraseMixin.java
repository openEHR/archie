package com.nedap.archie.template.betterjson.parser;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.nedap.archie.rm.support.identification.TerminologyId;

public interface CodePhraseMixin {

    @JsonAlias("terminology_id")
    void setTerminologyId(TerminologyId id);

    @JsonAlias("code_string")
    void setCodeString(String codeString);

}
