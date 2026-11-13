package com.nedap.archie.terminology;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OpenEHRTerminologyAccessTest {

    private OpenEHRTerminologyAccess termAccess;

    @BeforeEach
    public void getInstance() {
        termAccess = OpenEHRTerminologyAccess.getInstance();//should not throw an exception from parsing
    }

    @AfterEach
    public void reset() {
        OpenEHRTerminologyAccess.READ_FROM_JSON = true;
        OpenEHRTerminologyAccess.instance = null;

    }
    @Test
    public void writeToJson() throws Exception {
        //be sure to parse from XML!
        OpenEHRTerminologyAccess.instance = null;
        OpenEHRTerminologyAccess.READ_FROM_JSON = false;
        OpenEHRTerminologyAccess ac = OpenEHRTerminologyAccess.getInstance();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        String json = mapper.writeValueAsString(ac);
        OpenEHRTerminologyAccess parsed = mapper.readValue(json, OpenEHRTerminologyAccess.class);

        assertTermCodesEqual(ac, parsed, a -> a.getTermsByOpenEhrId("countries", "en"));
        assertTermCodesEqual(ac, parsed, a -> a.getTermsByOpenEHRGroup("composition category", "en"));
        assertTermCodesEqual(ac, parsed, a -> a.getTerms("openehr", "en"));
        assertTermCodeEquals(ac, parsed, a -> a.getTermByTerminologyURI("http://openehr.org/id/124", "en"));
        assertTermCodeEquals(ac, parsed, a -> a.getTermByOpenEHRGroup("composition category", "en", "431"));
        assertTermCodeEquals(ac, parsed, a -> a.getTerm("openehr", "124", "en"));
        assertTermCodeEquals(ac, parsed, a -> a.getTerm("openehr", "268", "pt"));
        assertTermCodeEquals(ac, parsed, a -> a.getTermByOpenEhrId("countries", "NL", "en"));

        try (InputStream stream = getClass().getResourceAsStream("/openEHR_RM/fullTermFile.json")) {
            String includedJson = IOUtils.toString(stream, StandardCharsets.UTF_8);
            assertEquals(json, includedJson, "XML and included terminology have gone out of sync. please regenerate json file!");
        }
    }

    private static void assertTermCodesEqual(OpenEHRTerminologyAccess expected, OpenEHRTerminologyAccess actual,
                                             Function<OpenEHRTerminologyAccess, List<TermCode>> lookup) {
        List<TermCode> expectedValues = lookup.apply(expected);
        assertFalse(expectedValues.isEmpty());
        List<TermCode> actualValues = lookup.apply(actual);
        assertEquals(expectedValues.size(), actualValues.size());
        for (int i = 0; i < expectedValues.size(); i++) {
            assertTermCodeEquals(expectedValues.get(i), actualValues.get(i));
        }
    }

    private static void assertTermCodeEquals(OpenEHRTerminologyAccess expected, OpenEHRTerminologyAccess actual,
                                             Function<OpenEHRTerminologyAccess, TermCode> lookup) {
        assertTermCodeEquals(lookup.apply(expected), lookup.apply(actual));
    }

    private static void assertTermCodeEquals(TermCode expected, TermCode actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertEquals(expected.getTerminologyId(), actual.getTerminologyId());
        assertEquals(expected.getLanguage(), actual.getLanguage());
        assertEquals(expected.getCodeString(), actual.getCodeString());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getGroupName(), actual.getGroupName());
        assertEquals(expected.getGroupIds(), actual.getGroupIds());
    }

    @Test
    public void getCodesFromGroup() {
        List<TermCode> compositionCategories = termAccess.getTermsByOpenEHRGroup("composition category", "en");
        assertEquals(3, compositionCategories.size());
        for(TermCode termCode:compositionCategories) {
            assertEquals("composition category", termCode.getGroupName());
            assertEquals("en", termCode.getLanguage());
            assertEquals("openehr", termCode.getTerminologyId());
        }

        TermCode persistent = compositionCategories.get(0);
        TermCode episodic = compositionCategories.get(1);
        TermCode event = compositionCategories.get(2);
        assertEquals("431", persistent.getCodeString());
        assertEquals("451", episodic.getCodeString());
        assertEquals("433", event.getCodeString());
        assertEquals("persistent", persistent.getDescription());
        assertEquals("episodic", episodic.getDescription());
        assertEquals("event", event.getDescription());


    }

    @Test
    public void getCodesFromCodeSet() {
        TermCode mass = termAccess.getTerm("openehr", "124", "en");
        assertEquals("Mass", mass.getDescription());
        assertEquals("124", mass.getCodeString());
        assertEquals("openehr", mass.getTerminologyId());
        assertEquals("en", mass.getLanguage());

        assertEquals("median", termAccess.getTerm("openehr", "268", "en").getDescription());

        assertEquals("massa", termAccess.getTerm("openehr", "124", "pt").getDescription());
        assertEquals("mediana", termAccess.getTerm("openehr", "268", "pt").getDescription());
    }

    @Test
    public void getWithOpenEHRId() {
        TermCode termByOpenEhrId = termAccess.getTermByOpenEhrId("countries", "NL", "en");
        assertEquals("NETHERLANDS", termByOpenEhrId.getDescription());
        assertEquals("NL", termByOpenEhrId.getCodeString());
    }

    @Test
    public void getWithOpenEhrUri() {
        TermCode termByOpenEhrId = termAccess.getTermByTerminologyURI("http://openehr.org/id/124", "en");
        assertEquals("Mass", termByOpenEhrId.getDescription());
        assertEquals("124", termByOpenEhrId.getCodeString());
    }

    @Test
    public void getGroup() {
        List<TermCode> groupContent = termAccess.getTermsByOpenEHRGroup("composition category", "en");

        assertEquals("431", groupContent.get(0).getCodeString());
        assertEquals("persistent", groupContent.get(0).getDescription());
        assertEquals("451", groupContent.get(1).getCodeString());
        assertEquals("episodic", groupContent.get(1).getDescription());
        assertEquals("433", groupContent.get(2).getCodeString());
        assertEquals("event", groupContent.get(2).getDescription());

        groupContent.stream().forEach(t -> assertEquals("composition category", t.getGroupName()));
        groupContent.stream().forEach(t -> assertEquals("en", t.getLanguage()));
        groupContent.stream().forEach(t -> assertEquals("openehr", t.getTerminologyId()));
    }

}
