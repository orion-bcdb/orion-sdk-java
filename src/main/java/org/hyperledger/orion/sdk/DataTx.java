package org.hyperledger.orion.sdk;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

import org.hyperledger.orion.sdk.exception.TransactionSpentException;

import types.BlockAndTransaction;
import types.BlockAndTransaction.AccessControl;
import types.BlockAndTransaction.DBOperation;
import types.BlockAndTransaction.DataDelete;
import types.BlockAndTransaction.DataRead;
import types.BlockAndTransaction.DataTxEnvelope;
import types.BlockAndTransaction.DataWrite;
import types.BlockAndTransaction.ValueWithMetadata;
import types.Query.GetDataQuery;
import types.Response.GetDataResponse;
import types.Response.GetDataResponseEnvelope;
import types.Response.TxReceiptResponseEnvelope;

public class DataTx implements DataTxContext {
    CommonTxContext cTxContext;
    Map<String, DBOperations> DBsOperations = new HashMap<String, DBOperations>();
    DataTxEnvelope envelope;
    boolean txSpent;

    public DataTx(CommonTxContext cTxContext) {
        this.cTxContext = cTxContext;
    }

    public void put(String dbName, String key, byte[] value, AccessControl acl) throws Exception {
        if (txSpent) {
            throw new TransactionSpentException(
                    "Transaction is already consumed by either calling commit or abort operation");
        }

        DBOperations dbOps = DBsOperations.get(dbName);
        if (dbOps == null) {
            dbOps = new DBOperations();
            DBsOperations.put(dbName, dbOps);
        }

        dbOps.deleteFromDeletesIfExist(key);

        DataWrite.Builder dataWrite = DataWrite.newBuilder();
        dataWrite.setKey(key);
        dataWrite.setValue(ByteString.copyFrom(value));
        if (acl != null) {
            dataWrite.setAcl(acl);
        }

        dbOps.setDataWrite(key, dataWrite.build());
    }

    public ValueWithMetadata get(String dbName, String key) throws Exception {
        if (txSpent) {
            throw new TransactionSpentException(
                    "Transaction is already consumed by either calling commit or abort operation");
        }

        DBOperations dbOps = DBsOperations.get(dbName);
        if (dbOps != null) {
            var kv = dbOps.getRead(key);
            if (kv != null) {
                ValueWithMetadata.Builder v = ValueWithMetadata.newBuilder();
                v.setValue(kv.getValue());
                v.setMetadata(kv.getMetadata());
                return v.build();
            }
        } else {
            dbOps = new DBOperations();
            DBsOperations.put(dbName, dbOps);
        }

        GetDataQuery.Builder dataQuery = GetDataQuery.newBuilder();
        dataQuery.setDbName(dbName);
        dataQuery.setUserId(cTxContext.userID);
        dataQuery.setKey(key);

        String jsonString = JsonFormat.printer()
                .preservingProtoFieldNames()
                .omittingInsignificantWhitespace()
                .print(dataQuery.build());
        System.out.println(jsonString);
        var sig = cTxContext.crypto.sign(cTxContext.privateKey, jsonString.getBytes());

        GetDataResponseEnvelope.Builder resp = GetDataResponseEnvelope.newBuilder();

        cTxContext.handleGetPostRequest("/data/" + dbName + "/" + key, "GET", sig, resp);
        var kv = resp.build();
        dbOps.setDataRead(key, kv.getResponse());

        ValueWithMetadata.Builder v = ValueWithMetadata.newBuilder();
        v.setValue(kv.getResponse().getValue());
        v.setMetadata(kv.getResponse().getMetadata());
        return v.build();
    }

    public void delete(String dbName, String key) {

    }

    // Commit submits transaction to the server, can be sync or async.
    // Sync option returns tx id and tx receipt envelope and
    // in case of error, commitTimeout error is one of possible errors to return.
    // Async returns tx id, always nil as tx receipt or error
    public TxReceiptResponseEnvelope commit(boolean sync) throws Exception {
        DataTxEnvelope.Builder txEnvelope = DataTxEnvelope.newBuilder();

        BlockAndTransaction.DataTx.Builder dataTx = BlockAndTransaction.DataTx.newBuilder();

        Iterator<Map.Entry<String, DBOperations>> dbItr = DBsOperations.entrySet().iterator();
        while (dbItr.hasNext()) {
            Map.Entry<String, DBOperations> entry = dbItr.next();
            var dbName = entry.getKey();

            DBOperation.Builder dbOps = DBOperation.newBuilder();
            dbOps.setDbName(dbName);

            Iterator<Map.Entry<String, GetDataResponse>> readItr = entry.getValue().dataReads.entrySet().iterator();
            while (readItr.hasNext()) {
                Map.Entry<String, GetDataResponse> readEntry = readItr.next();
                DataRead.Builder read = DataRead.newBuilder();
                read.setKey(readEntry.getKey());
                read.setVersion(readEntry.getValue().getMetadata().getVersion());
                dbOps.addDataReads(read.build());
            }

            Iterator<Map.Entry<String, DataWrite>> writeItr = entry.getValue().dataWrites.entrySet().iterator();
            while (writeItr.hasNext()) {
                Map.Entry<String, DataWrite> writeEntry = writeItr.next();
                dbOps.addDataWrites(writeEntry.getValue());
            }

            Iterator<Map.Entry<String, DataDelete>> deleteItr = entry.getValue().dataDeletes.entrySet().iterator();
            while (deleteItr.hasNext()) {
                Map.Entry<String, DataDelete> deleteEntry = deleteItr.next();
                dbOps.addDataDeletes(deleteEntry.getValue());
            }

            dataTx.addDbOperations(dbOps.build());
        }

        dataTx.setTxId(cTxContext.txID);
        dataTx.addMustSignUserIds(cTxContext.userID);

        var txPayload = dataTx.build();
        txEnvelope.setPayload(txPayload);

        String jsonString = JsonFormat.printer()
                .preservingProtoFieldNames()
                .omittingInsignificantWhitespace()
                .print(txPayload);

        var sig = cTxContext.crypto.sign(cTxContext.privateKey, jsonString.getBytes());
        txEnvelope.putSignatures(cTxContext.userID, ByteString.copyFrom(sig));

        envelope = txEnvelope.build();

        return cTxContext.commit("/data/tx", true, envelope);
    }

    // Abort cancel submission and abandon all changes
    // within given transaction context
    public void abort() {

    }

    // CommittedTxEnvelope returns transaction envelope, can be called only after
    // Commit(), otherwise will return nil
    public Message committedTxEnvelope() {
        return envelope;
    }
}
