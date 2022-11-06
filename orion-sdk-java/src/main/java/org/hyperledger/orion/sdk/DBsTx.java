package org.hyperledger.orion.sdk;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

import org.hyperledger.orion.sdk.exception.TransactionSpentException;

import types.BlockAndTransaction.DBAdministrationTx;
import types.BlockAndTransaction.DBAdministrationTxEnvelope;
import types.BlockAndTransaction.DBIndex;
import types.BlockAndTransaction.IndexAttributeType;
import types.Query.GetDBIndexQuery;
import types.Query.GetDBStatusQuery;
import types.Response.GetDBIndexResponseEnvelope;
import types.Response.GetDBStatusResponseEnvelope;
import types.Response.TxReceiptResponseEnvelope;

public class DBsTx implements DBsTxContex {
    CommonTxContext cTxContext;
    HashMap<String, DBIndex> createDBs;
    HashMap<String, Boolean> deleteDBs;
    DBAdministrationTxEnvelope envelope;
    boolean txSpent;

    public DBsTx(CommonTxContext cTxContext) {
        this.cTxContext = cTxContext;
        createDBs = new HashMap<String, DBIndex>();
        deleteDBs = new HashMap<String, Boolean>();
    }

    public void createDB(String dbName, HashMap<String, IndexAttributeType> index) throws Exception {
        if (txSpent) {
            throw new TransactionSpentException(
                    "Transaction is already consumed by either calling commit or abort operation");
        }

        if (index == null) {
            createDBs.put(dbName, null);
            return;
        }

        DBIndex.Builder idx = DBIndex.newBuilder();
        for (Map.Entry<String, IndexAttributeType> entry : index.entrySet())
            idx.putAttributeAndType(entry.getKey(), entry.getValue());
        createDBs.put(dbName, idx.build());
    }

    public void deleteDB(String dbName) throws Exception {
        if (txSpent) {
            throw new TransactionSpentException(
                    "Transaction is already consumed by either calling commit or abort operation");
        }

        deleteDBs.put(dbName, true);
    }

    public boolean exists(String dbName) throws Exception {
        if (txSpent) {
            throw new TransactionSpentException(
                    "Transaction is already consumed by either calling commit or abort operation");
        }

        GetDBStatusQuery.Builder q = GetDBStatusQuery.newBuilder();
        q.setUserId(cTxContext.userID);
        q.setDbName(dbName);

        String jsonString = JsonFormat.printer()
                .preservingProtoFieldNames()
                .omittingInsignificantWhitespace()
                .print(q.build());
        System.out.println(jsonString);
        var sig = cTxContext.crypto.sign(cTxContext.privateKey, jsonString.getBytes());

        GetDBStatusResponseEnvelope.Builder resp = GetDBStatusResponseEnvelope.newBuilder();
        cTxContext.handleGetPostRequest("/db/" + dbName, "GET", sig, resp);

        return resp.getResponse().getExist();
    }

    public HashMap<String, IndexAttributeType> getDBIndex(String dbName) throws Exception {
        if (txSpent) {
            throw new TransactionSpentException(
                    "Transaction is already consumed by either calling commit or abort operation");
        }

        GetDBIndexQuery.Builder q = GetDBIndexQuery.newBuilder();
        q.setUserId(cTxContext.userID);
        q.setDbName(dbName);

        String jsonString = JsonFormat.printer()
            .preservingProtoFieldNames()
            .omittingInsignificantWhitespace()
            .print(q.build());

        var sig = cTxContext.crypto.sign(cTxContext.privateKey, jsonString.getBytes());

        GetDBIndexResponseEnvelope.Builder resp = GetDBIndexResponseEnvelope.newBuilder();
        cTxContext.handleGetPostRequest("/db/index/" + dbName, "GET", sig, resp);

        var indexResp = resp.build().getResponse().getIndex();
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, IndexAttributeType> index = mapper.readValue(indexResp, HashMap.class);

        return index;

    }

    public TxReceiptResponseEnvelope commit(boolean sync) throws Exception {
        DBAdministrationTxEnvelope.Builder txEnvelope = DBAdministrationTxEnvelope.newBuilder();

        DBAdministrationTx.Builder tx = DBAdministrationTx.newBuilder();
        tx.setUserId(cTxContext.userID);
        tx.setTxId(cTxContext.txID);

        var dbsIndex = tx.getDbsIndex();

        for (Map.Entry<String, DBIndex> entry : createDBs.entrySet()) {
            tx.addCreateDbs(entry.getKey());
            if (entry.getValue() != null) {
                dbsIndex.put(entry.getKey(), entry.getValue());
            }
        }

        for (Map.Entry<String, Boolean> entry : deleteDBs.entrySet()) {
            tx.addDeleteDbs(entry.getKey());
        }

        var txPayload = tx.build();
        txEnvelope.setPayload(txPayload);

        String jsonString = JsonFormat.printer()
            .preservingProtoFieldNames()
            .omittingInsignificantWhitespace()
            .print(txPayload);

        var sig = cTxContext.crypto.sign(cTxContext.privateKey, jsonString.getBytes());
        txEnvelope.setSignature(ByteString.copyFrom(sig));

        envelope = txEnvelope.build();

        return cTxContext.commit("/db/tx", sync, envelope);
    }

    public void abort() {

    }

    public Message committedTxEnvelope() {
        return envelope;
    }
}


