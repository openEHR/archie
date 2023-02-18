package com.nedap.archie.rm.support.identification;

import org.junit.Test;

import static org.junit.Assert.*;

public class TerminologyIdTest {

    @Test
    public void createTerminologyIdWithNameAndVersionSuccessfully() {
        TerminologyId terminologyId = new TerminologyId("name", "version");
        assertEquals("name(version)", terminologyId.getValue());
        assertNotNull(terminologyId.name());
        assertEquals("name", terminologyId.name());
        assertNotNull(terminologyId.versionId());
        assertEquals("version", terminologyId.versionId());
    }

    @Test
    public void createTerminologyIdWithOnlyNameSuccessfully() {
        TerminologyId terminologyId = new TerminologyId("name");
        assertEquals("name", terminologyId.getValue());
        assertNotNull(terminologyId.name());
        assertEquals("name", terminologyId.name());
        assertNull(terminologyId.versionId());
    }

    @Test
    public void createTerminologyIdWithNoNameOrVersionSuccessfully() {
        TerminologyId terminologyId = new TerminologyId();
        assertNull(terminologyId.getValue());
        assertNull(terminologyId.name());
        assertNull(terminologyId.versionId());
    }

}
