/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.util;

public interface ILogger {

    ILogger SILENT = new ILogger() {
        @Override
        public void info(String msg) {

        }

        @Override
        public void warn(String msg) {

        }

        @Override
        public void error(String msg) {

        }
    };

    void info(String msg);

    void warn(String msg);

    void error(String msg);

}
