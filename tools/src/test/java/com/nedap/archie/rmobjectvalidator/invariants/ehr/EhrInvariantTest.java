package com.nedap.archie.rmobjectvalidator.invariants.ehr;

import org.openehr.rm.datavalues.quantity.datetime.DvDateTime;
import org.openehr.rm.ehr.Ehr;
import org.openehr.rm.support.identification.HierObjectId;
import org.openehr.rm.support.identification.ObjectRef;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil.createExampleRef;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class EhrInvariantTest {

    @Test
    public void validEhr() {
        Ehr ehr = createValidEhr();
        InvariantTestUtil.assertValid(ehr);
    }



    @Test
    public void invalidContributions() {
        Ehr ehr = createValidEhr();
        ehr.setContributions(createExampleRefList("VERSIONED_COMPOSITION"));
        InvariantTestUtil.assertInvariantInvalid(ehr, "Contributions valid", "/");
    }

    @Test
    public void invalidEhrAccess() {
        Ehr ehr = createValidEhr();
        ehr.setEhrAccess(createExampleRef("VERSIONED_EHR_STATUS"));
        InvariantTestUtil.assertInvariantInvalid(ehr, "Ehr_access_valid", "/");
    }

    @Test
    public void invalidEhrStatus() {
        Ehr ehr = createValidEhr();
        ehr.setEhrStatus(createExampleRef("VERSIONED_EHR_ACCESS"));
        InvariantTestUtil.assertInvariantInvalid(ehr, "Ehr_status_valid", "/");
    }

    @Test
    public void invalidDirectory() {
        Ehr ehr = createValidEhr();
        ObjectRef directoryRef = createExampleRef("VERSIONED_UNKNOWN");
        ehr.setDirectory(directoryRef);
        ehr.setFolders(null);
        InvariantTestUtil.assertInvariantInvalid(ehr, "Directory_valid", "/");
    }

    @Test
    public void invalidCompositions() {
        Ehr ehr = createValidEhr();
        ehr.setCompositions(createExampleRefList("VERSIONED_NON_COMPOSITION"));
        InvariantTestUtil.assertInvariantInvalid(ehr, "Compositions_valid", "/");
    }

    @Test
    public void emptyDirectoryValid() {
        Ehr ehr = createValidEhr();
        ehr.setDirectory(null);
        ehr.setFolders(new ArrayList<>());
        InvariantTestUtil.assertValid(ehr);
    }

    @Test
    public void foldersDoesNotContainDirectory() {
        Ehr ehr = createValidEhr();
        ObjectRef directoryRef = createExampleRef("VERSIONED_FOLDER");
        ehr.setDirectory(directoryRef);
        ehr.setFolders(new ArrayList<>());
        InvariantTestUtil.assertInvariantInvalid(ehr, "Directory_in_folders", "/");
    }

    private Ehr createValidEhr() {
        Ehr ehr = new Ehr();
        ehr.setContributions(createExampleRefList("CONTRIBUTION"));
        ehr.setEhrAccess(createExampleRef("VERSIONED_EHR_ACCESS"));
        ehr.setEhrStatus(createExampleRef("VERSIONED_EHR_STATUS"));
        ObjectRef directoryRef = createExampleRef("VERSIONED_FOLDER");
        ehr.setDirectory(directoryRef);
        ehr.setFolders(Arrays.asList(directoryRef));
        ehr.setTimeCreated(new DvDateTime(LocalDateTime.now()));
        ehr.setSystemId(new HierObjectId("something"));
        ehr.setEhrId(new HierObjectId("something"));
        ehr.setCompositions(createExampleRefList("VERSIONED_COMPOSITION"));
        return ehr;
    }

    private List<ObjectRef<?>> createExampleRefList(String type) {
        return Arrays.asList(createExampleRef(type));
    }


}
