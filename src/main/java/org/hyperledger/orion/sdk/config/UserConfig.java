package org.hyperledger.orion.sdk.config;

public class UserConfig {
    String userID;
    String certPath;
    String privateKeyPath;

    public UserConfig(String userID, String certPath, String privateKeyPath) {
        this.userID = userID;
        this.certPath = certPath;
        this.privateKeyPath = privateKeyPath;
    }

    public String getUserID() {
        return this.userID;
    }

    public String getCertPath() {
        return this.certPath;
    }

    public String getPrivateKeyPath() {
        return this.privateKeyPath;
    }
}
