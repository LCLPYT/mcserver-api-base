/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.translate;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Map;

public interface ITranslationLoader {

    @Nullable
    Map<String, Map<String, String>> load() throws IOException;

}
