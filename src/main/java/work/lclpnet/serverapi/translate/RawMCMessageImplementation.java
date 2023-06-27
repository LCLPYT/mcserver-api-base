/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.translate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RawMCMessageImplementation {

    public static String convertMCMessageToString(MCMessage msg, String language) {
        return convertMCMessageToString(msg, language, (lang, translationKey, substitutes) ->
                String.format("translate('%s', %s, %s)",
                        translationKey,
                        lang,
                        Arrays.toString(substitutes)));
    }

    public static String convertMCMessageToString(MCMessage msg, String language, ITranslationService translationService) {
        StringBuilder builder = new StringBuilder();
        recurseMessage(Objects.requireNonNull(msg), language, builder, translationService);

        return builder.toString();
    }

    private static void recurseMessage(MCMessage msg, String language, StringBuilder builder, ITranslationService translationService) {
        if (!msg.isTextNode()) {
            for (MCMessage child : msg.getChildren()) {
                recurseMessage(child, language, builder, translationService);
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

            text = translationService.translate(language, translationMsg.getText(), substitutes);
        } else {
            text = msg.getText();
        }

        builder.append(text);
    }
}
