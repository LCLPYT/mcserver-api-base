/*
 * Copyright (c) 2022 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.api;

import com.google.gson.annotations.Expose;
import work.lclpnet.lclpnetwork.facade.JsonSerializable;

public class TransactionResult extends JsonSerializable {

    @Expose
    private String status;
    @Expose
    private String message;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return "success".equals(getStatus());
    }

    public boolean isFailureMissingCoins() {
        return "failure".equals(getStatus()) && "Specified payer does not have enough coins.".equals(getMessage());
    }

}
