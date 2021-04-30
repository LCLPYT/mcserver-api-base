/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.translate;

import work.lclpnet.serverapi.util.ILogger;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DefaultTranslationLocator implements ITranslationLocator {

    private final Class<?> owningClass;
    private final ILogger logger;
    private Predicate<String> fileNamePredicate;

    public DefaultTranslationLocator(Class<?> owningClass, ILogger logger, List<String> resourceDirectories) {
        this.owningClass = owningClass;
        this.logger = logger;
        this.fileNamePredicate = name -> name.endsWith(".json") && resourceDirectories.stream().anyMatch(name::startsWith);
    }

    public void setFileNamePredicate(Predicate<String> fileNamePredicate) {
        this.fileNamePredicate = Objects.requireNonNull(fileNamePredicate);
    }

    @Override
    public List<String> locate() throws IOException {
        CodeSource src = owningClass.getProtectionDomain().getCodeSource();
        if(src == null) throw new NullPointerException("code source is null");

        List<String> translationFiles = new ArrayList<>();

        URL jar = src.getLocation();
        try (ZipInputStream zip = new ZipInputStream(jar.openStream())) {
            while(true) {
                ZipEntry entry = zip.getNextEntry();
                if(entry == null) break;

                String name = entry.getName();
                if(!fileNamePredicate.test(name)) continue;

                translationFiles.add(name);
                logger.info(String.format("Located translation file '%s'.", name));
            }
        }

        return translationFiles;
    }

}
