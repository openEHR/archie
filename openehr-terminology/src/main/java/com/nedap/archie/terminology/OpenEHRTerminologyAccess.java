package com.nedap.archie.terminology;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nedap.archie.terminology.openehr.*;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
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

    static volatile OpenEHRTerminologyAccess instance;

    static boolean READ_FROM_JSON = true;

    @JsonProperty
    private Map<String, TerminologyImpl> terminologiesByOpenEHRId = new LinkedHashMap<>();
    @JsonProperty
    private Map<String, TerminologyImpl> terminologiesByExternalId = new LinkedHashMap<>();

    private static final String[] resourceNames = {
            "/openEHR_RM/en/openehr_terminology.xml",
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
            if(fromJson) {
                instance = parseFromJson();
            } else {
                instance = new OpenEHRTerminologyAccess();
                instance.parseFromXml();
            }
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

    private static final Pattern openEHRTermIdPattern = Pattern.compile("http://openehr.org/id/(?<id>[0-9]+)");

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
        return openehr.getAllTermsForLanguage(language).stream()
                .filter(t -> t.getGroupIds().contains(groupId))
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
                .filter(t -> t.getGroupIds().contains(groupId))
                .collect(Collectors.toList());
        return codes.stream().filter(c -> c.getCodeString().equalsIgnoreCase(code)).findFirst().orElse(null);
    }

}


