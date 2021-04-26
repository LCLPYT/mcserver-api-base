/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.test;

import org.junit.jupiter.api.Test;
import work.lclpnet.lclpnetwork.LCLPNetworkAPI;
import work.lclpnet.lclpnetwork.api.APIAccess;
import work.lclpnet.lclpnetwork.api.APIAuthAccess;
import work.lclpnet.lclpnetwork.api.APIException;
import work.lclpnet.serverapi.MCServerAPI;

import javax.annotation.Nullable;
import java.io.*;
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
        MCServerAPI instance = getAuth();
        assertNotNull(instance);

        Boolean operator = instance.isNetworkOperator("7357a549-fa3e-4342-91b2-63e5e73ed39a").join();
        assertNotNull(operator);
        assertTrue(operator);
    }

    /**
     * This test currently only works when there is a development server of LCLPNetwork running on http://localhost:8000.
     * TODO: Update this when there is a staging server.
     * @throws IOException If there was an auth error.
     */
    @Test
    void updateLastSeen() throws IOException {
        MCServerAPI instance = getAuth("localToken", "http://localhost:8000");
        assertNotNull(instance);
        Boolean success = instance.updateLastSeen("7357a549-fa3e-4342-91b2-63e5e73ed39a").join();
        assertTrue(success);
    }

    /*@Test
    void processMCLinkToken() throws IOException {
        MCServerAPI instance = getAuth("localToken", "http://localhost:8000");
        assertNotNull(instance);
        String token = "27d23437-6ac0-438a-94b5-184d69ed1c99";
        Boolean success = instance.processMCLinkToken("7357a549-fa3e-4342-91b2-63e5e73ed39a", token).join();
        assertTrue(success);
    }*/

    /*@Test
    void requestMCLinkReverseToken() throws IOException {
        MCServerAPI instance = getAuth("localToken", "http://localhost:8000");
        assertNotNull(instance);
        String token = instance.requestMCLinkReverseToken("7357a549-fa3e-4342-91b2-63e5e73ed39a").join();
        assertNotNull(token);
    }*/

    /* */

    @Nullable
    static MCServerAPI getAuth() throws IOException {
        return getAuth("token", null);
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
