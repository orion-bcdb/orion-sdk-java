package org.hyperledger.orion.sdk;

import java.io.File;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.security.PrivateKey;
import java.time.Duration;

import com.google.protobuf.ByteString;
import com.google.protobuf.util.JsonFormat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.hyperledger.orion.sdk.crypto.Signer;
import org.hyperledger.orion.sdk.security.CryptoPrimitives;

import types.BlockAndTransaction.DBOperation;
import types.BlockAndTransaction.DataTx;
import types.BlockAndTransaction.DataTxEnvelope;
import types.BlockAndTransaction.DataWrite;
import types.Query.GetDBStatusQuery;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HTTPRestClientTest {
    CryptoPrimitives c;
    PrivateKey adminPrivateKey;

    @BeforeAll
    public void setUp() {
        try {
        c = new CryptoPrimitives();
        c.init();
        var adminKeyFilePath = "/Users/senthil/projects/github.ibm.com/orion-sdk-java/src/test/resources/crypto/admin/admin.key";

        File file = new File(adminKeyFilePath);
        var key = Files.readAllBytes(file.toPath());
        adminPrivateKey = c.bytesToPrivateKey(key);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    @Test
    void query() {
        HTTPRestClient client = new HTTPRestClient(
                "admin",
                HttpClient.newHttpClient(),
                Duration.ofSeconds(1),
                Duration.ofSeconds(2));
        try {
            GetDBStatusQuery.Builder dbStatus = GetDBStatusQuery.newBuilder();
            dbStatus.setDbName("bdb");
            dbStatus.setUserId("admin");

            String jsonString = JsonFormat.printer()
                    .preservingProtoFieldNames()
                    .omittingInsignificantWhitespace()
                    .print(dbStatus.build());

            var sig = c.sign(adminPrivateKey, jsonString.getBytes());
            var resp = client.Query("http://127.0.0.1:6001/db/bdb", "GET", null, sig);
            System.out.println(resp);
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
    }

    @Test
    void submit() {
        HTTPRestClient client = new HTTPRestClient(
                "user1",
                HttpClient.newHttpClient(),
                Duration.ofSeconds(1),
                Duration.ofSeconds(2));
        DataTxEnvelope.Builder dataTxEnv = DataTxEnvelope.newBuilder();

        DataWrite.Builder dWrite = DataWrite.newBuilder();
        dWrite.setKey("key1");

        DBOperation.Builder dbOps = DBOperation.newBuilder();
        dbOps.setDbName("bdb");
        dbOps.addDataWrites(dWrite.build());

        DataTx.Builder dataTx = DataTx.newBuilder();
        dataTx.setTxId("tx1");
        dataTx.addMustSignUserIds("admin");
        dataTx.addDbOperations(dbOps.build());
        var txPayload = dataTx.build();
        dataTxEnv.setPayload(txPayload);

        try {
            Signer signer = new Signer("admin", "/Users/senthil/projects/github.ibm.com/orion-sdk-java/admin.key");

            String jsonString = JsonFormat.printer()
                    .preservingProtoFieldNames()
                    .omittingInsignificantWhitespace()
                    .print(txPayload);

            var sig = signer.Sign(jsonString.getBytes());
            dataTxEnv.putSignatures("admin", ByteString.copyFrom(sig));

            var resp = client.Submit("http://127.0.0.1:6001/data/tx", dataTxEnv.build());
            System.out.println(resp);
        } catch (Exception e) {
            System.out.println("ERROR" + e.toString());
        }
    }
}
