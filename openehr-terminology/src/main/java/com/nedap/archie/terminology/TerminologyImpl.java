package com.nedap.archie.terminology;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class TerminologyImpl {
    private String terminologyId;
    private String issuer;
    private String openEhrId;
    private Map<String, MultiLanguageTerm> termsById = new LinkedHashMap<>();

    /** for json creation */
    public TerminologyImpl() {
    }

    public TerminologyImpl(String issuer, String openEhrId, String terminologyId) {
        this.issuer = issuer;
        this.openEhrId = openEhrId;
        this.terminologyId = terminologyId;
    }

    public String getTerminologyId() {
        return terminologyId;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getOpenEhrId() {
        return openEhrId;
    }

    public Map<String, MultiLanguageTerm> getTermsById() {
        return termsById;
    }

    public TermCode getTermCode(String code, String language) {
        MultiLanguageTerm multiLanguageTerm = this.termsById.get(code);
        if(multiLanguageTerm != null) {
            return multiLanguageTerm.getTermCodesByLanguage().get(language);
        }
        return null;
    }

    public MultiLanguageTerm getMultiLanguageTerm(String code) {
        return this.termsById.get(code);
    }

    public List<TermCode> getAllTermsForLanguage(String language) {
        return getTermsById().values().stream()
                .map(a -> a.getTermCodesByLanguage().get(language))
                .filter(t -> t != null)
                .collect(Collectors.toList());
    }

    public MultiLanguageTerm getOrCreateTermSet(String id) {
        MultiLanguageTerm multiLanguageTerm = termsById.get(id);
        if(multiLanguageTerm == null) {
            multiLanguageTerm = new MultiLanguageTerm(terminologyId, id);
            termsById.put(id, multiLanguageTerm);
        }
        return multiLanguageTerm;
    }

}

