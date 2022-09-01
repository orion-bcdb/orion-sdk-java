package org.hyperledger.orion.sdk;


import java.time.Duration;

import com.google.protobuf.util.JsonFormat;

import org.hyperledger.orion.sdk.config.ConnectionConfig;
import org.hyperledger.orion.sdk.config.Replica;
import org.hyperledger.orion.sdk.config.SessionConfig;
import org.hyperledger.orion.sdk.config.UserConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import types.BlockAndTransaction.ValueWithMetadata;

 @TestInstance(Lifecycle.PER_CLASS)
class DataTxTest {
    DBSession session;
    SessionConfig sConfig;

    @BeforeAll
    void setUp() throws Exception {
        String adminCertFilePath = "/Users/senthil/projects/github.ibm.com/orion-sdk-java/src/test/resources/crypto/admin/admin.pem";
        String adminKeyFilePath = "/Users/senthil/projects/github.ibm.com/orion-sdk-java/src/test/resources/crypto/admin/admin.key";

        Replica r = new Replica("node1", "http://127.0.0.1:6001");
        ConnectionConfig cConfig = new ConnectionConfig(new Replica[] { r }, null);
        DB db = new DB(cConfig);

        UserConfig user = new UserConfig("admin", adminCertFilePath, adminKeyFilePath);
        this.sConfig = new SessionConfig(user, Duration.ofSeconds(5), Duration.ofSeconds(5));
        this.session = db.session(sConfig);
    }

    @Test
    void commitWriteOnlyDataTx() throws Exception {
        DataTxContext tx = session.createDataTx();
        tx.put("bdb", "key1", "value1".getBytes(), null);
        var receipt = tx.commit(true);
        String jsonString = JsonFormat.printer()
                // .preservingProtoFieldNames()
                .print(receipt);
        System.out.println(jsonString);
    }

    @Test
    void commitReadWriteDataTx() throws Exception {
        DataTxContext tx = session.createDataTx();

        ValueWithMetadata v = tx.get("bdb", "key1");
        var value = v.getValue().toStringUtf8();
        value = value + "appendedvalue";

        tx.put("bdb", "key1", value.getBytes(), null);

        var receipt = tx.commit(true);
        String jsonString = JsonFormat.printer()
                // .preservingProtoFieldNames()
                .print(receipt);
        System.out.println(jsonString);
    }
}
