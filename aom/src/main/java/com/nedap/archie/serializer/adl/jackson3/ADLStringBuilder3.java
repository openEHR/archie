package com.nedap.archie.serializer.adl.jackson3;

import com.nedap.archie.serializer.adl.ADLStringBuilder;
import org.openehr.odin.jackson3.ODINMapper3;
import org.openehr.odin.jackson3.ODINPrettyPrinter3;

/**
 * Jackson 3 variant of {@link ADLStringBuilder}.
 * Identical behaviour, but uses ODINMapper3 for ODIN serialization instead of the Jackson 2 ODINMapper.
 */
public class ADLStringBuilder3 extends ADLStringBuilder {

    private final ODINMapper3 odinMapper;

    public ADLStringBuilder3() {
        odinMapper = (ODINMapper3) new ArchetypeODINMapperFactory3().createMapper();
    }

    @Override
    public ADLStringBuilder odin(Object structure) {
        try {
            String odin = odinMapper.writer()
                    .with(new ODINPrettyPrinter3(getIndentDepth()))
                    .writeValueAsString(structure).trim();
            append(odin).newline();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }
}
