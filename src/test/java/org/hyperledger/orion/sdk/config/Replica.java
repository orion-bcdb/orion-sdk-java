package org.hyperledger.orion.sdk.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReplicaTest {
    @Test void CheckGetterAndSetterMethod() {
        Replica r = new Replica("node1", "http://127.0.0.1:6001");
        assertEquals("node1", r.getID());
        assertEquals("http://127.0.0.1:6001", r.getEndpoint());
    }
}
