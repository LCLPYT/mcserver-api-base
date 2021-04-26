/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi;

import work.lclpnet.lclpnetwork.LCLPNetworkAPI;

public class MCServerAPI {

    public static void main(String[] args) {
        LCLPNetworkAPI.INSTANCE.getUserById(1).thenAccept(System.out::println).join();
    }

}
