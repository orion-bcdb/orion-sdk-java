package orion.sdk.java;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Duration;

import com.google.protobuf.ByteString;
import com.google.protobuf.util.JsonFormat;

import org.junit.jupiter.api.Test;

import orion.sdk.java.crypto.Signer;
import types.BlockAndTransaction.DBOperation;
import types.BlockAndTransaction.DataTx;
import types.BlockAndTransaction.DataTxEnvelope;
import types.BlockAndTransaction.DataWrite;

class HTTPRestClientTest {
    @Test
    void query() {
        HTTPRestClient client = new HTTPRestClient(
                "user1",
                HttpClient.newHttpClient(),
                Duration.ofSeconds(1),
                Duration.ofSeconds(2));
        try {
            var resp = client.Query("http://127.0.0.1:6001/db/bdb", "GET", null, "sign".getBytes());
            System.out.println(resp);
        } catch (IOException e) {
            System.out.println("error IO: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("error ie");
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
                .print(txPayload);

            var sig = signer.Sign(jsonString.getBytes());
            dataTxEnv.putSignatures("admin", ByteString.copyFrom(sig));
        } catch (IOException e) {
            System.out.println("ERROR" + e.toString());
        }

        try {
            var resp = client.Submit("http://127.0.0.1:6001/data/tx", dataTxEnv.build());
            System.out.println(resp);
        } catch (IOException e) {
            System.out.println("error IO: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("error ie");
        }
    }
}
