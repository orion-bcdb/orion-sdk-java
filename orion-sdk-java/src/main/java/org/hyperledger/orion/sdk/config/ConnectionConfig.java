package org.hyperledger.orion.sdk.config;

public class ConnectionConfig {
    Replica[] replicas;
    String[] rootCAs;

    public ConnectionConfig(Replica []replicas, String []rootCAs) {
        this.replicas = replicas;
        this.rootCAs = rootCAs;
    }

    public Replica[] getReplicas() {
        return this.replicas;
    }

    public String[] rootCAs() {
        return this.rootCAs();
    }
}
