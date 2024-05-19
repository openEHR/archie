package com.nedap.archie.rm.datavalues;

import org.openehr.rm.datatypes.CodePhrase;
import org.openehr.rm.datavalues.encapsulated.DvParsable;
import org.openehr.rm.support.identification.TerminologyId;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DvParsableTest {

    @Test
    public void size() {
        DvParsable defaultCharsetDvParsable1 = new DvParsable(null, null, "something", "markdown");
        assertEquals(9, defaultCharsetDvParsable1.size()); //UTF-8 is the default charset and the sigma is two bytes in UTF-8
        DvParsable defaultCharsetDvParsable2 = new DvParsable(null, null, "Σomething", "markdown");
        assertEquals(10, defaultCharsetDvParsable2.size()); //UTF-8 is the default charset and the sigma is two bytes in UTF-8

        DvParsable utf8DvParsable = new DvParsable(new CodePhrase(new TerminologyId("IANA_character-sets"), "UTF-8"), null, "Σomething", "markdown");
        assertEquals(10, utf8DvParsable.size()); //the sigma is two bytes in UTF-8

        DvParsable utf16DvParsable = new DvParsable(new CodePhrase(new TerminologyId("IANA_character-sets"), "UTF-16"), null, "something", "markdown");
        assertEquals(20, utf16DvParsable.size()); //two bytes BOM, plus 9 characters * 2 bytes = 20

    }
}
