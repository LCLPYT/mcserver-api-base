/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.lclpnet.translations.loader.TranslationProvider;
import work.lclpnet.translations.loader.language.LanguageLoader;
import work.lclpnet.translations.network.LCLPNetworkLanguageLoader;

import java.util.Collections;
import java.util.List;

public class LCLPNetworkTranslationProvider implements TranslationProvider {

    private final Logger logger = LoggerFactory.getLogger(LCLPNetworkTranslationProvider.class);

    @Override
    public LanguageLoader create() {
        List<String> apps = Collections.singletonList("mc_server");

        return new LCLPNetworkLanguageLoader(apps, null, logger);
    }
}
