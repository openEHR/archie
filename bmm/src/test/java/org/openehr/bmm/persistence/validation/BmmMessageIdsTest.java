package org.openehr.bmm.persistence.validation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openehr.utils.message.I18n;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

/**
 * Test i18n of the BmmMessageIds
 */
public class BmmMessageIdsTest {


    @AfterEach
    public void tearDown() {
        I18n.setCurrentLocale(null);
    }
    @Test
    public void english() {
        I18n.setCurrentLocale(Locale.forLanguageTag("en"));
        String englishMessage =BmmMessageIds.ec_bmm_schema_duplicate_schema_found.getMessage("model", "file_name");

        assertEquals("Duplicate Reference Model schema found for model 'model' in file file_name, ignoring latter", englishMessage);

    }

    @Test
    public void dutch() {
        I18n.setCurrentLocale(Locale.forLanguageTag("nl"));
        String dutchMessage =BmmMessageIds.ec_bmm_schema_duplicate_schema_found.getMessage("model", "bestandsnaam");
        assertEquals("Meer dan één Reference Model schema gevonden voor model 'model' in bestand bestandsnaam. De tweede en volgende worden genegeerd", dutchMessage);
    }


    @Test
    public void explicitLocale() {
        I18n.setCurrentLocale(null);
        String dutchMessage = BmmMessageIds.ec_bmm_schema_duplicate_schema_found.getMessage(Locale.forLanguageTag("nl"), "model", "bestandsnaam");
        assertEquals("Meer dan één Reference Model schema gevonden voor model 'model' in bestand bestandsnaam. De tweede en volgende worden genegeerd", dutchMessage);
    }
}
