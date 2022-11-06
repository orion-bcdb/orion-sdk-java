package org.hyperledger.orion.sdk;

import java.net.http.HttpClient;
import java.security.PrivateKey;
import java.time.Duration;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

import org.hyperledger.orion.sdk.config.Replica;
import org.hyperledger.orion.sdk.security.CryptoPrimitives;

import types.BlockAndTransaction.DataTxEnvelope;
import types.Response.TxReceiptResponseEnvelope;

public class CommonTxContext {
    String userID;
    PrivateKey privateKey;
    CryptoPrimitives crypto;
    String txID;
    Replica[] replicas;
    HTTPRestClient client;

    public CommonTxContext(String userID, Replica[] replicas, String txID, PrivateKey privateKey, CryptoPrimitives crypto) {
        this.userID = userID;
        this.privateKey = privateKey;
        this.crypto = crypto;
        this.txID = txID;
        this.replicas = replicas;
        this.client = new HTTPRestClient(
                userID,
                HttpClient.newHttpClient(),
                Duration.ofSeconds(1),
                Duration.ofSeconds(2));
    }

    // Commit submits transaction to the server, can be sync or async.
    // Sync option returns tx id and tx receipt envelope and
    // in case of error, commitTimeout error is one of possible errors to return.
    // Async returns tx id, always nil as tx receipt or error
    public TxReceiptResponseEnvelope commit(String postEndPoint, boolean sync, Message txEnvelope) throws Exception {
        String restPath = selectReplica() + postEndPoint;
        var httpResp = client.Submit(restPath, txEnvelope);
        if (httpResp.statusCode() != 200) {
            System.out.println(httpResp.toString());
            throw new Exception();
        }

        TxReceiptResponseEnvelope.Builder receipt = TxReceiptResponseEnvelope.newBuilder();
        JsonFormat.parser().merge(httpResp.body(), receipt);
        return receipt.build();
    }

    // Abort cancel submission and abandon all changes
    // within given transaction context
    public void abort() {

    }

    // CommittedTxEnvelope returns transaction envelope, can be called only after
    // Commit(), otherwise will return nil
    public Message committedTxEnvelope() {
        DataTxEnvelope.Builder tx = DataTxEnvelope.newBuilder();
        return tx.build();
    }

    public void handleGetPostRequest(String partURL, String method, byte[] sign, Message.Builder resp) throws Exception {
        String restPath = selectReplica() + partURL;
        System.out.println(restPath);
        var httpResp = client.Query(restPath, method, null, sign);
        if (httpResp.statusCode() != 200) {
            System.out.println(resp.toString());
            return;
        }
        JsonFormat.parser().merge(httpResp.body(), resp);
    }

    private String selectReplica() throws Exception {
        for (Replica s: this.replicas) {
            return s.getEndpoint();
        }

        throw new Exception();
    }
}
