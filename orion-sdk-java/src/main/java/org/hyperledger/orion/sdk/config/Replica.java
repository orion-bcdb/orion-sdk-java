package org.hyperledger.orion.sdk.config;

public class Replica {
    String id;
    String endpoint;

    public Replica(String id, String endpoint) {
        this.id = id;
        this.endpoint = endpoint;
    }

    public String getID() {
        return this.id;
    }

    public String getEndpoint() {
        return this.endpoint;
    }
}
