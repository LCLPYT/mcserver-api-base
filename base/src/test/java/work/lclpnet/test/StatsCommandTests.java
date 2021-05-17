/*
 * Copyright (c) 2021 LCLP.
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
import work.lclpnet.serverapi.util.IPlatformBridge;
import work.lclpnet.serverapi.util.MojangAPI;
import work.lclpnet.serverapi.util.TransactionMessenger;

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

    private static final StatsCommandScheme testStatsCommand = new StatsCommandScheme() {

        @Override
        public boolean shouldDebug() {
            return true;
        }

        private final MCServerAPI api = new MCServerAPI(APIAccess.PUBLIC);

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

    };

    @Test
    void noArgs() throws InterruptedException {
        testStatsCommand.execute("7357a549-fa3e-4342-91b2-63e5e73ed39a", new Object[0]);
        Thread.sleep(2000L);
    }

    @Test
    void playerNameMatching() throws InterruptedException {
        testStatsCommand.execute("7357a549-fa3e-4342-91b2-63e5e73ed39a", new Object[] { "LCLP" });
        Thread.sleep(2000L);
    }

    @Test
    void playerName() throws InterruptedException {
        testStatsCommand.execute("7357a549-fa3e-4342-91b2-63e5e73ed39a", new Object[] { "Secuenix" });
        Thread.sleep(2000L);
    }

    @Test
    void playerUuidMatching() throws InterruptedException {
        testStatsCommand.execute("7357a549-fa3e-4342-91b2-63e5e73ed39a", new Object[] { "7357a549-fa3e-4342-91b2-63e5e73ed39a" });
        Thread.sleep(2000L);
    }

    @Test
    void playerUuid() throws InterruptedException {
        testStatsCommand.execute("7357a549-fa3e-4342-91b2-63e5e73ed39a", new Object[] { "4eb6bcf7-023f-4b57-b0c3-716a9dbba51f" });
        Thread.sleep(2000L);
    }

    @Test
    void playerUuidMissing() throws InterruptedException {
        testStatsCommand.execute("7357a549-fa3e-4342-91b2-63e5e73ed39a", new Object[] { "014c30a3-4924-4731-ac59-7a68a5947761" });
        Thread.sleep(2000L);
    }

    @Test
    void justTrash() throws InterruptedException {
        testStatsCommand.execute("7357a549-fa3e-4342-91b2-63e5e73ed39a", new Object[] { "hfuiahjfaafdhjlkfhjklfdashjfadshjkl7z234" });
        Thread.sleep(2000L);
    }

    @Test
    void transactionMessenger() {
        TransactionMessenger.sendStatChangeMessage(testBridge, "7357a549-fa3e-4342-91b2-63e5e73ed39a", "stat.general.coins", 5);
        TransactionMessenger.sendStatChangeMessage(testBridge, "7357a549-fa3e-4342-91b2-63e5e73ed39a", "stat.general.coins", -2);
    }

}
