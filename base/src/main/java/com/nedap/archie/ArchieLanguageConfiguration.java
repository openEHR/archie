package com.nedap.archie;

/**
 * Created by pieter.bos on 21/10/15.
 */
public class ArchieLanguageConfiguration {

    private static ThreadLocal<String> currentMeaningAndDescriptionLanguage = new ThreadLocal<>();

    private static String DEFAULT_MEANING_DESCRIPTION_LANGUAGE = "en";

    /**
     * The language for use in logical paths
     * @return The language for use in logical paths
     */
    public static String getMeaningAndDescriptionLanguage() {
        String language = currentMeaningAndDescriptionLanguage.get();
        if(language == null) {
            language = DEFAULT_MEANING_DESCRIPTION_LANGUAGE;
        }
        return language;
    }

    public static void setDefaultMeaningAndDescriptionLanguage(String defaultLanguage) {
        DEFAULT_MEANING_DESCRIPTION_LANGUAGE = defaultLanguage;
    }

    /**
     * Get the overridden language for descriptions and meanings in the current thread.
     *
     * In general you should use the {@link #getMeaningAndDescriptionLanguage()} function
     * as it will automatically fall back to the default setting when no thread specific
     * language is set.
     */
    public static String getThreadLocalDescriptiongAndMeaningLanguage() {
        return currentMeaningAndDescriptionLanguage.get();
    }

    /*
     * Override the language used in descriptions and meanings, on a thread local basis
     * @Param language the language to use
     */
    public static void setThreadLocalDescriptiongAndMeaningLanguage(String language) {
        currentMeaningAndDescriptionLanguage.set(language);
    }

    public static String getDefaultMeaningAndDescriptionLanguage() {
        return DEFAULT_MEANING_DESCRIPTION_LANGUAGE;
    }
}
