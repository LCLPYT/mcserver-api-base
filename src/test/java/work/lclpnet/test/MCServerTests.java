/*
 * Copyright (c) 2022 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import work.lclpnet.lclpnetwork.LCLPNetworkAPI;
import work.lclpnet.lclpnetwork.api.*;
import work.lclpnet.serverapi.MCServerAPI;
import work.lclpnet.serverapi.api.*;

import javax.annotation.Nullable;
import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class MCServerTests {

    @Test
    void isNetworkOperatorNoAuth() {
        MCServerAPI instance = new MCServerAPI(APIAccess.PUBLIC);
        try {
            instance.isNetworkOperator("7357a549-fa3e-4342-91b2-63e5e73ed39a").thenAccept(System.out::println).join();
            fail("This statement should not be reached.");
        } catch (CompletionException e) {
            if(e.getCause() == null || !(e.getCause() instanceof APIException)) throw e;
            assertEquals(APIException.UNAUTHENTICATED, e.getCause());
        }
    }

    @Test
    void isNetworkOperator() throws IOException {
        MCServerAPI instance = stagingAuth();
        assertNotNull(instance);

        Boolean operator = instance.isNetworkOperator("7357a549-fa3e-4342-91b2-63e5e73ed39a").join();
        assertTrue(operator);
    }

    @Test
    void updateLastSeen() throws IOException {
        MCServerAPI instance = stagingAuth();
        assertNotNull(instance);
        instance.updateLastSeen("7357a549-fa3e-4342-91b2-63e5e73ed39a").join();
    }

    /*@Test
    void processMCLinkToken() throws IOException {
        MCServerAPI instance = stagingAuth();
        assertNotNull(instance);
        String token = "27d23437-6ac0-438a-94b5-184d69ed1c99";
        Boolean success = instance.processMCLinkToken("7357a549-fa3e-4342-91b2-63e5e73ed39a", token).join();
        assertTrue(success);
    }*/

    /*@Test
    void requestMCLinkReverseToken() throws IOException {
        MCServerAPI instance = stagingAuth();
        assertNotNull(instance);
        MCLinkResponse linkResponse = instance.requestMCLinkReverseToken("7357a549-fa3e-4342-91b2-63e5e73ed39a").join();
        assertFalse(linkResponse.isAlreadyLinked());
        assertNotNull(linkResponse.getToken());
    }*/

    @Test
    void incrementStat() throws IOException {
        MCServerAPI instance = stagingAuth();
        assertNotNull(instance);
        IncrementTransaction.Item coins = new IncrementTransaction.Item(StatItems.COINS, 1);
        IncrementTransaction.Item points = new IncrementTransaction.Item(StatItems.POINTS, 1);
        IncrementTransaction transaction = new IncrementTransaction("7357a549-fa3e-4342-91b2-63e5e73ed39a", Arrays.asList(coins, points));
        IncrementResult result = instance.incrementStat(StatTypes.CURRENCY, Collections.singletonList(transaction)).join();
        assertTrue(result.isSuccess());
    }

    @Test
    void incrementMassStat() throws IOException {
        MCServerAPI instance = stagingAuth();
        assertNotNull(instance);
        IncrementResult result = instance.incrementStat(new MassIncrementTransaction(StatTypes.CURRENCY)
                .add("7357a549-fa3e-4342-91b2-63e5e73ed39a", StatItems.POINTS, 5)
                .add("7357a549-fa3e-4342-91b2-63e5e73ed39a", StatItems.COINS, 2)
                .add("4eb6bcf7-023f-4b57-b0c3-716a9dbba51f", StatItems.COINS, 3)
        ).join();
        assertTrue(result.isSuccess());
    }

    @Test
    void incrementMassCurrency() throws IOException {
        MCServerAPI instance = stagingAuth();
        assertNotNull(instance);
        IncrementResult result = instance.incrementStat(new CurrencyMassIncrementTransaction()
                .addCoins("7357a549-fa3e-4342-91b2-63e5e73ed39a", 5, "mcserver.tests.grant", true)
                .addCoins("4eb6bcf7-023f-4b57-b0c3-716a9dbba51f", 2, "MCServer Tests", false)
        ).join();
        assertTrue(result.isSuccess());
    }

    @Test
    void makeTransaction() throws IOException {
        MCServerAPI instance = stagingAuth();
        assertNotNull(instance);
        TransactionResult result = instance.makeCoinTransaction("7357a549-fa3e-4342-91b2-63e5e73ed39a", null, 1, "Test transaction", false).join();
        assertTrue(result.isSuccess());
    }

    @Test
    void makeTransactionWithRecipient() throws IOException {
        MCServerAPI instance = stagingAuth();
        assertNotNull(instance);
        TransactionResult result = instance.makeCoinTransaction(
                "7357a549-fa3e-4342-91b2-63e5e73ed39a",
                "a16bf50d-9e08-4855-826b-5922f47ff451",
                1, "Test transfer transaction", false).join();
        assertTrue(result.isSuccess());
    }

    @Test
    void getRegisteredLanguages() throws IOException {
        MCServerAPI instance = stagingAuth();
        assertNotNull(instance);
        instance.getRegisteredLanguages().join();
    }

    @Test
    void setPreferredLanguage() throws IOException {
        MCServerAPI instance = stagingAuth();
        assertNotNull(instance);
        Boolean result = instance.setPreferredLanguage("7357a549-fa3e-4342-91b2-63e5e73ed39a", "en_us").join();
        assertTrue(result);
    }

    @Test
    void setPreferredLanguageNotRegistered() throws IOException {
        MCServerAPI instance = stagingAuth();
        assertNotNull(instance);
        try {
            instance.setPreferredLanguage("7357a549-fa3e-4342-91b2-63e5e73ed39a", "xyz").join();
            fail("Statement should not be reached.");
        } catch (CompletionException e) {
            APIResponse resp = ResponseEvaluationException.getResponseFromCause(e);
            if (resp != null) {
                JsonObject obj = resp.getErrorAs(JsonObject.class);
                JsonElement msg = obj.get("message");
                assertEquals("That language is not registered.", msg.getAsString());
            } else throw e;
        }
    }

    @Test
    void getPlayersRankedByPoints() throws IOException {
        MCServerAPI instance = stagingAuth();
        assertNotNull(instance);
        instance.getPlayersRankedBy("points", 3).join();
    }

    @Test
    void updateLastPlayed() throws IOException {
        MCServerAPI instance = localAuth();
        assertNotNull(instance);
        MassUpdateResult result = instance.updateLastPlayed("arcadeParty", Arrays.asList(
                "7357a549-fa3e-4342-91b2-63e5e73ed39a",
                "4eb6bcf7-023f-4b57-b0c3-716a9dbba51f"
        )).join();
        assertTrue(result.isSuccess());
    }

    /* */

    @Nullable
    static MCServerAPI localAuth() throws IOException {
        return getAuth("localToken", "http://localhost:8000");
    }

    @Nullable
    static MCServerAPI stagingAuth() throws IOException {
        return getAuth("stagingToken", "https://staging.lclpnet.work");
    }

    @Nullable
    static MCServerAPI getAuth(String tokenKey, String host) throws IOException {
        return getAuth(tokenKey, host, MCServerAPI::new);
    }

    @Nullable
    static <T extends LCLPNetworkAPI > T getAuth(String tokenKey, String host, Function<APIAuthAccess, T> mapper) throws IOException {
        String token = getPrivateProperty(tokenKey);
        if (token == null) return null;

        CompletableFuture<APIAuthAccess> future;
        if (host == null) future = APIAccess.withAuth(token);
        else {
            APIAuthAccess access = new APIAuthAccess(token);
            access.setHost(host);
            future = APIAccess.withAuthCheck(access);
        }

        return future.thenApply(mapper).join();
    }

    @Nullable
    static String getPrivateProperty(String key) throws IOException {
        File f = new File("src/test/resources/test-private.properties");
        Properties privateProps = new Properties();
        try (InputStream in = new FileInputStream(f)) {
            privateProps.load(in);
        } catch (FileNotFoundException e) {
            return null;
        }
        return privateProps.getProperty(key);
    }

}
