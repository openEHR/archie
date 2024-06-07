package com.nedap.archie.json.flat;

import com.nedap.archie.aom.*;
import com.nedap.archie.aom.utils.ArchetypeParsePostProcesser;
import com.nedap.archie.json.ArchieJacksonConfiguration;
import com.nedap.archie.openehr.serialisation.json.OpenEhrRmJacksonUtil;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertNotNull;

public class ArchetypeParsePostProcessorTest {
    @Test
    public void setTupleParents() throws Exception {
        ArchieJacksonConfiguration config = ArchieJacksonConfiguration.createConfigForJavascriptUsage();
        try(InputStream stream = getClass().getResourceAsStream("/com/nedap/archie/json/snaq_rc_opt.js")) {
            OperationalTemplate template = OpenEhrRmJacksonUtil.getObjectMapper(config).readValue(stream, OperationalTemplate.class);
            ArchetypeParsePostProcesser.fixArchetype(template);
            CComplexObject dvOrdinal = template.itemAtPath("/content[id0.0.100.1]/data[id2]/events[id3]/data[id4]/items[id15]/value[id25]");
            CAttributeTuple tuple = dvOrdinal.getAttributeTuples().get(0);
            for(CAttribute tupleMember:tuple.getMembers()) {
                assertNotNull(tupleMember.getArchetype());
            }
            for(CPrimitiveTuple primitiveTuple:tuple.getTuples()) {
                for(CPrimitiveObject primitiveObject:primitiveTuple.getMembers()) {
                    assertNotNull(primitiveObject.getArchetype());
                }
            }
        }
    }

}
