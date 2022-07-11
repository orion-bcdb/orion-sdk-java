package org.hyperledger.orion.sdk;

import com.google.protobuf.Message;

import types.BlockAndTransaction.AccessControl;
import types.BlockAndTransaction.DataTxEnvelope;
import types.BlockAndTransaction.ValueWithMetadata;
import types.Response.TxReceiptResponseEnvelope;

public class DataTx implements DataTxContext {
    CommonTxContext cTxContext;

    public void put(String dbName, String key, byte[] value, AccessControl acl) {

    }

    public ValueWithMetadata get(String dbName, String key) {
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
