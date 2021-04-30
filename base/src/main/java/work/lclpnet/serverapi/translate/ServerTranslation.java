/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.translate;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class ServerTranslation {

    private static String defaultLanguage = "en_us";
    private static final Map<String, Map<String, String>> languages = new HashMap<>();
    private static final Map<String, SimpleDateFormat> dateFormats = new HashMap<>();

    public static void setDefaultLanguage(String defaultLanguage) {
        ServerTranslation.defaultLanguage = defaultLanguage;
    }

    public static String getDefaultLanguage() {
        return defaultLanguage;
    }

    /**
     * Loads translation files from a given loader and adds all translations to the {@link ServerTranslation} translation list.
     * If there are duplicate keys, the one which was loaded last will be used.
     *
     * @param loader The loader used to load translation files.
     * @throws IOException If there was an I/O error.
     */
    public static void loadFrom(ITranslationLoader loader) throws IOException {
        Map<String, Map<String, String>> loaded = loader.load();
        if(loaded == null) return;

        loaded.forEach((locale, translations) -> {
            Map<String, String> alreadyLoaded = languages.get(locale);
            if(alreadyLoaded == null) {
                languages.put(locale, translations);
                return;
            }

            alreadyLoaded.putAll(translations);
        });
    }

    public static String getTranslation(String locale, String key, Object... substitutes) {
        Map<String, String> translations = languages.get(locale);
        if(translations == null) {
            translations = getDefaultLanguageTranslations();
            if(translations == null) return key;
        }

        String translation = translations.get(key);
        if(translation == null) {
            translations = getDefaultLanguageTranslations();
            if(translations == null) return key;

            translation = translations.get(key);
            if(translation == null) return key;
        }

        return String.format(translation, substitutes);
    }

    private static Map<String, String> getDefaultLanguageTranslations() {
        return languages.get(defaultLanguage);
    }

    /**
     * Check if there is a specific translation for a given language.
     *
     * @param locale The locale to check.
     * @param key The translation key.
     * @return True, if there is a translation for the given language.
     */
    public static boolean hasTranslation(String locale, String key) {
        Map<String, String> translations = languages.get(locale);
        if(translations == null) return false;

        return translations.containsKey(key);
    }

    @Nonnull
    public SimpleDateFormat getDateFormat(String locale) {
        if(dateFormats.containsKey(locale)) return dateFormats.get(locale);

        if(!hasTranslation(locale, "date.format")) {
            locale = defaultLanguage;
            if(dateFormats.containsKey(defaultLanguage)) return dateFormats.get(defaultLanguage);

            if(!hasTranslation(defaultLanguage, "date.format")) {
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                dateFormats.put(defaultLanguage, format);
                return format;
            }
        }

        SimpleDateFormat format = new SimpleDateFormat(getTranslation(locale, "date.format"));
        dateFormats.put(locale, format);
        return format;
    }

}
