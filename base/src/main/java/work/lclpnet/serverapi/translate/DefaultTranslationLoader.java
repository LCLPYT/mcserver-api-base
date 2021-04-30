/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.translate;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import work.lclpnet.serverapi.util.ILogger;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class DefaultTranslationLoader implements ITranslationLoader {

    private ITranslationLocator translationLocator;
    public final Function<String, InputStream> resourceLoader;
    public final Class<?> owningClass;
    public final ILogger logger;

    public DefaultTranslationLoader(ITranslationLocator translationLocator, Function<String, InputStream> resourceLoader, Class<?> owningClass, @Nullable ILogger logger) {
        this.translationLocator = Objects.requireNonNull(translationLocator);
        this.resourceLoader = Objects.requireNonNull(resourceLoader);
        this.owningClass = Objects.requireNonNull(owningClass);
        this.logger = logger == null ? ILogger.SILENT : logger;
    }

    public void setTranslationLocator(ITranslationLocator translationLocator) {
        this.translationLocator = translationLocator;
    }

    @Nullable
    @Override
    public Map<String, Map<String, String>> load() throws IOException {
        logger.info("Locating translation files...");

        List<String> translationFiles = translationLocator.locate();

        logger.info(String.format("Located %s translation files.", translationFiles.size()));
        logger.info("Loading translations...");

        Map<String, Map<String, String>> languages = new HashMap<>();

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

            Map<String, String> alreadyPut = languages.get(language);
            if(alreadyPut == null) languages.put(language, translations);
            else alreadyPut.putAll(translations);
        }

        int entries = 0;
        for(Map<String, String> langTranslations : languages.values())
            entries += langTranslations.size();

        logger.info(String.format("Loaded %s locales with a total of %s translation entries.", languages.size(), entries));
        return languages;
    }

}
