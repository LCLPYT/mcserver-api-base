/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.translate;

import java.io.IOException;
import java.util.List;

public interface ITranslationLocator {

    List<String> locate() throws IOException;

}
