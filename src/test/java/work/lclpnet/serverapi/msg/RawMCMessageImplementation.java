/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.msg;

import work.lclpnet.translations.Translator;

import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class RawMCMessageImplementation {

    public static String convertMCMessageToString(MCMessage msg, String language) {
        return convertMCMessageToString(msg, language, new Translator() {
            @Nonnull
            @Override
            public String translate(String locale, String key) {
                return String.format("translate('%s', %s)", key, locale);
            }

            @Nonnull
            @Override
            public String translate(String locale, String key, Object... substitutes) {
                return String.format("translate('%s', %s, %s)",
                        key,
                        locale,
                        Arrays.toString(substitutes));
            }

            @Override
            public boolean hasTranslation(String locale, String key) {
                return true;
            }

            @Nonnull
            @Override
            public SimpleDateFormat getDateFormat(String locale) {
                return new SimpleDateFormat();
            }

            @Override
            public Iterable<String> getLanguages() {
                return Collections.singletonList("none");
            }
        });
    }

    public static String convertMCMessageToString(MCMessage msg, String language, Translator translationService) {
        StringBuilder builder = new StringBuilder();
        recurseMessage(Objects.requireNonNull(msg), language, builder, translationService);

        return builder.toString();
    }

    private static void recurseMessage(MCMessage msg, String language, StringBuilder builder, Translator translator) {
        if (!msg.isTextNode()) {
            for (MCMessage child : msg.getChildren()) {
                recurseMessage(child, language, builder, translator);
            }

            return;
        }

        String text;
        if (msg instanceof MCMessage.MCTranslationMessage) {
            MCMessage.MCTranslationMessage translationMsg = (MCMessage.MCTranslationMessage) msg;

            List<MCMessage> substituteList = translationMsg.getSubstitutes();
            String[] substitutes = new String[substituteList.size()];

            for (int i = 0; i < substituteList.size(); i++) {
                MCMessage subMsg = substituteList.get(i);
                substitutes[i] = convertMCMessageToString(subMsg, language);
            }

            text = translator.translate(language, translationMsg.getText(), (Object[]) substitutes);
        } else {
            text = msg.getText();
        }

        builder.append(text);
    }
}
