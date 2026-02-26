package org.openehr.utils.message;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;

public class I18n {

    private static final LoadingCache<Locale, Optional<ResourceBundle>> resourceCache = CacheBuilder.newBuilder().build(
            new CacheLoader<>() {
                @Override
                public Optional<ResourceBundle> load(Locale locale) throws Exception {
                    try {
                        return Optional.of(ResourceBundle.getBundle("openehrArchie", locale));
                    } catch (MissingResourceException e) {
                        //TODO: warn about missing resource?
                        return Optional.empty();
                    }
                }
            });

    private static ThreadLocal<Locale> currentLocale = new ThreadLocal<>();

    /**
     * Translate the given string with the given input to the desired language
     * @param input
     * @param args
     * @return
     */
    public static String t(String input, Object... args) {
        return t(input, getCurrentLocale(), args);
    }

    /**
     * Translate the given string with the given input to the desired language
     * @param input
     * @param args
     * @return
     */
    public static String t(String input, Locale locale, Object... args) {

        Optional<ResourceBundle> optionalBundle = resourceCache.getUnchecked(locale);

        if(optionalBundle.isPresent()) {
            ResourceBundle bundle = optionalBundle.get();
            if(bundle.containsKey(input)) {
                return MessageFormat.format(optionalBundle.get().getString(input), args);
            }
        }
        //just return the base string if either the bundle has not been found or the key is not present - fall back to the English default
        return MessageFormat.format(input, args);
    }


    /**
     * Register a template key and return it. Useful mostly when you want to translate enum constants - for normal
     * translation you will likely want to use the t method from this class
     *
     * This method does not do anything, but the gettext scanner recognizes it and adds it to the resource bundles
     * @param template
     * @return
     */
    public static String register(String template) {

        return template;
    }

    public static void setCurrentLocale(Locale locale){
        currentLocale.set(locale);
    }

    public static Locale getCurrentLocale() {
        Locale result = currentLocale.get();
        if(currentLocale.get() == null) {
            result = Locale.getDefault();
        }
        return result;
    }
}
