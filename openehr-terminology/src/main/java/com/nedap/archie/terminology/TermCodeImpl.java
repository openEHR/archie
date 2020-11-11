package com.nedap.archie.terminology;

import com.nedap.archie.terminology.openehr.Code;

public class TermCodeImpl implements TermCode {

    private String terminologyId;
    private String language;
    private String codeString;
    private String description;
    private String groupName;

    public TermCodeImpl(String terminologyId, String language, String codeString, String description, String groupName) {
        this.terminologyId = terminologyId;
        this.language = language;
        this.description = description;
        if(description == null) {
            this.description = codeString;
        }
        this.codeString = codeString;
        this.groupName = groupName;
    }

    public TermCodeImpl(String terminologyId, String language, String codeString, String description) {
        this(terminologyId, language, codeString, description, null);
    }

    @Override
    public String getTerminologyId() {
        return terminologyId;
    }

    @Override
    public String getLanguage() {
        return language;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getCodeString() {
        return codeString;
    }

    @Override
    public String getGroupName() {
        return groupName;
    }
}
