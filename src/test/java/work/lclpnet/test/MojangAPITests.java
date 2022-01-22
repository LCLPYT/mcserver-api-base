/*
 * Copyright (c) 2022 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.test;

import org.junit.jupiter.api.Test;
import work.lclpnet.serverapi.util.MojangAPI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MojangAPITests {

    /**
     * This test will break, should LCLP ever change their name.
     */
    @Test
    void uuidByUsername() {
        String uuid = MojangAPI.getUUIDByUsername("LCLP").join();
        assertEquals("7357a549-fa3e-4342-91b2-63e5e73ed39a", uuid);
    }

    /**
     * This test will break, should anyone create an account with that name.
     */
    @Test
    void uuidByUsernameMissing() {
        String uuid = MojangAPI.getUsernameByUUID("n34b2nxyuln658").join();
        assertNull(uuid);
    }

    /**
     * This test will break, should LCLP ever change their name.
     */
    @Test
    void usernameByUuid() {
        String name = MojangAPI.getUsernameByUUID("7357a549-fa3e-4342-91b2-63e5e73ed39a").join();
        assertEquals("LCLP", name);
    }

    /**
     * This test will break, if there is ever a Minecraft account with that UUID.
     */
    @Test
    void usernameByUuidMissing() {
        String name = MojangAPI.getUsernameByUUID("014c30a3-4924-4731-ac59-7a68a5947761").join();
        assertNull(name);
    }

}
