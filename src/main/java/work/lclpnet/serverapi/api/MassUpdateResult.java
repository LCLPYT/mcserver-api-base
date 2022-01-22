/*
 * Copyright (c) 2022 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.api;

import com.google.gson.annotations.Expose;
import work.lclpnet.lclpnetwork.facade.JsonSerializable;

import java.util.List;

public class MassUpdateResult extends JsonSerializable {

    @Expose
    private String status;
    @Expose
    private String message;
    @Expose
    private List<Error> errors;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public boolean isSuccess() {
        return "success".equals(this.status);
    }

    public static class Error extends JsonSerializable {

        @Expose
        private String element;
        @Expose
        private String value;
        @Expose
        private String message;

        public String getElement() {
            return element;
        }

        public String getValue() {
            return value;
        }

        public String getMessage() {
            return message;
        }

    }

}
