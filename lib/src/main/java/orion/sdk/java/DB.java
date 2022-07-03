package orion.sdk.java;

import java.time.Duration;

import orion.sdk.java.config.ConnectionConfig;
import orion.sdk.java.config.SessionConfig;


public class DB implements BCDB {
    ConnectionConfig cConfig;

    public DB(ConnectionConfig cConfig) {
        this.cConfig = cConfig;
    }

    public DBSession session(SessionConfig sConfig) {
        return new Session(sConfig.getUserConfig().getUserID(), this.cConfig.getReplicas(), Duration.ofSeconds(10));
    }
}
