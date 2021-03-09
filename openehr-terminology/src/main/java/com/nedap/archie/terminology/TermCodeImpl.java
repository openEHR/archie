package com.nedap.archie.terminology;

import com.nedap.archie.terminology.openehr.Code;

import java.util.HashSet;
import java.util.Set;

public class TermCodeImpl implements TermCode {

    private final String terminologyId;
    private final String language;
    private final String codeString;
    private final String description;
    private final String groupName;
    private final Set<String> groupIds;//TODO: still one group name. Change to multiple as well?

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
        this.groupIds = new HashSet<>();
        groupIds.add(groupId);
    }

    public TermCodeImpl(String terminologyId, String language, String codeString, String description) {
        this(terminologyId, language, codeString, description, null, null);
    }

    protected  void addGroupId(String groupId) {
        this.groupIds.add(groupId);
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
    public Set<String> getGroupIds() {
        return groupIds;
    }
}
