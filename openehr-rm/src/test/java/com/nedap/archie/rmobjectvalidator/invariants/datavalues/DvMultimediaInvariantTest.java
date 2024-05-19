package com.nedap.archie.rmobjectvalidator.invariants.datavalues;

import org.openehr.rm.datatypes.CodePhrase;
import org.openehr.rm.datavalues.encapsulated.DvMultimedia;
import org.openehr.rm.support.identification.TerminologyId;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.Test;

public class DvMultimediaInvariantTest {

    @Test
    public void valid() {
        DvMultimedia dvMultimedia = createValid();
        InvariantTestUtil.assertValid(dvMultimedia);
        dvMultimedia.setMediaType(new CodePhrase("openehr::427")); //also image/png, but in an old not human readable way.
        InvariantTestUtil.assertValid(dvMultimedia);
    }

    @Test
    public void mediatypeInvalid() {
        DvMultimedia dvMultimedia = createValid();
        dvMultimedia.setMediaType(new CodePhrase("IANA_media-types::blimage/qnh"));
        InvariantTestUtil.assertInvariantInvalid(dvMultimedia, "Media_type_valid", "/");
    }

    @Test
    public void integrityCheckWithoutAlgorithm() {
        DvMultimedia dvMultimedia = createValid();
        dvMultimedia.setIntegrityCheckAlgorithm(null);
        InvariantTestUtil.assertInvariantInvalid(dvMultimedia, "Integrity_check_validity", "/");
    }

    @Test
    public void integrityCheckAlgorithmInvalid() {
        DvMultimedia dvMultimedia = createValid();
        dvMultimedia.setIntegrityCheckAlgorithm(new CodePhrase("openehr_integrity_check_algorithms::BLA-256"));
        InvariantTestUtil.assertInvariantInvalid(dvMultimedia, "Integrity_check_algorithm_validity", "/");
    }

    @Test
    public void sizeInvalid() {
        DvMultimedia dvMultimedia = createValid();
        dvMultimedia.setSize(-10);
        InvariantTestUtil.assertInvariantInvalid(dvMultimedia, "Size_valid", "/");
    }

    @Test
    public void emptyInvalid() {
        DvMultimedia dvMultimedia = createValid();
        dvMultimedia.setData(null);
        InvariantTestUtil.assertInvariantInvalid(dvMultimedia, "Not_empty", "/");
    }

    @Test
    public void languageInvalid() {
        DvMultimedia dvMultimedia = createValid();
        dvMultimedia.setLanguage(new CodePhrase(new TerminologyId("ISO_639-1"), "newspeak"));
        InvariantTestUtil.assertInvariantInvalid(dvMultimedia, "Language_valid", "/");
    }

    @Test
    public void charSetInvalid() {
        DvMultimedia dvMultimedia = createValid();
        dvMultimedia.setCharset(new CodePhrase(new TerminologyId("IANA_character-sets"), "UTF-13"));
        InvariantTestUtil.assertInvariantInvalid(dvMultimedia, "Charset_valid", "/");
    }


    private DvMultimedia createValid() {
        DvMultimedia dvMultimedia = new DvMultimedia();
        dvMultimedia.setMediaType(new CodePhrase("IANA_media-types::image/png"));
        dvMultimedia.setData(new byte[1]);
        dvMultimedia.setSize(dvMultimedia.getData().length);
        dvMultimedia.setCompressionAlgorithm(new CodePhrase("openehr_compression_algorithms::gzip"));
        dvMultimedia.setIntegrityCheck(new byte[16]);
        dvMultimedia.setIntegrityCheckAlgorithm(new CodePhrase("openehr_integrity_check_algorithms::SHA-256"));
        dvMultimedia.setLanguage(new CodePhrase(new TerminologyId("ISO_639-1"), "nl"));
        dvMultimedia.setCharset(new CodePhrase(new TerminologyId("IANA_character-sets"), "UTF-8"));
        return dvMultimedia;
    }
}
