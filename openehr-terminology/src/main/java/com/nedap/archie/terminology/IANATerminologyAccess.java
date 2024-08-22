package com.nedap.archie.terminology;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IANATerminologyAccess implements TerminologyAccess {
    private static final Pattern IANATermIdPattern = Pattern.compile("https://www.w3.org/ns/iana/media-types/(?<type>[A-Za-z]+)/(?<subtype>[A-Za-z]+)#Resource");

    @Override
    public TermCode getTerm(String terminologyId, String code, String language) {
        return null;
    }

    @Override
    public List<TermCode> getTerms(String terminologyId, String language) {
        return Collections.emptyList();
    }

    @Override
    public TermCode getTermByTerminologyURI(String uri, String language) {
        return null;
    }

    @Override
    public TermCode getTermByOpenEhrId(String terminologyId, String code, String language) {
        return null;
    }

    @Override
    public List<TermCode> getTermsByOpenEhrId(String terminologyId, String language) {
        return Collections.emptyList();
    }

    @Override
    public List<TermCode> getTermsByOpenEHRGroup(String groupId, String language) {
        return Collections.emptyList();
    }

    @Override
    public TermCode getTermByOpenEHRGroup(String groupId, String language, String code) {
        return null;
    }

    public static String parseTerminologyURI(String uri) {
        Matcher matcher = IANATermIdPattern.matcher(uri);
        if(matcher.matches()) {
            String type = matcher.group("type");
            String subType = matcher.group("subtype");
            return type + "/" + subType;
        }
        return null;
    }
}
