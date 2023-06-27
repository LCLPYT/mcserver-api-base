/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.cmd;

public interface IDebuggable {

    boolean shouldDebug();

    default void logError(Throwable throwable) {
        if (throwable != null) throwable.printStackTrace();
    }

}
