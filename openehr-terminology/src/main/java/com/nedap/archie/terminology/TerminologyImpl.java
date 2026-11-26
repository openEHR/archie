package com.nedap.archie.terminology;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public Stream<TermCodeImpl> streamAllTermsForLanguage(String language) {
        return getTermsById().values().stream()
                .map(a -> a.getTermCodesByLanguage().get(language))
                .filter(Objects::nonNull);
    }

    public List<TermCode> getAllTermsForLanguage(String language) {
        return streamAllTermsForLanguage(language)
                .collect(Collectors.toList());
    }

    public MultiLanguageTerm getOrCreateTermSet(String id) {
        return termsById.computeIfAbsent(id, d -> new MultiLanguageTerm(terminologyId, d));
    }

}

