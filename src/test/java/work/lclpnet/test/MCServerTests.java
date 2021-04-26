/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.test;

import org.junit.jupiter.api.Test;
import work.lclpnet.lclpnetwork.api.APIAccess;
import work.lclpnet.lclpnetwork.api.APIAuthAccess;
import work.lclpnet.lclpnetwork.api.APIException;
import work.lclpnet.serverapi.MCServerAPI;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.CompletionException;

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

    /* */

    private MCServerAPI getAuth() throws IOException {
        String token = getPrivateProperty("token");
        if(token == null) return null;
        APIAuthAccess access = APIAccess.withAuth(token).join();
        return new MCServerAPI(access);
    }

    private String getPrivateProperty(String key) throws IOException {
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
