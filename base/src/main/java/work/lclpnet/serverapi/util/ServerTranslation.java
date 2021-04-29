/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import javax.annotation.Nullable;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.CodeSource;
import java.util.*;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ServerTranslation {

    private final Function<String, InputStream> resourceLoader;
    private final Class<?> owningClass;
    private final ILogger logger;
    private final String defaultLanguage;
    private final Map<String, Map<String, String>> languages = new HashMap<>();

    public ServerTranslation(Function<String, InputStream> resourceLoader, Class<?> owningClass, @Nullable ILogger logger, String defaultLanguage) {
        this.resourceLoader = Objects.requireNonNull(resourceLoader);
        this.owningClass = Objects.requireNonNull(owningClass);
        this.logger = logger == null ? ILogger.DUMMY : logger;
        this.defaultLanguage = Objects.requireNonNull(defaultLanguage);
    }

    public void load() throws IOException {
        languages.clear();

        logger.info("Locating translation files...");

        CodeSource src = owningClass.getProtectionDomain().getCodeSource();
        if(src == null) throw new NullPointerException("code source is null");

        List<String> translationFiles = new ArrayList<>();

        URL jar = src.getLocation();
        try (ZipInputStream zip = new ZipInputStream(jar.openStream())) {
            while(true) {
                ZipEntry entry = zip.getNextEntry();
                if(entry == null) break;

                String name = entry.getName();
                if(!name.startsWith("lang/") || !name.endsWith(".json")) continue;

                translationFiles.add(name);
                logger.info(String.format("Located translation file '%s'.", name));
            }
        }

        if(translationFiles.isEmpty()) {
            logger.error("Could not locate any translation files. Translation will fail.");
            return;
        }

        logger.info("Loading translations...");

        final Gson gson = new Gson();
        for (String file : translationFiles) {
            JsonObject translationObj;

            logger.info(String.format("Trying to load language file '%s'...", file));

            try (InputStream in = resourceLoader.apply(file)) {
                if(in == null) throw new FileNotFoundException(String.format("Resource '%s' could not be found.", file));

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                    translationObj = gson.fromJson(reader, JsonObject.class);
                } catch (JsonSyntaxException e) {
                    logger.error(String.format("Failed to load language file '%s'.", file));
                    throw e;
                }
            }

            String[] parts = file.split("/");
            String fileName = parts[parts.length - 1];
            String language = fileName.substring(0, fileName.length() - 5); // remove .json ending

            Map<String, String> translations = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry : translationObj.entrySet()) {
                JsonElement value = entry.getValue();
                String key = entry.getKey();
                try {
                    String val = value.getAsString();
                    translations.put(key, val);
                } catch (ClassCastException e) {
                    logger.warn(String.format("Unexpected value type '%s' of key '%s' in '%s'.", value.getClass().getName(), key, fileName));
                }
            }

            languages.put(language, translations);
        }

        if(!languages.containsKey(defaultLanguage))
            logger.warn(String.format("Default language '%s' was not loaded.", defaultLanguage));

        logger.info("Translations loaded.");
    }

    public String getTranslation(String locale, String key, Object... substitutes) {
        Map<String, String> translations = languages.get(locale);
        if(translations == null) {
            translations = getDefaultLanguage();
            if(translations == null) return key;
        }

        String translation = translations.get(key);
        if(translation == null) {
            translations = getDefaultLanguage();
            if(translations == null) return key;

            translation = translations.get(key);
            if(translation == null) return key;
        }

        return String.format(translation, substitutes);
    }

    private Map<String, String> getDefaultLanguage() {
        return languages.get(defaultLanguage);
    }

}
