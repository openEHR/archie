package com.nedap.archie.terminology;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OpenEHRTerminologyAccessTest {

    private OpenEHRTerminologyAccess termAccess;

    @Before
    public void getInstance() {
        termAccess = OpenEHRTerminologyAccess.getInstance();//should not throw an exception from parsing
    }

    @After
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
        //System.out.println(json);
        try(InputStream stream = getClass().getResourceAsStream("/openEHR_RM/fullTermFile.json")) {
            String includedJson = IOUtils.toString(stream, StandardCharsets.UTF_8);
            assertEquals("XML and included terminology have gone out of sync. please regenerate json file!", json, includedJson);
        }
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