package com.nedap.archie.terminology;

import com.nedap.archie.terminology.openehr.Code;

public class TermCodeImpl implements TermCode {

    private final String terminologyId;
    private final String language;
    private final String codeString;
    private final String description;
    private final String groupName;
    private final String groupId;

    public TermCodeImpl(String terminologyId, String language, String codeString, String description, String groupName, String groupId) {
        this.terminologyId = terminologyId;
        this.language = language;
        if(description == null) {
            this.description = codeString;
        } else {
            this.description = description;
        }
        this.codeString = codeString;
        this.groupName = groupName;
        this.groupId = groupId;
    }

    public TermCodeImpl(String terminologyId, String language, String codeString, String description) {
        this(terminologyId, language, codeString, description, null, null);
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

    @Override
    public String getGroupId() {
        return groupId;
    }
}
