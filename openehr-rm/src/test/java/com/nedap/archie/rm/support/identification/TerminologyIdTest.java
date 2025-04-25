package com.nedap.archie.rm.support.identification;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TerminologyIdTest {

    @Test
    public void createTerminologyIdWithNameAndVersionSuccessfully() {
        TerminologyId terminologyId = new TerminologyId("name", "version");
        assertEquals("name(version)", terminologyId.getValue());
        assertEquals("name", terminologyId.getName());
        assertEquals("version", terminologyId.getVersionId());
    }

    @Test
    public void createTerminologyIdWithOnlyNameSuccessfully() {
        TerminologyId terminologyId = new TerminologyId("name");
        assertEquals("name", terminologyId.getValue());
        assertEquals("name", terminologyId.getName());
        assertNull(terminologyId.getVersionId());
    }

    @Test
    public void createTerminologyIdWithNoNameOrVersionSuccessfully() {
        TerminologyId terminologyId = new TerminologyId();
        assertNull(terminologyId.getValue());
        assertNull(terminologyId.getName());
        assertNull(terminologyId.getVersionId());
    }

}
