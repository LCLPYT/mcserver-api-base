/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.translate;

public interface ITranslationService {

    String translate(String language, String translationKey, String[] substitutes);

}
