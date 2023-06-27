/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.test;

import org.junit.jupiter.api.Test;
import work.lclpnet.lclpnetwork.api.APIAccess;
import work.lclpnet.lclpnetwork.facade.MCStats;
import work.lclpnet.serverapi.MCServerAPI;
import work.lclpnet.serverapi.cmd.StatsCommandScheme;
import work.lclpnet.serverapi.translate.MCMessage;
import work.lclpnet.serverapi.translate.RawMCMessageImplementation;
import work.lclpnet.serverapi.util.*;

import java.util.concurrent.CompletableFuture;

public class StatsCommandTests {

    private static final IPlatformBridge testBridge = new IPlatformBridge() {

        @Override
        public void sendMessageTo(String playerUuid, MCMessage msg) {
            System.out.println(RawMCMessageImplementation.convertMCMessageToString(msg, "en_us"));
        }

        @Override
        public CompletableFuture<String> getPlayerNameByUUID(String playerUuid) {
            return MojangAPI.getUsernameByUUID(playerUuid);
        }

        @Override
        public CompletableFuture<String> getPlayerUUIDByName(String name) {
            return MojangAPI.getUUIDByUsername(name);
        }

    };

    private static final ServerContext testServerContext = new ServerContext() {
        private final ServerCache cache = new ServerCache();

        @Override
        public ServerCache getCache() {
            return cache;
        }
    };

    private static final StatsCommandScheme testStatsCommand = new StatsCommandScheme() {

        private final MCServerAPI api = new MCServerAPI(APIAccess.PUBLIC);

        @Override
        public boolean shouldDebug() {
            return true;
        }

        @Override
        public MCServerAPI getAPI() {
            return api;
        }

        @Override
        public IPlatformBridge getPlatformBridge() {
            return testBridge;
        }

        @Override
        public void openStats(String invokerUuid, String targetUuid, MCMessage title, MCStats targetStats) {
            System.out.println("Success: " + RawMCMessageImplementation.convertMCMessageToString(title, "en_us") + " " + targetStats);
        }

        @Override
        public ServerContext getContext() {
            return testServerContext;
        }
    };

    @Test
    void noArgs() {
        testStatsCommand.execute("7357a549-fa3e-4342-91b2-63e5e73ed39a", new Object[0]).join();
    }

    @Test
    void playerNameMatching() {
        testStatsCommand.execute("7357a549-fa3e-4342-91b2-63e5e73ed39a", new Object[]{"LCLP"}).join();
    }

    @Test
    void playerName() {
        testStatsCommand.execute("7357a549-fa3e-4342-91b2-63e5e73ed39a", new Object[]{"Secuenix"}).join();
    }

    @Test
    void playerUuidMatching() {
        testStatsCommand.execute("7357a549-fa3e-4342-91b2-63e5e73ed39a", new Object[]{"7357a549-fa3e-4342-91b2-63e5e73ed39a"}).join();
    }

    @Test
    void playerUuid() {
        testStatsCommand.execute("7357a549-fa3e-4342-91b2-63e5e73ed39a", new Object[]{"4eb6bcf7-023f-4b57-b0c3-716a9dbba51f"}).join();
    }

    @Test
    void playerUuidMissing() {
        testStatsCommand.execute("7357a549-fa3e-4342-91b2-63e5e73ed39a", new Object[]{"014c30a3-4924-4731-ac59-7a68a5947761"}).join();
    }

    @Test
    void justTrash() {
        testStatsCommand.execute("7357a549-fa3e-4342-91b2-63e5e73ed39a", new Object[]{"hfuiahjfaafdhjlkfhjklfdashjfadshjkl7z234"}).join();
    }

    @Test
    void transactionMessenger() {
        TransactionMessenger messenger = TransactionMessenger.getInstance();
        messenger.sendStatChangeMessage(testBridge, "7357a549-fa3e-4342-91b2-63e5e73ed39a", "stat.general.coins", 5);
        messenger.sendStatChangeMessage(testBridge, "7357a549-fa3e-4342-91b2-63e5e73ed39a", "stat.general.coins", -2);
    }
}
