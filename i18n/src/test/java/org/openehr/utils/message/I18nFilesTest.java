package org.openehr.utils.message;

import org.junit.Test;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.fail;

/**
 * Test to check if the syntax of the translations are correct.
 */
public class I18nFilesTest {

    private void testI18nFile(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("openehrArchie", locale);
        for(String key : bundle.keySet()) {
            String translation = bundle.getString(key);
            try {
                new MessageFormat(translation);
            } catch (IllegalArgumentException ex) {
                fail("Parsing translation of '" + key + "' ('" + translation + "') failed: " + ex.getMessage());
            }
        }
    }

    @Test
    public void testEn() {
        testI18nFile(Locale.forLanguageTag("en"));
    }

    @Test
    public void testNl() {
        testI18nFile(Locale.forLanguageTag("nl"));
    }
}
