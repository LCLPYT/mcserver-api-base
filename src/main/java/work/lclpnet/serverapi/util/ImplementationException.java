/*
 * Copyright (c) 2022 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.util;

public class ImplementationException extends RuntimeException {

    private static final long serialVersionUID = -174759647047513470L;

    public ImplementationException() {
        super();
    }

    public ImplementationException(String message) {
        super(message);
    }

    public ImplementationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImplementationException(Throwable cause) {
        super(cause);
    }

}
