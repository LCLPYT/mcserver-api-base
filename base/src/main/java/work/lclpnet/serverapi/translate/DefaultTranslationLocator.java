/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.translate;

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

    private final DefaultTranslationLoader loader;
    private Predicate<String> fileNamePredicate = name -> name.startsWith("lang/") && name.endsWith(".json");

    public DefaultTranslationLocator(DefaultTranslationLoader loader) {
        this.loader = loader;
    }

    public void setFileNamePredicate(Predicate<String> fileNamePredicate) {
        this.fileNamePredicate = Objects.requireNonNull(fileNamePredicate);
    }

    @Override
    public List<String> locate() throws IOException {
        CodeSource src = loader.owningClass.getProtectionDomain().getCodeSource();
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
                loader.logger.info(String.format("Located translation file '%s'.", name));
            }
        }

        return translationFiles;
    }

}
