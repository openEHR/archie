package com.nedap.archie.terminology;

import com.nedap.archie.terminology.openehr.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class OpenEHRTerminologyAccess implements TerminologyAccess {

    private static volatile OpenEHRTerminologyAccess instance;

    private Map<String, TerminologyImpl> terminologiesByOpenEHRId = new LinkedHashMap<>();
    private Map<String, TerminologyImpl> terminologiesByExternalId = new LinkedHashMap<>();

    private static final String[] resourceNames = {
            "/openEHR_RM/en/openehr_terminology.xml",
            "/openEHR_RM/ja/openehr_terminology.xml",
            "/openEHR_RM/pt/openehr_terminology.xml",
            "/openEHR_RM/openehr_external_terminologies.xml",
    };


    private OpenEHRTerminologyAccess() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Code.class, Codeset.class, Concept.class, Group.class, Terminology.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            for(String resourceName:resourceNames) {
                try (InputStream stream = getClass().getResourceAsStream(resourceName)) {
                    Terminology terminology = (Terminology) unmarshaller.unmarshal(stream);
                    for (Codeset codeSet : terminology.getCodeset()) {
                        TerminologyImpl impl = getOrCreateTerminologyById(codeSet.getIssuer(), codeSet.getOpenehrId(), codeSet.getExternalId());
                        for (Code code : codeSet.getCode()) {
                            MultiLanguageTerm multiLanguageTerm = impl.getOrCreateTermSet(code.getValue());
                            multiLanguageTerm.addCode(new TermCodeImpl(codeSet.getExternalId(), terminology.getLanguage(), code.getValue(), code.getDescription()));
                        }
                    }
                    for(Group group:terminology.getGroup()) {
                        //could be possible to move this up, but only useful if there are groups, so ok as is.
                        TerminologyImpl impl = getOrCreateTerminologyById("openehr", "openehr", terminology.getName());
                        for (Concept concept : group.getConcept()) {
                            MultiLanguageTerm multiLanguageTerm = impl.getOrCreateTermSet(concept.getId().toString());
                            multiLanguageTerm.addCode(new TermCodeImpl(terminology.getName(), terminology.getLanguage(), concept.getId().toString(), concept.getRubric(), group.getName(), group.getId()));
                        }
                    }

                }
            }

        } catch (JAXBException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private TerminologyImpl getOrCreateTerminologyById(String issuer, String openEhrId, String externalId) {
        TerminologyImpl terminology = terminologiesByExternalId.get(externalId);
        if(terminology == null) {
            terminology = new TerminologyImpl(issuer, openEhrId, externalId);
            terminologiesByExternalId.put(externalId, terminology);
            terminologiesByOpenEHRId.put(openEhrId, terminology);
        }
        return terminology;
    }

    public static OpenEHRTerminologyAccess getInstance() {
        if(instance == null) {
            createInstance();
        }
        return instance;
    }

    private static synchronized void createInstance() {
        if(instance == null) {
            instance = new OpenEHRTerminologyAccess();
        }
    }



    @Override
    public TermCode getTerm(String terminologyId, String code, String language) {
        TerminologyImpl terminology = terminologiesByExternalId.get(terminologyId);
        if(terminology != null) {
            return terminology.getTermCode(code, language);
        }
        return null;
    }

    @Override
    public List<TermCode> getTerms(String terminologyId, String language) {
        TerminologyImpl terminology = terminologiesByExternalId.get(terminologyId);
        if(terminology != null) {
            return terminology.getAllTermsForLanguage(language);
        }
        return Collections.emptyList();
    }

    private static Pattern openEHRTermIdPattern = Pattern.compile("http://openehr.org/id/(?<id>[0-9]+)");

    @Override
    public TermCode getTermByTerminologyURI(String uri, String language) {
        Matcher matcher = openEHRTermIdPattern.matcher(uri);
        if(matcher.matches()) {
            return getTerm("openehr", matcher.group("id"), language);
        }
        return null;
    }

    @Override
    public TermCode getTermByOpenEhrId(String terminologyId, String code, String language) {
        TerminologyImpl terminology = terminologiesByOpenEHRId.get(terminologyId);
        if(terminology != null) {
            return terminology.getTermCode(code, language);
        }
        return null;
    }

    @Override
    public List<TermCode> getTermsByOpenEhrId(String terminologyId, String language) {
        TerminologyImpl terminology = terminologiesByOpenEHRId.get(terminologyId);
        if(terminology != null) {
            return terminology.getAllTermsForLanguage(language);
        }
        return Collections.emptyList();
    }

    @Override
    public List<TermCode> getTermsByOpenEHRGroup(String groupId, String language) {
        //TODO: improve performance with a nice index
        TerminologyImpl openehr = terminologiesByExternalId.get("openehr");
        if(openehr == null) {
            return Collections.emptyList(); //should never happen
        }
        return openehr.getAllTermsForLanguage(language).stream()
                .filter(t -> t.getGroupId().equalsIgnoreCase(groupId))
                .collect(Collectors.toList());
    }

    @Override
    public TermCode getTermByOpenEHRGroup(String groupId, String language, String code) {
        //TODO: improve performance with a nice index
        TerminologyImpl openehr = terminologiesByExternalId.get("openehr");
        if(openehr == null) {
            return null; //should never happen
        }
        List<TermCode> codes = openehr.getAllTermsForLanguage(language).stream()
                .filter(t -> t.getGroupId().equalsIgnoreCase(groupId))
                .collect(Collectors.toList());
        return codes.stream().filter(c -> c.getCodeString().equalsIgnoreCase(code)).findFirst().orElse(null);
    }

    private static class TerminologyImpl {
        private String terminologyId;
        private String issuer;
        private String openEhrId;
        private Map<String, MultiLanguageTerm> termsById = new LinkedHashMap<>();

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

    private static class MultiLanguageTerm {
        private String terminologyId;
        private String termId;
        private Map<String, TermCode> termCodesByLanguage = new LinkedHashMap<>();

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

        public Map<String, TermCode> getTermCodesByLanguage() {
            return termCodesByLanguage;
        }

        public void addCode(TermCode code) {
            termCodesByLanguage.put(code.getLanguage(), code);
        }

    }
}


