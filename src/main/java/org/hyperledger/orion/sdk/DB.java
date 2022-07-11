package org.hyperledger.orion.sdk;

import java.time.Duration;

import org.hyperledger.orion.sdk.config.ConnectionConfig;
import org.hyperledger.orion.sdk.config.SessionConfig;


public class DB implements BCDB {
    ConnectionConfig cConfig;

    public DB(ConnectionConfig cConfig) {
        this.cConfig = cConfig;
    }

    public DBSession session(SessionConfig sConfig) {
        return new Session(sConfig.getUserConfig().getUserID(), this.cConfig.getReplicas(), Duration.ofSeconds(10));
    }
}
