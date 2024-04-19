package com.nedap.archie.rm.support.identification;

import org.junit.Test;
import org.openehr.rm.support.identification.TerminologyId;

import static org.junit.Assert.*;

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
