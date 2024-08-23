package org.s2.rm.base.data_types.text;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.nedap.archie.rminfo.ReferenceModels;
import org.junit.Before;
import org.junit.Test;
import org.s2.rm.base.foundation_types.terminology.TerminologyCode;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;
import org.s2.rminfo.S2RmInfoLookup;
import org.s2.serialisation.json.S2RmJacksonUtil;
import org.s2.serialisation.json.S2RmObjectMapperProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CodedTextTest {
    @Test
    public void testEquals() {
        CodedText codedTextOne = new CodedText(new TerminologyTerm("icd10::123 preferred term", new TerminologyCode("icd10", "123")), "some screen text");
        CodedText codedTextTwo = new CodedText(new TerminologyTerm("icd10::123 preferred term", new TerminologyCode("icd10", "123")),"some screen text");
        CodedText codedTextThree = new CodedText(new TerminologyTerm("icd10::234 preferred term", new TerminologyCode("icd10", "234")), "some other screen text");

        assertEquals(codedTextOne, codedTextTwo);
        assertNotEquals(codedTextOne, codedTextThree);
    }

    @Test
    public void testToString() throws JsonProcessingException {
        ObjectMapper om = S2RmJacksonUtil.getObjectMapper();

        CodedText codedTextOne = new CodedText();
        String codedTextOneJson = om.writeValueAsString(codedTextOne);
        assertEquals("{ }", codedTextOneJson);

        CodedText codedTextTwo = new CodedText(new TerminologyTerm("icd10::123 preferred term", new TerminologyCode("icd10", "123")), "some screen text");
        String codedTextTwoJson = om.writeValueAsString(codedTextTwo);
        assertEquals("{\n" +
                "  \"text\" : \"some screen text\",\n" +
                "  \"term\" : {\n" +
                "    \"description\" : \"icd10::123 preferred term\",\n" +
                "    \"concept\" : {\n" +
                "      \"terminology_id\" : \"icd10\",\n" +
                "      \"code_string\" : \"123\"\n" +
                "    }\n" +
                "  }\n" +
                "}", codedTextTwoJson);
    }
}
