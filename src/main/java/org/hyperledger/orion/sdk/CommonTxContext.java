package org.hyperledger.orion.sdk;

import java.time.Duration;

import com.google.protobuf.Message;

import org.hyperledger.orion.sdk.config.Replica;
import types.BlockAndTransaction.DataTxEnvelope;
import types.Response.TxReceiptResponseEnvelope;

public class CommonTxContext implements TxContext {
    String userID;
    String txID;
    Replica[] replicas;

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
