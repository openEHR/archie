package com.nedap.archie.rmobjectvalidator.invariants.datavalues;

import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.encapsulated.DvParsable;
import com.nedap.archie.rm.support.identification.TerminologyId;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessage;
import com.nedap.archie.rmobjectvalidator.RMObjectValidator;
import com.nedap.archie.rmobjectvalidator.ValidationConfiguration;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DvParsableInvariantTest {

    @Test
    public void valid() {
        DvParsable value = createValid();
        InvariantTestUtil.assertValid(value);
    }

    @Test
    public void languageInvalid() {
        DvParsable value = createValid();
        value.setLanguage(new CodePhrase(new TerminologyId("ISO_639-1"), "newspeak"));
        InvariantTestUtil.assertInvariantInvalid(value, "Language_valid", "/");
    }

    @Test
    public void charSetInvalid() {
        DvParsable value = createValid();
        value.setCharset(new CodePhrase(new TerminologyId("IANA_character-sets"), "UTF-13"));
        RMObjectValidator validator = new RMObjectValidator(ArchieRMInfoLookup.getInstance(), (templateId) -> null, new ValidationConfiguration.Builder().build());
        List<RMObjectValidationMessage> messages = validator.validate(value);

        InvariantTestUtil.assertInvariantInvalid(value, "Charset_valid", "/");
    }

    @Test
    public void formalismInvalid() {
        DvParsable value = createValid();
        value.setFormalism("");
        InvariantTestUtil.assertInvariantInvalid(value, "Formalism_valid", "/");
    }

    private DvParsable createValid() {
        DvParsable value = new DvParsable();
        value.setValue("{\"something\": \"something\"}");
        value.setFormalism("json");
        value.setLanguage(new CodePhrase(new TerminologyId("ISO_639-1"), "nl"));
        value.setCharset(new CodePhrase(new TerminologyId("IANA_character-sets"), "UTF-8"));
        return value;
    }
}
