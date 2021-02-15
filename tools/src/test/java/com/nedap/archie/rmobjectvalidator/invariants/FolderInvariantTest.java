package com.nedap.archie.rmobjectvalidator.invariants;

import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.directory.Folder;
import org.junit.Test;



public class FolderInvariantTest {

    @Test
    public void valid() {
        InvariantTestUtil.assertValid(new Folder("id25", new DvText("folder name"), null, null, null));
    }

}
