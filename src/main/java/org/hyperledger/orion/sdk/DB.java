package org.hyperledger.orion.sdk;

import java.net.URL;
import java.util.Map;

import org.hyperledger.orion.sdk.config.ConnectionConfig;
import org.hyperledger.orion.sdk.config.Replica;
import org.hyperledger.orion.sdk.config.SessionConfig;
import org.hyperledger.orion.sdk.security.CryptoPrimitives;


public class DB implements BCDB {
    ConnectionConfig cConfig;
    CryptoPrimitives crypto;
    Map<String, URL> bootstrapReplica;

    public DB(ConnectionConfig cConfig) throws Exception {
        this.cConfig = cConfig;
        this.crypto = new CryptoPrimitives();
        crypto.init();

        for (Replica r: cConfig.getReplicas()) {
            this.bootstrapReplica.put(r.getID(), new URL(r.getEndpoint()));
        }
    }

    public DBSession session(SessionConfig sConfig) throws Exception {
        return new Session(sConfig, this.cConfig.getReplicas(), crypto);
    }
}
