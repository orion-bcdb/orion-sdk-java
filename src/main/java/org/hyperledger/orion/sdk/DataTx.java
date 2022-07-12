package org.hyperledger.orion.sdk;

import java.util.Map;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;

import org.hyperledger.orion.sdk.exception.TransactionSpentException;

import types.BlockAndTransaction.AccessControl;
import types.BlockAndTransaction.DataTxEnvelope;
import types.BlockAndTransaction.DataWrite;
import types.BlockAndTransaction.ValueWithMetadata;
import types.Response.TxReceiptResponseEnvelope;

public class DataTx implements DataTxContext {
    CommonTxContext cTxContext;
    Map<String, DBOperations> DBsOperations;
    boolean txSpent;

    public void put(String dbName, String key, byte[] value, AccessControl acl) throws Exception {
        if (txSpent) {
            throw new TransactionSpentException("Transaction is already consumed by either calling commit or abort operation");
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
        dataWrite.setAcl(acl);

        dbOps.setDataWrite(key, dataWrite.build());
    }

    public ValueWithMetadata get(String dbName, String key) throws Exception {
        if (txSpent) {
            throw new TransactionSpentException("Transaction is already consumed by either calling commit or abort operation");
        }

        ValueWithMetadata.Builder v = ValueWithMetadata.newBuilder();
        return v.build();
    }

    public void delete(String dbName, String key) {

    }

	// Commit submits transaction to the server, can be sync or async.
	// Sync option returns tx id and tx receipt envelope and
	// in case of error, commitTimeout error is one of possible errors to return.
	// Async returns tx id, always nil as tx receipt or error
    public TxReceiptResponseEnvelope commit(boolean sync) {
        TxReceiptResponseEnvelope.Builder txReceipt = TxReceiptResponseEnvelope.newBuilder();
        return txReceipt.build();
    }

	// Abort cancel submission and abandon all changes
	// within given transaction context
    public void abort() {

    }

	// CommittedTxEnvelope returns transaction envelope, can be called only after Commit(), otherwise will return nil
    public Message committedTxEnvelope() {
        DataTxEnvelope.Builder tx = DataTxEnvelope.newBuilder();
        return tx.build();
    }
}
