package com.nedap.archie.rmobjectvalidator.invariants;

import org.openehr.rm.datavalues.DvText;
import org.openehr.rm.directory.Folder;
import org.junit.Test;



public class FolderInvariantTest {

    @Test
    public void valid() {
        InvariantTestUtil.assertValid(new Folder("id25", new DvText("folder name"), null, null, null));
    }

}
