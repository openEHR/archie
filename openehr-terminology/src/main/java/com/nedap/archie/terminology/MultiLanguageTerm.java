package com.nedap.archie.terminology;

import java.util.LinkedHashMap;
import java.util.Map;

class MultiLanguageTerm {
    private String terminologyId;
    private String termId;
    private Map<String, TermCodeImpl> termCodesByLanguage = new LinkedHashMap<>();

    /** for json parsing only*/
    public MultiLanguageTerm() {

    }

    public MultiLanguageTerm(String terminologyId, String termId) {
        this.terminologyId = terminologyId;
        this.termId = termId;
    }

    public String getTerminologyId() {
        return terminologyId;
    }

    public String getTermId() {
        return termId;
    }

    public Map<String, TermCodeImpl> getTermCodesByLanguage() {
        return termCodesByLanguage;
    }

    public void addCode(TermCodeImpl code) {
        TermCode termCode = termCodesByLanguage.get(code.getLanguage());
        if(termCode != null) {
            //sometimes terms occur twice. They mean the same, but are in two groups.
            //todo: properly implement groups
            termCode.getGroupIds().addAll(code.getGroupIds());
        } else {
            termCodesByLanguage.put(code.getLanguage(), code);
        }
    }

}
