package org.hyperledger.orion.sdk;

import java.io.File;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.time.Duration;

import org.bouncycastle.util.encoders.Hex;
import org.hyperledger.orion.sdk.config.Replica;
import org.hyperledger.orion.sdk.config.SessionConfig;
import org.hyperledger.orion.sdk.security.CryptoPrimitives;

public class Session implements DBSession {
    String userID;
    PrivateKey privateKey;
    CryptoPrimitives crypto;
    Replica[] replicas;
    Duration queryTimeout;
    Duration txTimeout;

    public Session(SessionConfig sConfig, Replica[] replicas, CryptoPrimitives crypto) throws Exception {
        this.userID = sConfig.getUserConfig().getUserID();
        this.replicas = replicas;
        this.crypto = crypto;
        this.queryTimeout = sConfig.getQueryTimeout();
        this.txTimeout = sConfig.getTxTimeout();

        File f = new File(sConfig.getUserConfig().getPrivateKeyPath());
        var key = Files.readAllBytes(f.toPath());
        this.privateKey = crypto.bytesToPrivateKey(key);
    }

    public DataTxContext createDataTx() throws Exception {
        return new DataTx(getCommonTxContext());
    }

    public DataTxContext createDataTx(String txID) {
        CommonTxContext cTxContext = new CommonTxContext(userID, replicas, txID, privateKey, crypto);
        return new DataTx(cTxContext);
    }

    public DBsTxContex createDBsTx() throws Exception {
        return new DBsTx(getCommonTxContext());
    }

    CommonTxContext getCommonTxContext() throws Exception {
        String txID;
        byte[] nonce = new byte[24];
        new SecureRandom().nextBytes(nonce);

        MessageDigest digest = MessageDigest.getInstance("SHA256");
        byte[] hash = digest.digest(nonce);
        txID = new String(Hex.encode(hash));

        return new CommonTxContext(userID, replicas, txID, privateKey, crypto);
    }
}
