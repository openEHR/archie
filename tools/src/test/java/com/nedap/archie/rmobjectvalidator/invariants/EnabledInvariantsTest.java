package com.nedap.archie.rmobjectvalidator.invariants;

import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.support.identification.TerminologyId;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rminfo.RMInvariants;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessage;
import com.nedap.archie.rmobjectvalidator.RMObjectValidator;
import com.nedap.archie.rmobjectvalidator.ValidationConfiguration;
import com.nedap.archie.testutil.DummyOperationalTemplateProvider;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnabledInvariantsTest {

    @Test
    public void allInvariantsByDefault() {
        List<RMObjectValidationMessage> messages = validate(createInvalidText(), new ValidationConfiguration.Builder().build());
        assertEquals(2, messages.size(), messages.toString());
    }

    @Test
    public void onlyEnabledInvariants() {
        List<RMObjectValidationMessage> messages = validate(createInvalidText(), new ValidationConfiguration.Builder()
                .enabledInvariants(Set.of(RMInvariants.DV_TEXT.LANGUAGE_VALID))
                .build());
        assertEquals(1, messages.size(), messages.toString());
        assertEquals("Invariant Language_valid failed on type DV_TEXT", messages.get(0).getMessage());
    }

    @Test
    public void emptySetValidatesNoInvariants() {
        List<RMObjectValidationMessage> messages = validate(createInvalidText(), new ValidationConfiguration.Builder()
                .enabledInvariants(Set.of())
                .build());
        assertTrue(messages.isEmpty(), messages.toString());
    }

    @Test
    public void validateInvariantsFalseValidatesNoInvariants() {
        List<RMObjectValidationMessage> messages = validate(createInvalidText(), new ValidationConfiguration.Builder()
                .validateInvariants(false)
                .build());
        assertTrue(messages.isEmpty(), messages.toString());
    }

    @Test
    public void validateInvariantsFalseAndEnabledInvariantsThrows() {
        ValidationConfiguration.Builder builder = new ValidationConfiguration.Builder()
                .validateInvariants(false)
                .enabledInvariants(Set.of(RMInvariants.DV_TEXT.LANGUAGE_VALID));
        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    public void validateInvariantsTrueAndEnabledInvariantsThrows() {
        ValidationConfiguration.Builder builder = new ValidationConfiguration.Builder()
                .validateInvariants(true)
                .enabledInvariants(Set.of(RMInvariants.DV_TEXT.LANGUAGE_VALID));
        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    public void enabledInvariantsAndValidateInvariantsInOtherOrderThrows() {
        ValidationConfiguration.Builder builder = new ValidationConfiguration.Builder()
                .enabledInvariants(Set.of(RMInvariants.DV_TEXT.LANGUAGE_VALID))
                .validateInvariants(false);
        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    public void declaringTypeAppliesToSubtypes() {
        DvCodedText text = new DvCodedText("something", new CodePhrase(new TerminologyId("local"), "at1"));
        text.setLanguage(new CodePhrase(new TerminologyId("ISO_639-1"), "Pig latin"));
        List<RMObjectValidationMessage> messages = validate(text, new ValidationConfiguration.Builder()
                .enabledInvariants(Set.of(RMInvariants.DV_TEXT.LANGUAGE_VALID))
                .build());
        assertEquals(1, messages.size(), messages.toString());
        assertEquals("Invariant Language_valid failed on type DV_CODED_TEXT", messages.get(0).getMessage());
    }

    @Test
    public void subtypeDoesNotApplyToSupertype() {
        List<RMObjectValidationMessage> messages = validate(createInvalidText(), new ValidationConfiguration.Builder()
                .enabledInvariants(Set.of("DV_CODED_TEXT.Language_valid"))
                .build());
        assertTrue(messages.isEmpty(), messages.toString());
    }

    @Test
    public void unknownInvariantThrows() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validate(createInvalidText(), new ValidationConfiguration.Builder()
                .enabledInvariants(Set.of("DV_TEXT.Langauge_valid"))
                .build()));
        assertTrue(exception.getMessage().contains("DV_TEXT.Langauge_valid (unknown)"), exception.getMessage());
    }

    @Test
    public void ignoredInvariantThrows() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validate(createInvalidText(), new ValidationConfiguration.Builder()
                .enabledInvariants(Set.of("DV_TEXT.Mappings_valid"))
                .build()));
        assertTrue(exception.getMessage().contains("DV_TEXT.Mappings_valid (ignored"), exception.getMessage());
    }

    @Test
    public void inheritedInvariantOnSubtypeIsKnown() {
        assertDoesNotThrow(() -> validate(createInvalidText(), new ValidationConfiguration.Builder()
                .enabledInvariants(Set.of("DV_CODED_TEXT.Language_valid"))
                .build()));
    }

    @Test
    public void allGeneratedConstantsAreKnown() throws IllegalAccessException {
        Set<String> allInvariants = new HashSet<>();
        for (Class<?> typeClass : RMInvariants.class.getDeclaredClasses()) {
            for (Field field : typeClass.getDeclaredFields()) {
                allInvariants.add((String) field.get(null));
            }
        }
        assertFalse(allInvariants.isEmpty());
        assertDoesNotThrow(() -> validate(createInvalidText(), new ValidationConfiguration.Builder()
                .enabledInvariants(allInvariants)
                .build()));
    }

    @Test
    public void onlyDisabledInvariantsAreSkipped() {
        List<RMObjectValidationMessage> messages = validate(createInvalidText(), new ValidationConfiguration.Builder()
                .disabledInvariants(Set.of(RMInvariants.DV_TEXT.LANGUAGE_VALID))
                .build());
        assertEquals(1, messages.size(), messages.toString());
        assertEquals("Invariant Encoding_valid failed on type DV_TEXT", messages.get(0).getMessage());
    }

    @Test
    public void emptyDisabledSetValidatesAllInvariants() {
        List<RMObjectValidationMessage> messages = validate(createInvalidText(), new ValidationConfiguration.Builder()
                .disabledInvariants(Set.of())
                .build());
        assertEquals(2, messages.size(), messages.toString());
    }

    @Test
    public void disabledDeclaringTypeAppliesToSubtypes() {
        DvCodedText text = new DvCodedText("something", new CodePhrase(new TerminologyId("local"), "at1"));
        text.setLanguage(new CodePhrase(new TerminologyId("ISO_639-1"), "Pig latin"));
        List<RMObjectValidationMessage> messages = validate(text, new ValidationConfiguration.Builder()
                .disabledInvariants(Set.of(RMInvariants.DV_TEXT.LANGUAGE_VALID))
                .build());
        assertTrue(messages.isEmpty(), messages.toString());
    }

    @Test
    public void disabledSubtypeDoesNotApplyToSupertype() {
        List<RMObjectValidationMessage> messages = validate(createInvalidText(), new ValidationConfiguration.Builder()
                .disabledInvariants(Set.of("DV_CODED_TEXT.Language_valid"))
                .build());
        assertEquals(2, messages.size(), messages.toString());
    }

    @Test
    public void unknownDisabledInvariantThrows() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validate(createInvalidText(), new ValidationConfiguration.Builder()
                .disabledInvariants(Set.of("DV_TEXT.Langauge_valid"))
                .build()));
        assertTrue(exception.getMessage().contains("Invalid disabled invariants: DV_TEXT.Langauge_valid (unknown)"), exception.getMessage());
    }

    @Test
    public void ignoredDisabledInvariantThrows() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validate(createInvalidText(), new ValidationConfiguration.Builder()
                .disabledInvariants(Set.of("DV_TEXT.Mappings_valid"))
                .build()));
        assertTrue(exception.getMessage().contains("DV_TEXT.Mappings_valid (ignored"), exception.getMessage());
    }

    @Test
    public void enabledAndDisabledInvariantsThrows() {
        ValidationConfiguration.Builder builder = new ValidationConfiguration.Builder()
                .enabledInvariants(Set.of(RMInvariants.DV_TEXT.LANGUAGE_VALID))
                .disabledInvariants(Set.of(RMInvariants.DV_TEXT.ENCODING_VALID));
        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    public void validateInvariantsAndDisabledInvariantsThrows() {
        ValidationConfiguration.Builder builder = new ValidationConfiguration.Builder()
                .validateInvariants(true)
                .disabledInvariants(Set.of(RMInvariants.DV_TEXT.LANGUAGE_VALID));
        assertThrows(IllegalStateException.class, builder::build);
    }

    private static DvText createInvalidText() {
        DvText text = new DvText("something");
        text.setLanguage(new CodePhrase(new TerminologyId("ISO_639-1"), "Pig latin"));
        text.setEncoding(new CodePhrase(new TerminologyId("IANA_character-sets"), "Pig latin"));
        return text;
    }

    private static List<RMObjectValidationMessage> validate(Object object, ValidationConfiguration configuration) {
        RMObjectValidator validator = new RMObjectValidator(ArchieRMInfoLookup.getInstance(), new DummyOperationalTemplateProvider("example"), configuration);
        return validator.validate(object);
    }
}
