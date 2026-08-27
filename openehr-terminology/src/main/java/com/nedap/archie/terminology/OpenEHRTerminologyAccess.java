package com.nedap.archie.terminology;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nedap.archie.terminology.openehr.*;

import java.util.AbstractMap;
import java.util.Objects;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class OpenEHRTerminologyAccess implements TerminologyAccess {

    private static final Pattern openEHRTermIdPattern = Pattern.compile("http://openehr.org/id/(?<id>[0-9]+)");
    private static final Pattern IANATermIdPattern = Pattern.compile("https://www.w3.org/ns/iana/media-types/(?<type>.+)/(?<subtype>.+)#Resource");

    static volatile OpenEHRTerminologyAccess instance;
    static boolean READ_FROM_JSON = true;

    @JsonProperty
    private Map<String, TerminologyImpl> terminologiesByOpenEHRId = new LinkedHashMap<>();
    @JsonProperty
    private Map<String, TerminologyImpl> terminologiesByExternalId = new LinkedHashMap<>();

    @JsonIgnore
    private Map<GroupLanguageCode, TermCode> termCodeByGroupLanguageCode;

    private static final String[] resourceNames = {
            "/openEHR_RM/en/openehr_terminology.xml",
            "/openEHR_RM/es/openehr_terminology.xml",
            "/openEHR_RM/ja/openehr_terminology.xml",
            "/openEHR_RM/pt/openehr_terminology.xml",
            "/openEHR_RM/openehr_external_terminologies.xml",
    };



    private OpenEHRTerminologyAccess() {

    }

    private static OpenEHRTerminologyAccess parseFromJson() {
        try(InputStream stream = OpenEHRTerminologyAccess.class.getResourceAsStream("/openEHR_RM/fullTermFile.json")) {
            return new ObjectMapper().readValue(stream, OpenEHRTerminologyAccess.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void parseFromXml() {
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
            createInstance(READ_FROM_JSON);
        }
        return instance;
    }


    private static synchronized void createInstance(boolean fromJson) {
        if(instance == null) {
            OpenEHRTerminologyAccess newInstance;
            if(fromJson) {
                newInstance = parseFromJson();
            } else {
                newInstance = new OpenEHRTerminologyAccess();
                newInstance.parseFromXml();
            }
            newInstance.prepareCache();
            instance = newInstance;
        }
    }

    private void prepareCache() {
        TerminologyImpl openehr = this.terminologiesByExternalId.get("openehr");
        if (openehr == null) {
            //should never happen
            this.termCodeByGroupLanguageCode = Collections.emptyMap();

        } else {
            this.termCodeByGroupLanguageCode = openehr.getTermsById().values().stream()
                    //<<lang, TC>,MLT>
                    .flatMap(a -> a.getTermCodesByLanguage().entrySet().stream().map(e -> new AbstractMap.SimpleImmutableEntry<>(e, a)))
                    //<Group,<<Lang, TC>,MLT>>
                    .flatMap(t -> t.getKey().getValue().getGroupIds().stream().map(g -> new AbstractMap.SimpleImmutableEntry<>(g, t)))
                    .collect(Collectors.groupingBy(e ->  new GroupLanguageCode(e.getKey(), e.getValue().getKey().getKey(), e.getValue().getKey().getValue().getCodeString()),
                            Collectors.mapping(e -> e.getValue().getKey().getValue(),
                                    Collectors.reducing(null, (a, b) -> b))
                    ));
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

    public String parseIANATerminologyURI(String uri) {
        Matcher matcher = IANATermIdPattern.matcher(uri);
        if(matcher.matches()) {
            String type = matcher.group("type");
            String subType = matcher.group("subtype");
            return type + "/" + subType;
        }
        return null;
    }

    public String parseTerminologyURI(String uri) {
        Matcher matcher = openEHRTermIdPattern.matcher(uri);
        if(matcher.matches()) {
            return matcher.group("id");
        }
        return null;
    }

    @Override
    public TermCode getTermByTerminologyURI(String uri, String language) {
        String code = parseTerminologyURI(uri);
        if(code != null) {
            return getTerm("openehr", code, language);
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
        return openehr.streamAllTermsForLanguage(language)
                .filter(t -> t.getGroupIds().contains(groupId))
                .collect(Collectors.toList());
    }

    @Override
    public TermCode getTermByOpenEHRGroup(String groupId, String language, String code) {
        return termCodeByGroupLanguageCode.get(new GroupLanguageCode(groupId, language, code));
    }

    private static final class GroupLanguageCode {
        private final String group;
        private final String lang;
        private final String code;
        private final int hash;

        private GroupLanguageCode(String group, String lang, String code) {
            this.group = group;
            this.lang = lang;
            this.code = code;

            this.hash = 41 * (37 * (43 + Objects.hashCode(group)) + Objects.hashCode(lang)) + Objects.hashCode(code);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            GroupLanguageCode lgcKey = (GroupLanguageCode) o;
            return Objects.equals(hash, lgcKey.hash)
                    && Objects.equals(code, lgcKey.code)
                    && Objects.equals(group, lgcKey.group)
                    && Objects.equals(lang, lgcKey.lang);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}


